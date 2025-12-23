package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Cronologia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CronologiaRepository extends JpaRepository<Cronologia, Long> {
    List<Cronologia> findByIdIssueOrderByDataAsc(Integer idIssue);
}
