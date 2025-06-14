package example.oidc.backend.service

import example.oidc.backend.domain.Textmessage
import example.oidc.backend.model.TextmessageDTO
import example.oidc.backend.repos.TextmessageRepository
import example.oidc.backend.util.NotFoundException
import java.lang.IllegalArgumentException
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service


@Service
class TextmessageServiceImpl(
    private val textmessageRepository: TextmessageRepository
) : TextmessageService {

    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).VIEW_TEXT_MESSAGES.name())")
    override fun findAll(filter: String?, pageable: Pageable): Page<TextmessageDTO> {
        var page: Page<Textmessage>
        if (filter != null) {
            var uuidFilter: UUID? = null
            try {
                uuidFilter = UUID.fromString(filter)
            } catch (illegalArgumentException: IllegalArgumentException) {
                // keep null - no parseable input
            }
            page = textmessageRepository.findAllById(uuidFilter, pageable)
        } else {
            page = textmessageRepository.findAll(pageable)
        }
        return PageImpl(page.content
                .stream()
                .map { textmessage -> mapToDTO(textmessage, TextmessageDTO()) }
                .toList(),
                pageable, page.totalElements)
    }

    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).VIEW_TEXT_MESSAGES.name())")
    override fun `get`(id: UUID): TextmessageDTO = textmessageRepository.findById(id)
            .map { textmessage -> mapToDTO(textmessage, TextmessageDTO()) }
            .orElseThrow { NotFoundException() }

    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).EDIT_TEXT_MESSAGES.name())")
    override fun create(textmessageDTO: TextmessageDTO): UUID {
        val textmessage = Textmessage()
        mapToEntity(textmessageDTO, textmessage)
        return textmessageRepository.save(textmessage).id!!
    }

    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).EDIT_TEXT_MESSAGES.name())")
    override fun update(id: UUID, textmessageDTO: TextmessageDTO) {
        val textmessage = textmessageRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(textmessageDTO, textmessage)
        textmessageRepository.save(textmessage)
    }

    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).EDIT_TEXT_MESSAGES.name())")
    override fun delete(id: UUID) {
        textmessageRepository.deleteById(id)
    }

    private fun mapToDTO(textmessage: Textmessage, textmessageDTO: TextmessageDTO): TextmessageDTO {
        textmessageDTO.id = textmessage.id
        textmessageDTO.message = textmessage.message
        return textmessageDTO
    }

    private fun mapToEntity(textmessageDTO: TextmessageDTO, textmessage: Textmessage): Textmessage {
        textmessage.message = textmessageDTO.message
        return textmessage
    }

}
