package example.oidc.accounting.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class AccountinfoResourceAuthorizationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val baseUrl = "/api/accountinfos"
    private val testId = UUID.randomUUID()
    private val testMessageJson = """{"accountEntry": "Test message"}"""

    @Test
    @WithAnonymousUser
    fun `anonymous users cannot access any endpoints`() {
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(get("$baseUrl/$testId"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isUnauthorized)

        mockMvc.perform(
            put("$baseUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isUnauthorized)

        mockMvc.perform(delete("$baseUrl/$testId"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(authorities = ["VIEW_ACCOUNT_INFO"])
    fun `users with VIEW_ACCOUNT_INFO can access read endpoints but not write endpoints`() {
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)

        mockMvc.perform(get("$baseUrl/$testId"))
            .andExpect(status().isNotFound)

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isUnauthorized)

        mockMvc.perform(
            put("$baseUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isUnauthorized)

        mockMvc.perform(delete("$baseUrl/$testId"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(authorities = ["EDIT_ACCOUNT_INFO", "VIEW_ACCOUNT_INFO"])
    fun `users with EDIT_ACCOUNT_INFO can access all endpoints`() {
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)

        mockMvc.perform(get("$baseUrl/$testId"))
            .andExpect(status().isNotFound)

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isCreated)

        mockMvc.perform(
            put("$baseUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isNotFound)

        mockMvc.perform(delete("$baseUrl/$testId"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(authorities = ["SOME_OTHER_ROLE"])
    fun `users with unrelated roles cannot access any endpoints`() {
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(get("$baseUrl/$testId"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isUnauthorized)

        mockMvc.perform(
            put("$baseUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isUnauthorized)

        mockMvc.perform(delete("$baseUrl/$testId"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(authorities = ["EDIT_ACCOUNT_INFO"])
    fun `users with only EDIT_ACCOUNT_INFO cannot access read endpoints`() {
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(get("$baseUrl/$testId"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isCreated)

        mockMvc.perform(
            put("$baseUrl/$testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testMessageJson)
        ).andExpect(status().isNotFound)

        mockMvc.perform(delete("$baseUrl/$testId"))
            .andExpect(status().isNoContent)
    }
}
