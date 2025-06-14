package example.oidc.accounting.service

import example.oidc.accounting.model.AccountInfoDTO
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface AccountInfoService {

    fun findAll(filter: String?, pageable: Pageable): Page<AccountInfoDTO>

    fun `get`(id: UUID): AccountInfoDTO

    fun create(accountinfoDTO: AccountInfoDTO): UUID

    fun update(id: UUID, accountinfoDTO: AccountInfoDTO)

    fun delete(id: UUID)

}
