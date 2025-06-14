package example.oidc.backend.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HomeResourceAuthorizationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @WithAnonymousUser
    fun `root endpoint should be accessible without authentication`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
    }
}