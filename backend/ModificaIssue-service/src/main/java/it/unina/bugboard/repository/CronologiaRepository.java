package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Cronologia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CronologiaRepository extends JpaRepository<Cronologia, Long> {
}
