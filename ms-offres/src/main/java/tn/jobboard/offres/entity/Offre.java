package tn.jobboard.offres.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String entreprise;
    private String localisation;
    private String typeContrat;
    private Double salaire;
    private boolean active;
}