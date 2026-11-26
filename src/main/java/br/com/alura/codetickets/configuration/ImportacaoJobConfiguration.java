package br.com.alura.codetickets.configuration;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportacaoJobConfiguration {
    @Bean
    public Job takeJob(Step passoInicial, JobRepository jobRepository){
        return new JobBuilder("geracao-tickets", jobRepository)
                .start(passoInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
