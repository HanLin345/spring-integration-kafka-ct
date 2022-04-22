package com.ing.bodega.demo

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties


@Configuration
class ConsumingChannelConfig {
    @Value("\${kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Value("\${kafka.topic.spring-integration-kafka}")
    private val springIntegrationKafkaTopic: String? = null
    @Bean
    fun consumingChannel(): DirectChannel {
        return DirectChannel()
    }

    @Bean
    fun kafkaMessageDrivenChannelAdapter(): KafkaMessageDrivenChannelAdapter<String, String> {
        val kafkaMessageDrivenChannelAdapter = KafkaMessageDrivenChannelAdapter(kafkaListenerContainer())
        kafkaMessageDrivenChannelAdapter.setOutputChannel(consumingChannel())
        return kafkaMessageDrivenChannelAdapter
    }

    @Bean
    @ServiceActivator(inputChannel = "consumingChannel")
    fun countDownLatchHandler(): SimpleHandler {
        return SimpleHandler()
    }

    @Bean
    fun kafkaListenerContainer(): ConcurrentMessageListenerContainer<String, String> {
        val containerProps = ContainerProperties(springIntegrationKafkaTopic)
        return ConcurrentMessageListenerContainer(
                consumerFactory(), containerProps) as ConcurrentMessageListenerContainer<String, String>
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<Any, Any> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    fun consumerConfigs(): Map<String, Any?> {
        val properties: MutableMap<String, Any?> = HashMap()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "spring-integration"
        // automatically reset the offset to the earliest offset
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return properties
    }
}