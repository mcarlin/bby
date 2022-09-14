package com.mcarlin.bby

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurerComposite

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@ConfigurationPropertiesScan
class ServerApplication(val env: Environment) {
	@Bean
	fun corsConfigurer(): WebFluxConfigurer {
		return object : WebFluxConfigurerComposite() {
			override fun addCorsMappings(registry: CorsRegistry) {
				registry.addMapping("/**").allowedOrigins("*")
					.allowedMethods("*")
			}
		}
	}

	@Bean(initMethod = "migrate")
	fun flyway(): Flyway {
		return Flyway(
			Flyway.configure()
			.dataSource(
				env.getRequiredProperty("spring.flyway.url"),
				env.getRequiredProperty("spring.flyway.user"),
				env.getRequiredProperty("spring.flyway.password"))
		)
	}
}

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
