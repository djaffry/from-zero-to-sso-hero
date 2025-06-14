package example.oidc.backend.repos

import example.oidc.backend.domain.Textmessage
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface TextmessageRepository : JpaRepository<Textmessage, UUID> {

    fun findAllById(id: UUID?, pageable: Pageable): Page<Textmessage>

}
