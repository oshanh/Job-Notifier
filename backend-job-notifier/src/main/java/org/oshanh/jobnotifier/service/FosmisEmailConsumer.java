package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oshanh.jobnotifier.config.RabbitMQConfig;
import org.oshanh.jobnotifier.dto.FosmisEmailMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FosmisEmailConsumer {

        private final NotificationService notificationService;

        @RabbitListener(queues = RabbitMQConfig.QUEUE, concurrency = "${fosmis.email.concurrency}")
        public void consume(FosmisEmailMessage message) {

                long start = System.currentTimeMillis();
                log.info("Sending email to {}", message.email());

                notificationService.sendFOSMISNotice(
                                message.title(),
                                message.publishedAt(),
                                message.link(),
                                "oshanedu@gmail.com");
                                //message.email());

                long duration = System.currentTimeMillis() - start;

                log.info(
                                "Email sent to {} in {} ms",
                                message.email(),
                                duration);
        }
}
