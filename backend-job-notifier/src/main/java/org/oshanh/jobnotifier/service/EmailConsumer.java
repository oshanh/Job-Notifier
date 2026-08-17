package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oshanh.jobnotifier.config.RabbitMQConfig;
import org.oshanh.jobnotifier.dto.FosmisEmailMessage;
import org.oshanh.jobnotifier.dto.JobEmailMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE, concurrency = "${fosmis.email.concurrency}")
    public void consumeFosmis(FosmisEmailMessage message) {
        long start = System.currentTimeMillis();
        log.info("Sending FOSMIS email to {}", message.email());

        notificationService.sendFOSMISNotice(
                message.title(),
                message.publishedAt(),
                message.link(),
                "oshanedu@gmail.com");
        // message.email());

        long duration = System.currentTimeMillis() - start;
        log.info("FOSMIS email sent to {} in {} ms", message.email(), duration);
    }

    @RabbitListener(queues = RabbitMQConfig.JOB_QUEUE)
    public void consumeJob(JobEmailMessage message) {
        try {
            log.info("Sending job email notification to {}", message.getEmail());
            notificationService.sendNewJobPostingsNotification(message.getEmail(), message.getJobs());
            log.info("Job email successfully dispatched to {}", message.getEmail());
        } catch (Exception e) {
            log.error("Failed to neatly send job email via RabbitMQ to {}", message.getEmail(), e);
        }
    }
}
