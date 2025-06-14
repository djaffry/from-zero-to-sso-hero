package example.oidc.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment

@SpringBootApplication
class TextMessageSpringBootApplication {

    @Bean
    fun swaggerUrlPrinter(environment: Environment): ApplicationListener<ApplicationReadyEvent> {
        return ApplicationListener { _ ->
            val port = environment.getProperty("server.port")
            println(
                "\nSwagger OpenAPI Definition: http://localhost:$port/swagger-ui.html" +
                        "\nApp available at: http://localhost:$port/" +
                        "\n"
            )
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TextMessageSpringBootApplication>(*args)
}
