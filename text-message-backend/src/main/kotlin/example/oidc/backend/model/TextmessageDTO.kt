package example.oidc.backend.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID


class TextmessageDTO {

    var id: UUID? = null

    @NotNull
    @Size(max = 255)
    var message: String? = null

}
