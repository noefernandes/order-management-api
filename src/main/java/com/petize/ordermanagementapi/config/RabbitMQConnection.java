package com.petize.ordermanagementapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.stereotype.Component;

@EnableRabbit
@Component
public class RabbitMQConnection {
    private static final String EXCHANGE_NAME = "amq.direct";
    private final AmqpAdmin amqpAdmin;

    public RabbitMQConnection(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    private Queue queue(String queueName) {
        return new Queue(queueName, true, false, false);
    }

    private DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    private Binding binding(Queue queue, DirectExchange exchange) {
        return new Binding(queue.getName(), Binding.DestinationType.QUEUE, exchange.getName(), queue.getName(), null);
    }

    @PostConstruct
    private void declareBinding() {
        Queue purchaseQueue =  this.queue("purchase_queue");
        DirectExchange exchange = this.directExchange();
        Binding binding = this.binding(purchaseQueue, exchange);
        this.amqpAdmin.declareQueue(purchaseQueue);
        this.amqpAdmin.declareExchange(exchange);
        this.amqpAdmin.declareBinding(binding);
    }
}
