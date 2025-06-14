package example.oidc.accounting.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeResource {

    @GetMapping("/")
    fun index(): String = "\"Hello C-Corp User! This is Accounting!\""

}