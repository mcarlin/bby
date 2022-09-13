package com.mcarlin.bby

import org.springframework.dao.NonTransientDataAccessException
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextLong

@Component
class UrlShortenerService(
    val urlDao: UrlDao
) {
    val lookup =  linkedSetOf (
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    )

    suspend fun shorten(url: String, ttl: LocalDateTime?): String? {
        val range = LongRange(0, 2.0.pow(62.0).toLong() - 1L)
        while (true) {
            val id = Random.nextLong(range)
            try {
                urlDao.save(Url(id, url, ttl))
                return encode(id)
            } catch (e: NonTransientDataAccessException) {
                continue
            }
        }
    }

    suspend fun getUrl(shortenedUrl: String): Url? {
       return urlDao.findById(decode(shortenedUrl))
    }

    suspend fun encode(num: Long): String {
        val base = lookup.size

        if (num < base) {
            lookup
          return lookup.elementAt(num.toInt()).toString()
        }

        val builder = StringBuilder()
        var tmp = num

        while (tmp > 0) {
            builder.insert(0, lookup.elementAt((tmp % base).toInt()))
            tmp /= base
        }

        return builder.toString()
    }

    suspend fun decode(shortenedUrl: String): Long {
        var id = 0L
        shortenedUrl.toCharArray().toList().reversed().forEachIndexed { index, c ->
           id += lookup.indexOf(c) * 62.0.pow(index.toDouble()).toLong()
        }
        return id
    }
}