package example.oidc.backend.security

import example.oidc.backend.model.TextmessageDTO
import example.oidc.backend.service.TextmessageService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import java.util.*

@SpringBootTest
class MethodSecurityTest {

    @Autowired
    private lateinit var textmessageService: TextmessageService

    private val testId = UUID.randomUUID()
    private val testMessage = TextmessageDTO().apply { message = "Test message" }

    @Test
    @WithAnonymousUser
    fun `anonymous users cannot access any methods`() {
        assertThrows<AccessDeniedException> {
            textmessageService.findAll(null, org.springframework.data.domain.Pageable.unpaged())
        }

        assertThrows<AccessDeniedException> {
            textmessageService.get(testId)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.create(testMessage)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.update(testId, testMessage)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.delete(testId)
        }
    }

    @Test
    @WithMockUser(authorities = ["VIEW_TEXT_MESSAGES"])
    fun `users with VIEW_TEXT_MESSAGES can access read methods but not write methods`() {
        textmessageService.findAll(null, org.springframework.data.domain.Pageable.unpaged())

        try {
            textmessageService.get(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        assertThrows<AccessDeniedException> {
            textmessageService.create(testMessage)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.update(testId, testMessage)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.delete(testId)
        }
    }

    @Test
    @WithMockUser(authorities = ["EDIT_TEXT_MESSAGES", "VIEW_TEXT_MESSAGES"])
    fun `users with EDIT_TEXT_MESSAGES and VIEW_TEXT_MESSAGES can access all methods`() {
        textmessageService.findAll(null, org.springframework.data.domain.Pageable.unpaged())

        try {
            textmessageService.get(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            textmessageService.create(testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            textmessageService.update(testId, testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            textmessageService.delete(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }
    }

    @Test
    @WithMockUser(authorities = ["EDIT_TEXT_MESSAGES"])
    fun `users with only EDIT_TEXT_MESSAGES cannot access read methods`() {
        assertThrows<AccessDeniedException> {
            textmessageService.findAll(null, org.springframework.data.domain.Pageable.unpaged())
        }

        assertThrows<AccessDeniedException> {
            textmessageService.get(testId)
        }

        try {
            textmessageService.create(testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            textmessageService.update(testId, testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            textmessageService.delete(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }
    }

    @Test
    @WithMockUser(authorities = ["SOME_OTHER_ROLE"])
    fun `users with unrelated roles cannot access any methods`() {
        assertThrows<AccessDeniedException> {
            textmessageService.findAll(null, org.springframework.data.domain.Pageable.unpaged())
        }

        assertThrows<AccessDeniedException> {
            textmessageService.get(testId)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.create(testMessage)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.update(testId, testMessage)
        }

        assertThrows<AccessDeniedException> {
            textmessageService.delete(testId)
        }
    }
}
