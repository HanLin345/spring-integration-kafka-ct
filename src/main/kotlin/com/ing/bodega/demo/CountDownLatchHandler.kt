package com.ing.bodega.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.MessagingException

import java.util.concurrent.CountDownLatch

class CountDownLatchHandler : MessageHandler {
    val latch = CountDownLatch(10)

    @Throws(MessagingException::class)
    override fun handleMessage(message: Message<*>) {
        LOGGER.info("received message='{}'", message)
        latch.countDown()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CountDownLatchHandler::class.java)
    }

}