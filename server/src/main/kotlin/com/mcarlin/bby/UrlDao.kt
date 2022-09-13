package com.mcarlin.bby

import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UrlDao: CoroutineCrudRepository<Url, Long>, CustomUrlDao

interface CustomUrlDao {
    suspend fun findAllExpired(): Flow<Url>
}

@Language("PostgreSQL")
const val FIND_EXPIRED = """
    select *
    from url
    where url.ttl < LOCALTIMESTAMP AT TIME ZONE 'UTC'
"""

class CustomUrlDaoImpl (
    private val client: DatabaseClient,
    private val converter: MappingR2dbcConverter,
): CustomUrlDao {
    override suspend fun findAllExpired(): Flow<Url> {
        return client.sql(FIND_EXPIRED)
            .map { row, meta -> converter.read(Url::class.java, row, meta) }
            .flow()
    }
}

@Table("url")
data class Url(
    @Id val url_id: Long,
    val url: String,
    val ttl: LocalDateTime?
) : Persistable<Long> {
    override fun getId(): Long = url_id
    override fun isNew(): Boolean = true
}