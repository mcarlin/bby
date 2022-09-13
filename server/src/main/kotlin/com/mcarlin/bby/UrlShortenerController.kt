package com.mcarlin.bby

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDateTime

@RestController
class UrlShortenerController (
    val shortenerService: UrlShortenerService,
) {
    @PostMapping(
        "/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun shorten(@RequestBody request: ShortenedUrlRequest): ResponseEntity<ShortenedUrlResponse> {
        val shortenedUrl = shortenerService.shorten(request.url, request.ttl)!!
        return ResponseEntity.ok(ShortenedUrlResponse(shortenedUrl, request.ttl))
    }

    @GetMapping("/{someId}")
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
}

data class ShortenedUrlRequest (
    val url: String,
    val ttl: LocalDateTime?
)

data class ShortenedUrlResponse (
    val shortenedUrl: String,
    val ttl: LocalDateTime?
)