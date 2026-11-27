package br.com.alura.codetickets.utils;

import br.com.alura.codetickets.models.Importacao;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportacaoMapper implements FieldSetMapper<Importacao> {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public Importacao mapFieldSet(FieldSet fieldSet) throws BindException {
        Importacao importacao = new Importacao();

        importacao.setCpf(fieldSet.readString("cpf"));
        importacao.setCliente(fieldSet.readString("cliente"));
        importacao.setDataNascimento(LocalDate.parse(fieldSet.readString("dataNascimento"), dateFormatter));
        importacao.setEvento(fieldSet.readString("evento"));
        importacao.setDataEvento(LocalDate.parse(fieldSet.readString("dataEvento"), dateFormatter));
        importacao.setTipoIngresso(fieldSet.readString("tipoIngresso"));
        importacao.setValorIngresso(fieldSet.readDouble("valorIngresso"));
        importacao.setHoraImportacao(LocalDateTime.now());

        return importacao;
    }
}
