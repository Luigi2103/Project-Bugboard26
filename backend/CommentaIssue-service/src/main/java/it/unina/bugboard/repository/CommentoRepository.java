package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Commento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentoRepository extends JpaRepository<Commento, Long> {
}
