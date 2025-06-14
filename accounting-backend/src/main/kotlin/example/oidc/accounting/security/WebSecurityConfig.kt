package example.oidc.accounting.security

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
import org.springframework.security.oauth2.jwt.JwtDecoder
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
            // TODO Add our custom JWT filter before the UsernamePasswordAuthenticationFilter here

            // Disable the default JWT processing since we're using our custom filter
            .oauth2ResourceServer { oauth2 -> oauth2.disable() }
            .build()

    @Bean
    fun jwtSecurityFilter(jwtDecoder: JwtDecoder) = JwtSecurityFilter(jwtDecoder)

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

}
