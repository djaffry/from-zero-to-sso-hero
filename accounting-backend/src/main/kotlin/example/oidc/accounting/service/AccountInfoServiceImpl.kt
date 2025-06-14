package example.oidc.accounting.service

import example.oidc.accounting.domain.AccountInfo
import example.oidc.accounting.model.AccountInfoDTO
import example.oidc.accounting.repos.AccountInfoRepository
import example.oidc.accounting.util.NotFoundException
import java.lang.IllegalArgumentException
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class AccountInfoServiceImpl(
    private val accountinfoRepository: AccountInfoRepository
) : AccountInfoService {

    /**
     * TODO secure methods as an additional layer of security
     *   data modifying functions -> secure with WRITE rights
     *   fetch only functions -> secure READ rights
     */

    override fun findAll(filter: String?, pageable: Pageable): Page<AccountInfoDTO> {
        var page: Page<AccountInfo>
        if (filter != null) {
            var uuidFilter: UUID? = null
            try {
                uuidFilter = UUID.fromString(filter)
            } catch (illegalArgumentException: IllegalArgumentException) {
                // keep null - no parseable input
            }
            page = accountinfoRepository.findAllById(uuidFilter, pageable)
        } else {
            page = accountinfoRepository.findAll(pageable)
        }
        return PageImpl(
            page.content
            .stream()
            .map { accountinfo -> mapToDTO(accountinfo, AccountInfoDTO()) }
            .toList(),
            pageable, page.totalElements)
    }

    override fun `get`(id: UUID): AccountInfoDTO = accountinfoRepository.findById(id)
        .map { accountinfo -> mapToDTO(accountinfo, AccountInfoDTO()) }
        .orElseThrow { NotFoundException() }

    override fun create(accountinfoDTO: AccountInfoDTO): UUID {
        val accountinfo = AccountInfo()
        mapToEntity(accountinfoDTO, accountinfo)
        return accountinfoRepository.save(accountinfo).id!!
    }

    override fun update(id: UUID, accountinfoDTO: AccountInfoDTO) {
        val accountinfo = accountinfoRepository.findById(id)
            .orElseThrow { NotFoundException() }
        mapToEntity(accountinfoDTO, accountinfo)
        accountinfoRepository.save(accountinfo)
    }

    override fun delete(id: UUID) {
        accountinfoRepository.deleteById(id)
    }

    private fun mapToDTO(accountinfo: AccountInfo, accountinfoDTO: AccountInfoDTO): AccountInfoDTO {
        accountinfoDTO.id = accountinfo.id
        accountinfoDTO.accountEntry = accountinfo.accountEntry
        return accountinfoDTO
    }

    private fun mapToEntity(accountinfoDTO: AccountInfoDTO, accountinfo: AccountInfo): AccountInfo {
        accountinfo.accountEntry = accountinfoDTO.accountEntry
        return accountinfo
    }

}
