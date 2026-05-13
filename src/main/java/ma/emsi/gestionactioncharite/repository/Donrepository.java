package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.StatutDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Donrepository extends JpaRepository<Don, Long> {
    List<Don> findByUserId(Long userId);

    List<Don> findByActionChariteId(Long actionId);

    List<Don> findByStatus(StatutDon statut);

    long countByActionChariteOrganisationIdAndStatus(Long organisationId, StatutDon status);

    List<Don> findTop5ByActionChariteOrganisationIdOrderByDateDonDescIdDesc(Long organisationId);

    @Query("""
            select coalesce(sum(d.montant), 0)
            from Don d
            where d.actionCharite.organisation.id = :organisationId
            and d.status = :status
            """)
    Double sumMontantByOrganisationIdAndStatus(@Param("organisationId") Long organisationId,
                                               @Param("status") StatutDon status);

    @Query("select coalesce(sum(d.montant), 0) from Don d where d.status = :status")
    Double sumAllMontantByStatus(@Param("status") StatutDon status);

    long countByStatus(StatutDon status);
}
