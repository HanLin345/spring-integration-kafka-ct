package com.ing.bodega.demo

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.expression.common.LiteralExpression
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.messaging.MessageHandler


@Configuration
class ProducingChannelConfig {
    @Value("\${kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null
    @Bean
    fun producingChannel(): DirectChannel {
        return DirectChannel()
    }

    @Bean
    @ServiceActivator(inputChannel = "producingChannel")
    fun kafkaMessageHandler(): MessageHandler {
        val handler = KafkaProducerMessageHandler(kafkaTemplate())
        handler.setMessageKeyExpression(LiteralExpression("kafka-integration"))
        return handler
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun producerConfigs(): Map<String, Any?> {
        val properties: MutableMap<String, Any?> = HashMap()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        // introduce a delay on the send to allow more messages to accumulate
        properties[ProducerConfig.LINGER_MS_CONFIG] = 1
        return properties
    }

}