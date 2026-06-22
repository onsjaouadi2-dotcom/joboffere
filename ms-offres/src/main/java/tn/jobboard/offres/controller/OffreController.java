package tn.jobboard.offres.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.jobboard.offres.entity.Offre;
import tn.jobboard.offres.service.OffreService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offres")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;

    // ── CRUD ──────────────────────────────────────────────
    @GetMapping
    public List<Offre> getAll() {
        return offreService.getAll();
    }

    @GetMapping("/{id}")
    public Offre getById(@PathVariable Long id) {
        return offreService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Offre> create(@RequestBody Offre offre) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offreService.save(offre));
    }

    @PutMapping("/{id}")
    public Offre update(@PathVariable Long id, @RequestBody Offre offre) {
        return offreService.update(id, offre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        offreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actives")
    public List<Offre> getActives() {
        return offreService.getOffresActives();
    }

    // ── COMMUNICATION SYNCHRONE (Feign) ───────────────────
    // Scénario 1 : candidats d'une offre
    @GetMapping("/{id}/candidats")
    public List<Map> getCandidats(@PathVariable Long id) {
        return offreService.getCandidatsByOffre(id);
    }

    // Scénario 2 : détails d'un candidat depuis MS Offres
    @GetMapping("/candidat/{candidatId}")
    public Map getCandidatDetails(@PathVariable String candidatId) {
        return offreService.getCandidatDetails(candidatId);
    }
}