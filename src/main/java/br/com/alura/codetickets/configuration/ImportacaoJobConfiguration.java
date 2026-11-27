package br.com.alura.codetickets.configuration;

import br.com.alura.codetickets.models.Importacao;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

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
                             JobRepository jobRepository){
        return new StepBuilder("passo-inicial", jobRepository)
                .<Importacao, Importacao>chunk(QUANTIDADE_ITENS_PROCESSA_POR_TRANSACTION)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Importacao> getItemReader(){
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv") // Nome da instância do Reader
                .resource(new FileSystemResource(CAMINHO_ARQUIVO + "dados.csv")) // Diz o caminho do arquivo para o reader
                .comments("--") // Caso encontre uma linha do arquivos com -- , irá ser ignorado pois se trata como comentári
                .delimited()

                // Indica as colunas e a ordem a ser lida no arquivos (deve ser o mesmo nome da classe de referência, no caso Importacao)
                .names("cpf", "cliente", "dataNascimento", "evento",
                        "dataEvento", "tipoIngresso", "valorIngresso")
                .targetType(Importacao.class) // Indica o tipo que o arquivo será convertido
                .build();
    }
}
