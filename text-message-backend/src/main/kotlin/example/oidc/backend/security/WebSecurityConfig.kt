package example.oidc.backend.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.cors(Customizer.withDefaults())
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.xssProtection(Customizer.withDefaults())
                headers.contentTypeOptions(Customizer.withDefaults())
                headers.frameOptions { frameOptions -> frameOptions.deny() }
                headers.contentSecurityPolicy { csp -> csp.policyDirectives("default-src 'self'") }
                headers.cacheControl(Customizer.withDefaults())
            }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                authorize.requestMatchers("/").permitAll()
                authorize.anyRequest().authenticated()
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                exceptions.accessDeniedHandler(accessDeniedHandler())
            }
            .oauth2ResourceServer { oauth2 -> 
                oauth2.jwt { jwt -> 
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .build()

    @Bean
    fun accessDeniedHandler() = object : AccessDeniedHandlerImpl() {
        override fun handle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            accessDeniedException: AccessDeniedException
        ) {
            response.status = HttpStatus.FORBIDDEN.value()
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Access Denied\",\"message\":\"You don't have permission to access this resource\"}")
        }
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt -> mapAuthorities(jwt.claims) }
        return converter
    }

    private fun mapAuthorities(claims: Map<String, Any>): List<SimpleGrantedAuthority> {
        try {
            val resourceAccess = claims["resource_access"] as? Map<*, *> ?: return emptyList()
            val messageServiceAccess = resourceAccess["message-service"] as? Map<*, *> ?: return emptyList()
            val roles = messageServiceAccess["roles"] as? Collection<*> ?: return emptyList()

            return roles.mapNotNull { role -> 
                if (role is String) SimpleGrantedAuthority(role) else null 
            }
        } catch (_: Exception) {
            return emptyList()
        }
    }

}
