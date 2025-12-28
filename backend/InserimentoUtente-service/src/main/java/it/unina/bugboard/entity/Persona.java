package it.unina.bugboard.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

@MappedSuperclass
public abstract class Persona {
    @Column(nullable = false, name = "nome")
    private String nome;
    @Column(nullable = false, name = "cognome")
    private String cognome;
    @Column(nullable = false, unique = true, name = "cf")
    private String codiceFiscale;
    @Column(nullable = false, name = "sesso")
    private char sesso;
    @Column(nullable = false, name = "datadinascita")
    private LocalDate dataNascita;
}
