package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.config.RabbitMQConfig;
import org.oshanh.jobnotifier.dto.FosmisEmailMessage;
import org.oshanh.jobnotifier.dto.JobEmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendFosmisEmail(FosmisEmailMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message);
    }

    public void sendJobEmail(JobEmailMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_EXCHANGE,
                RabbitMQConfig.JOB_ROUTING_KEY,
                message);
    }
}
