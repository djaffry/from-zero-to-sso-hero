package example.oidc.backend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {

    @Bean
    fun openApiSpec(): OpenAPI = OpenAPI()
        .components(Components()
            .addSchemas("ApiErrorResponse", ObjectSchema()
                .addProperty("status", IntegerSchema())
                .addProperty("code", StringSchema())
                .addProperty("message", StringSchema())
                .addProperty("fieldErrors", ArraySchema().items(
                    Schema<ArraySchema>().`$ref`("ApiFieldError"))))
            .addSchemas("ApiFieldError", ObjectSchema()
                .addProperty("code", StringSchema())
                .addProperty("message", StringSchema())
                .addProperty("property", StringSchema())
                .addProperty("rejectedValue", ObjectSchema())
                .addProperty("path", StringSchema()))
            .addSecuritySchemes("bearerAuth", SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token with Bearer prefix (e.g. 'Bearer eyJhbGciOiJSUzI1...')")))
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))

    @Bean
    fun operationCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation, _ ->
            operation.responses.addApiResponse("4xx/5xx", ApiResponse()
                    .description("Error")
                    .content(Content().addMediaType("*/*", MediaType().schema(
                            Schema<MediaType>().`$ref`("ApiErrorResponse")))))
            operation
        }
    }

}
