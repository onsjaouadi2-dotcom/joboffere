package tn.jobboard.offres.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.jobboard.offres.entity.Offre;
import tn.jobboard.offres.feign.CandidatClient;
import tn.jobboard.offres.messaging.OffreProducer;
import tn.jobboard.offres.repository.OffreRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository offreRepository;
    private final CandidatClient candidatClient;
    private final OffreProducer offreProducer;

    // ── CRUD ──────────────────────────────────────────────
    public List<Offre> getAll() {
        return offreRepository.findAll();
    }

    public Offre getById(Long id) {
        return offreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée : " + id));
    }

    public Offre save(Offre offre) {
        Offre saved = offreRepository.save(offre);
        // Notification async RabbitMQ — scénario 1
        offreProducer.sendNouvelleOffre(saved);
        return saved;
    }

    public Offre update(Long id, Offre updated) {
        Offre existing = getById(id);
        existing.setTitre(updated.getTitre());
        existing.setDescription(updated.getDescription());
        existing.setEntreprise(updated.getEntreprise());
        existing.setLocalisation(updated.getLocalisation());
        existing.setTypeContrat(updated.getTypeContrat());
        existing.setSalaire(updated.getSalaire());
        existing.setActive(updated.isActive());
        return offreRepository.save(existing);
    }

    public void delete(Long id) {
        offreRepository.deleteById(id);
        // Notification async RabbitMQ — scénario 2
        offreProducer.sendOffreSupprimee(id);
    }

    public List<Offre> getOffresActives() {
        return offreRepository.findByActiveTrue();
    }

    // ── COMMUNICATION SYNCHRONE (Feign) ───────────────────
    // Scénario 1 : récupérer tous les candidats d'une offre
    public List<Map> getCandidatsByOffre(Long offreId) {
        return candidatClient.getCandidatsByOffreId(offreId);
    }

    // Scénario 2 : récupérer les détails d'un candidat
    public Map getCandidatDetails(String candidatId) {
        return candidatClient.getCandidatById(candidatId);
    }
}