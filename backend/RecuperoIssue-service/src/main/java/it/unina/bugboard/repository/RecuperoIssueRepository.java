package it.unina.bugboard.repository;

import it.unina.bugboard.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecuperoIssueRepository extends JpaRepository<Issue, Integer> {

    @Query("SELECT DISTINCT i FROM Issue i WHERE i.idProgetto = :idProgetto AND i.stato != 'CLOSED'")
    org.springframework.data.domain.Page<Issue> findByIdProgettoAndStatoNot(@Param("idProgetto") int idProgetto,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT i FROM Issue i WHERE i.idAssegnatario = :idAssegnatario AND i.stato != 'CLOSED'")
    org.springframework.data.domain.Page<Issue> findByIdAssegnatarioAndStatoNot(
            @Param("idAssegnatario") int idAssegnatario, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT i FROM Issue i WHERE i.idProgetto = :idProgetto AND i.idAssegnatario = :idAssegnatario AND i.stato != 'CLOSED'")
    org.springframework.data.domain.Page<Issue> findByIdProgettoAndIdAssegnatarioAndStatoNot(
            @Param("idProgetto") int idProgetto,
            @Param("idAssegnatario") int idAssegnatario,
            org.springframework.data.domain.Pageable pageable);
}
