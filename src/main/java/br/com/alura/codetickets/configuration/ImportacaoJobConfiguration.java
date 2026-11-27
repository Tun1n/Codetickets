package br.com.alura.codetickets.configuration;

import br.com.alura.codetickets.models.Importacao;
import br.com.alura.codetickets.utils.ImportacaoMapper;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ImportacaoJobConfiguration {
    int QUANTIDADE_ITENS_PROCESSA_POR_TRANSACTION = 200; // Chunk Size
    String CAMINHO_ARQUIVO = "files/"; // Caminho onde ficam os arquivos a serem lidos

    @Bean
    public Job takeJob(Step passoInicial, JobRepository jobRepository){
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }
    @Bean
    public Step passoInicial(ItemReader<Importacao> reader,
                             ItemWriter<Importacao> writer,
                             JobRepository jobRepository,
                             PlatformTransactionManager transactionManager){
        return new StepBuilder("passo-inicial", jobRepository)
                .<Importacao, Importacao>chunk(QUANTIDADE_ITENS_PROCESSA_POR_TRANSACTION)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> getItemReader(){
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv") // Nome da instância do Reader
                .resource(new FileSystemResource(CAMINHO_ARQUIVO + "dados.csv")) // Diz o caminho do arquivo para o reader
                .linesToSkip(1)
                .comments("--") // Caso encontre uma linha do arquivos com -- , irá ser ignorado pois se trata como comentári
                .delimited() // Indica que o arquivo possui um delimitador
                .delimiter(";") // Indica o delimitador

                // Indica as colunas e a ordem a ser lida no arquivos (deve ser o mesmo nome da classe de referência, no caso Importacao)
                .names("cpf", "cliente", "dataNascimento", "evento",
                        "dataEvento", "tipoIngresso", "valorIngresso")
                .fieldSetMapper(new ImportacaoMapper())
                .build();
    }

    @Bean
    public ItemWriter<Importacao> getItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Importacao>()
                .dataSource(dataSource)
                .sql(
                        "INSERT INTO IMPORTACAO (cpf, cliente, data_nascimento, evento, data_evento, " +
                                "tipo_ingresso, valor_ingresso, hora_importacao) " +
                                "VALUES (:cpf, :cliente, :dataNascimento, :evento, :dataEvento, " +
                                ":tipoIngresso, :valorIngresso, :horaImportacao)"
                )
                .itemSqlParameterSourceProvider( // Transforma cada item em parâmetros SQL
                        // Transformar os getters do objeto em propriedade
                        new BeanPropertyItemSqlParameterSourceProvider<>()
                )
                .build();
    }
}
