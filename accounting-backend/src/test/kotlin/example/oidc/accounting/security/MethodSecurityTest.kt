package example.oidc.accounting.security

import example.oidc.accounting.model.AccountInfoDTO
import example.oidc.accounting.service.AccountInfoService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import java.util.*

@SpringBootTest
class MethodSecurityTest {

    @Autowired
    private lateinit var accountinfoService: AccountInfoService

    private val testId = UUID.randomUUID()
    private val testMessage = AccountInfoDTO().apply { accountEntry = "Test message" }

    @Test
    @WithAnonymousUser
    fun `anonymous users cannot access any methods`() {
        assertThrows<AccessDeniedException> {
            accountinfoService.findAll(null, Pageable.unpaged())
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.get(testId)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.create(testMessage)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.update(testId, testMessage)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.delete(testId)
        }
    }

    @Test
    @WithMockUser(authorities = ["VIEW_ACCOUNT_INFO"])
    fun `users with VIEW_ACCOUNT_INFO can access read methods but not write methods`() {
        accountinfoService.findAll(null, Pageable.unpaged())

        try {
            accountinfoService.get(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.create(testMessage)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.update(testId, testMessage)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.delete(testId)
        }
    }

    @Test
    @WithMockUser(authorities = ["EDIT_ACCOUNT_INFO", "VIEW_ACCOUNT_INFO"])
    fun `users with EDIT_ACCOUNT_INFO and VIEW_ACCOUNT_INFO can access all methods`() {
        accountinfoService.findAll(null, Pageable.unpaged())

        try {
            accountinfoService.get(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            accountinfoService.create(testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            accountinfoService.update(testId, testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            accountinfoService.delete(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }
    }

    @Test
    @WithMockUser(authorities = ["EDIT_ACCOUNT_INFO"])
    fun `users with only EDIT_ACCOUNT_INFO cannot access read methods`() {
        assertThrows<AccessDeniedException> {
            accountinfoService.findAll(null, Pageable.unpaged())
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.get(testId)
        }

        try {
            accountinfoService.create(testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            accountinfoService.update(testId, testMessage)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }

        try {
            accountinfoService.delete(testId)
        } catch (e: Exception) {
            assert(e !is AccessDeniedException) { "Should not throw AccessDeniedException" }
        }
    }

    @Test
    @WithMockUser(authorities = ["SOME_OTHER_ROLE"])
    fun `users with unrelated roles cannot access any methods`() {
        assertThrows<AccessDeniedException> {
            accountinfoService.findAll(null, Pageable.unpaged())
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.get(testId)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.create(testMessage)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.update(testId, testMessage)
        }

        assertThrows<AccessDeniedException> {
            accountinfoService.delete(testId)
        }
    }
}
