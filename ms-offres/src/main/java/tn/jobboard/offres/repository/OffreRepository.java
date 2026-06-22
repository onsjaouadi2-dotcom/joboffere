package tn.jobboard.offres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.jobboard.offres.entity.Offre;
import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByActiveTrue();
    List<Offre> findByTypeContrat(String typeContrat);
    List<Offre> findByEntreprise(String entreprise);
}