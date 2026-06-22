package tn.jobboard.offres.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tn.jobboard.offres.entity.Offre;

@Component
@RequiredArgsConstructor
@Slf4j
public class OffreProducer {

    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE = "jobboard.exchange";
    public static final String ROUTING_KEY_NOUVELLE = "offre.nouvelle";
    public static final String ROUTING_KEY_SUPPRIMEE = "offre.supprimee";

    // Scénario async 1 : nouvelle offre créée → notifier les candidats
    public void sendNouvelleOffre(Offre offre) {
        log.info("Envoi nouvelle offre : {}", offre.getTitre());
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_NOUVELLE, offre);
    }

    // Scénario async 2 : offre supprimée → notifier les candidats
    public void sendOffreSupprimee(Long offreId) {
        log.info("Envoi offre supprimée : {}", offreId);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SUPPRIMEE, offreId);
    }
}