package example.oidc.accounting.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID


class AccountInfoDTO {

    var id: UUID? = null

    @NotNull
    @Size(max = 255)
    var accountEntry: String? = null

}
