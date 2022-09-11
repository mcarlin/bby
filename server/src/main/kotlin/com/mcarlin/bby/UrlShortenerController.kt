package com.mcarlin.bby

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDateTime

@RestController
class UrlShortenerController {

    @PostMapping
    suspend fun shorten(request: ShortenedUrlRequest): ResponseEntity<ShortenedUrlResponse> {
        return ResponseEntity.ok(ShortenedUrlResponse("", request.ttl))
    }

    @GetMapping
    suspend fun getShortened(): ResponseEntity<Unit> {
        val headers = HttpHeaders()
        headers.location = URI.create("https://www.google.com")
        return ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY)
    }
}

data class ShortenedUrlRequest (
    val url: String,
    val ttl: LocalDateTime
)

data class ShortenedUrlResponse (
    val shortenedUrl: String,
    val ttl: LocalDateTime
)