package com.ing.bodega.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

import java.util.*
import java.util.concurrent.TimeUnit



@SpringJUnitConfig
@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private val countDownLatchHandler: CountDownLatchHandler? = null

	@Autowired
	private val applicationContext: ApplicationContext? = null

	companion object {
		private val logger: Logger = LoggerFactory.getLogger(DemoApplication::class.java)
		private const val SPRING_INTEGRATION_KAFKA_TOPIC = "spring-integration-kafka.t"

		@ExtendWith
		var embeddedKafka: EmbeddedKafkaRule = EmbeddedKafkaRule(1, true, SPRING_INTEGRATION_KAFKA_TOPIC)
	}

	@Test
	@Throws(Exception::class)
	fun testIntegration() {
		val producingChannel = applicationContext!!.getBean("producingChannel", MessageChannel::class.java)
		val headers: Map<String, Any> = Collections.singletonMap(KafkaHeaders.TOPIC, SPRING_INTEGRATION_KAFKA_TOPIC)
		logger.info("sending 10 messages")
		for (i in 0..9) {
			val message = GenericMessage("Hello Spring Integration Kafka $i!", headers)
			producingChannel.send(message)
			logger.info("sent message='{}'", message)
		}
		countDownLatchHandler!!.latch.await(10000, TimeUnit.MILLISECONDS)
		assertThat(countDownLatchHandler.latch.count).isEqualTo(0)
	}


}
