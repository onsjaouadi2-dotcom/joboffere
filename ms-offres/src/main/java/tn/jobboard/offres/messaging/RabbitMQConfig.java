package tn.jobboard.offres.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NOUVELLE = "queue.offre.nouvelle";
    public static final String QUEUE_SUPPRIMEE = "queue.offre.supprimee";
    public static final String EXCHANGE = "jobboard.exchange";

    @Bean
    public Queue queueNouvelleOffre() {
        return new Queue(QUEUE_NOUVELLE, true);
    }

    @Bean
    public Queue queueOffreSupprimee() {
        return new Queue(QUEUE_SUPPRIMEE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingNouvelle() {
        return BindingBuilder
                .bind(queueNouvelleOffre())
                .to(exchange())
                .with("offre.nouvelle");
    }

    @Bean
    public Binding bindingSupprimee() {
        return BindingBuilder
                .bind(queueOffreSupprimee())
                .to(exchange())
                .with("offre.supprimee");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}