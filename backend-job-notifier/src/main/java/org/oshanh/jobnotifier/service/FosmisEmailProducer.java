package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.config.RabbitMQConfig;
import org.oshanh.jobnotifier.dto.FosmisEmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FosmisEmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(FosmisEmailMessage message) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}
