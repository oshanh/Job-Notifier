package org.oshanh.jobnotifier.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "fosmis-email-queue";
    public static final String EXCHANGE = "fosmis-exchange";
    public static final String ROUTING_KEY = "fosmis.email";

    public static final String JOB_QUEUE = "job-email-queue";
    public static final String JOB_EXCHANGE = "job-exchange";
    public static final String JOB_ROUTING_KEY = "job.email";

    public static final String KALENI_QUEUE = "kaleni-email-queue";
    public static final String KALENI_EXCHANGE = "kaleni-exchange";
    public static final String KALENI_ROUTING_KEY = "kaleni.email";

    @Bean
    public Queue fosmisEmailQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public DirectExchange fosmisExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding fosmisBinding(
            Queue fosmisEmailQueue,
            DirectExchange fosmisExchange) {

        return BindingBuilder
                .bind(fosmisEmailQueue)
                .to(fosmisExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Queue jobEmailQueue() {
        return new Queue(JOB_QUEUE, true);
    }

    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(JOB_EXCHANGE);
    }

    @Bean
    public Binding jobBinding(
            Queue jobEmailQueue,
            DirectExchange jobExchange) {

        return BindingBuilder
                .bind(jobEmailQueue)
                .to(jobExchange)
                .with(JOB_ROUTING_KEY);
    }

    @Bean
    public Queue kaleniEmailQueue() {
        return new Queue(KALENI_QUEUE, true);
    }

    @Bean
    public DirectExchange kaleniExchange() {
        return new DirectExchange(KALENI_EXCHANGE);
    }

    @Bean
    public Binding kaleniBinding(
            Queue kaleniEmailQueue,
            DirectExchange kaleniExchange) {

        return BindingBuilder
                .bind(kaleniEmailQueue)
                .to(kaleniExchange)
                .with(KALENI_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(converter);

        return rabbitTemplate;
    }
}
