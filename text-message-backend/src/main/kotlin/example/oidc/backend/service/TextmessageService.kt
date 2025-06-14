package example.oidc.backend.service

import example.oidc.backend.model.TextmessageDTO
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface TextmessageService {

    fun findAll(filter: String?, pageable: Pageable): Page<TextmessageDTO>

    fun `get`(id: UUID): TextmessageDTO

    fun create(textmessageDTO: TextmessageDTO): UUID

    fun update(id: UUID, textmessageDTO: TextmessageDTO)

    fun delete(id: UUID)

}
