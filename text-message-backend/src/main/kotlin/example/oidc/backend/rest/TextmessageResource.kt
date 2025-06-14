package example.oidc.backend.rest

import example.oidc.backend.model.TextmessageDTO
import example.oidc.backend.service.TextmessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import java.lang.Void
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize


@RestController
@RequestMapping(
    value = ["/api/textmessages"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class TextmessageResource(
    private val textmessageService: TextmessageService
) {

    @Operation(parameters = [
        Parameter(name = "page", `in` = ParameterIn.QUERY, schema = Schema(implementation =
                    Int::class)),
        Parameter(name = "size", `in` = ParameterIn.QUERY, schema = Schema(implementation =
                    Int::class)),
        Parameter(name = "sort", `in` = ParameterIn.QUERY, schema = Schema(implementation =
                    String::class))
    ])
    @GetMapping
    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).VIEW_TEXT_MESSAGES.name())")
    fun getAllTextmessages(@RequestParam(name = "filter", required = false) filter: String?,
            @Parameter(hidden = true) @SortDefault(sort = ["id"]) @PageableDefault(size = 20)
            pageable: Pageable): ResponseEntity<Page<TextmessageDTO>> =
            ResponseEntity.ok(textmessageService.findAll(filter, pageable))

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).VIEW_TEXT_MESSAGES.name())")

    fun getTextmessage(@PathVariable(name = "id") id: UUID): ResponseEntity<TextmessageDTO> =
            ResponseEntity.ok(textmessageService.get(id))

    @PostMapping
    @ApiResponse(responseCode = "201")
    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).EDIT_TEXT_MESSAGES.name())")

    fun createTextmessage(@RequestBody @Valid textmessageDTO: TextmessageDTO):
            ResponseEntity<UUID> {
        val createdId = textmessageService.create(textmessageDTO)
        return ResponseEntity(createdId, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).EDIT_TEXT_MESSAGES.name())")
    fun updateTextmessage(@PathVariable(name = "id") id: UUID, @RequestBody @Valid
            textmessageDTO: TextmessageDTO): ResponseEntity<UUID> {
        textmessageService.update(id, textmessageDTO)
        return ResponseEntity.ok(id)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @PreAuthorize("hasAuthority(T(example.oidc.backend.security.Permission).EDIT_TEXT_MESSAGES.name())")
    fun deleteTextmessage(@PathVariable(name = "id") id: UUID): ResponseEntity<Void> {
        textmessageService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
