package br.com.alura.codetickets.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "importacao")
@Getter
@Setter
public class Importacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpf;
    private String cliente;
    private LocalDate dataNascimento;
    private String evento;
    private LocalDate dataEvento;
    private String tipoIngresso;
    private Double valorIngresso;
    private LocalDateTime horaImportacao;

}
