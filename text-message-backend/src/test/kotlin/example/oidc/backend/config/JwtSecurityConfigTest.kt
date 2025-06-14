package example.oidc.backend.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class JwtSecurityConfigTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun `JWT with message-service roles should grant access to protected endpoints`() {
        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "resource_access", mapOf(
                            "message-service" to mapOf(
                                "roles" to listOf("VIEW_TEXT_MESSAGES")
                            )
                        )
                    )
                }.authorities(SimpleGrantedAuthority("VIEW_TEXT_MESSAGES")))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "resource_access", mapOf(
                            "message-service" to mapOf(
                                "roles" to listOf("EDIT_TEXT_MESSAGES", "VIEW_TEXT_MESSAGES")
                            )
                        )
                    )
                }.authorities(
                    SimpleGrantedAuthority("VIEW_TEXT_MESSAGES"),
                    SimpleGrantedAuthority("EDIT_TEXT_MESSAGES")
                ))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `JWT with another client's roles should be denied access to protected endpoints`() {
        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "resource_access", mapOf(
                            "another-service" to mapOf(
                                "roles" to listOf("VIEW_TEXT_MESSAGES")
                            )
                        )
                    )
                })
        )
            .andExpect(status().isUnauthorized)

        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "resource_access", mapOf(
                            "another-service" to mapOf(
                                "roles" to listOf("EDIT_TEXT_MESSAGES", "VIEW_TEXT_MESSAGES")
                            )
                        )
                    )
                })
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `JWT without message-service roles should be denied access to protected endpoints`() {
        // Test with empty resource_access
        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim("resource_access", mapOf<String, Any>())
                })
        )
            .andExpect(status().isUnauthorized)

        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "resource_access", mapOf(
                            "other-client" to mapOf(
                                "roles" to listOf("VIEW_TEXT_MESSAGES")
                            )
                        )
                    )
                })
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `JWT with realm roles but no message-service roles should be denied access`() {
        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "realm_access", mapOf(
                            "roles" to listOf("VIEW_TEXT_MESSAGES")
                        )
                    )
                })
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `JWT with message-service roles but not the required ones should be denied access`() {
        mockMvc.perform(
            get("/api/textmessages")
                .with(jwt().jwt { token ->
                    token.claim(
                        "resource_access", mapOf(
                            "message-service" to mapOf(
                                "roles" to listOf("SOME_OTHER_ROLE")
                            )
                        )
                    )
                })
        )
            .andExpect(status().isUnauthorized)
    }
}
