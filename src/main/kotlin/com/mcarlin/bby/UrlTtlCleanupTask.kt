package com.mcarlin.bby

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "bby")
@Suppress("unused")
data class CleanupConfiguration @ConstructorBinding constructor (
    val cleanupSchedule: String
)

@Component
class UrlTtlCleanupTask(
    private val urlDao: UrlDao,
) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    //    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "\${bby.cleanupSchedule}")
    fun cleanupExpiredUrls() {
        log.info("Running expired URL cleanup task")

        runBlocking {
            val findAllExpired = urlDao.findAllExpired()
            log.info("Number of expired URLs: {}", findAllExpired.count())

            findAllExpired.collect {
                log.debug("Deleting expired url: {}", it)
                urlDao.delete(it)
            }
        }
    }
}