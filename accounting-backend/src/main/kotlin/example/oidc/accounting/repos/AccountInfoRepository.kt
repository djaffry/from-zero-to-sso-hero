package example.oidc.accounting.repos

import example.oidc.accounting.domain.AccountInfo
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface AccountInfoRepository : JpaRepository<AccountInfo, UUID> {

    fun findAllById(id: UUID?, pageable: Pageable): Page<AccountInfo>

}
