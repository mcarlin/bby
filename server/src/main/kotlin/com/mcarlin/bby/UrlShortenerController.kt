package com.mcarlin.bby

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDateTime

@RestController
class UrlShortenerController (
    val shortenerService: UrlShortenerService,
) {
    @PostMapping(
        "/v1/api/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun shorten(@RequestBody request: ShortenedUrlRequest): ResponseEntity<ShortenedUrlResponse> {
        val ttl = request.ttl ?: LocalDateTime.now().plusDays(1)

        val shortenedUrl =  shortenerService.shorten(request.url, ttl)!!
        return ResponseEntity.ok(ShortenedUrlResponse(shortenedUrl, ttl))
    }

    @GetMapping("/api/v1/{someId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun getShortened(@PathVariable("someId") someId: String): ResponseEntity<Unit> {
        val url = shortenerService.getUrl(someId)
        return if (url != null) {
            val headers = HttpHeaders()
            headers.location = URI.create(url.url)
            ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/u/{someId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun shortened(@PathVariable("someId") someId: String): ResponseEntity<Unit> {
        val url = shortenerService.getUrl(someId)
        return if (url != null) {
            val headers = HttpHeaders()
            headers.location = URI.create(url.url)
            ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class ShortenedUrlRequest (
    val url: String,
    val ttl: LocalDateTime?
)

data class ShortenedUrlResponse (
    val shortenedUrl: String,
    val ttl: LocalDateTime
)