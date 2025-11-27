package com.example.projectbugboard26.repository;

import com.example.projectbugboard26.Entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface repositoryUtente extends JpaRepository<Utente,Long> {

    Optional<Utente> findByUsername(String username);
}