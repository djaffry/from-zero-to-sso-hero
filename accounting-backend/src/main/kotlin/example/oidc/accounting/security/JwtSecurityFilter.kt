package example.oidc.accounting.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtSecurityFilter(private val jwtDecoder: JwtDecoder) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = extractJwtFromRequest(request)

            if (token != null) {
                val jwt = jwtDecoder.decode(token)
                verifyToken(jwt)

                val principal = jwt.subject
                val credentials = TODO()
                val authorities = mapAuthorities(TODO())
                val authentication = UsernamePasswordAuthenticationToken(principal, credentials, authorities)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: JwtException) {
            logger.error("JWT token validation failed", ex)
            SecurityContextHolder.clearContext()
        } catch (ex: Exception) {
            logger.error("Failed to process JWT authentication", ex)
            SecurityContextHolder.clearContext()
        }

        TODO("What do I need to do at the end of the FilterChain so I won't stop the chain here?")
    }

    private fun extractJwtFromRequest(request: HttpServletRequest): String? {
        val bearerPrefix = "Bearer "
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(bearerPrefix)) {
            return bearerToken.substring(bearerPrefix.length)
        }
        return null
    }

    private fun mapAuthorities(claims: Map<String, Any>): List<SimpleGrantedAuthority> {
        // TODO insert the correct keys for mapping a jwt here
        val key1 = TODO()
        val key2 = TODO()
        val key3 = TODO()
        try {
            val resourceAccess = claims[key1] as? Map<*, *> ?: return emptyList()
            val messageServiceAccess = resourceAccess[key2] as? Map<*, *> ?: return emptyList()
            val roles = messageServiceAccess[key3] as? Collection<*> ?: return emptyList()

            return roles.mapNotNull { role ->
                if (role is String) SimpleGrantedAuthority(role) else null
            }
        } catch (_: Exception) {
            return emptyList()
        }
    }

    private fun verifyToken(jwt: Jwt) {
        /**
         * This is the most crucial step
         * - Verify the token's signature against the jwks from the keycloak, check integrity
         * - Check Claims, like expiry, issuer, audience, etc.
         * - Check Revocation, if applicable
         *
         * Never implement this these checks yourself.
         * Save some time and peace of mind and use industry-standard libraries for security
         *
         * For the sake of the workshop, image this function verifies the jwt correctly.
         * This is not production ready code!
         */
    }
}
