package example.oidc.accounting.rest

import example.oidc.accounting.model.AccountInfoDTO
import example.oidc.accounting.service.AccountInfoService
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


@RestController
@RequestMapping(
    value = ["/api/accountinfos"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class AccountInfoResource(
    private val accountinfoService: AccountInfoService
) {

    /**
     * TODO secure endpoints as a minimum layer of security
     *   data modifying endpoints -> secure with WRITE rights
     *   fetch only endpoints -> secure READ rights
     */

    @Operation(
        parameters = [
            Parameter(
                name = "page", `in` = ParameterIn.QUERY, schema = Schema(
                    implementation =
                        Int::class
                )
            ),
            Parameter(
                name = "size", `in` = ParameterIn.QUERY, schema = Schema(
                    implementation =
                        Int::class
                )
            ),
            Parameter(
                name = "sort", `in` = ParameterIn.QUERY, schema = Schema(
                    implementation =
                        String::class
                )
            )
        ]
    )
    @GetMapping
    fun getAllAccountinfos(
        @RequestParam(name = "filter", required = false) filter: String?,
        @Parameter(hidden = true) @SortDefault(sort = ["id"]) @PageableDefault(size = 20)
        pageable: Pageable
    ): ResponseEntity<Page<AccountInfoDTO>> =
        ResponseEntity.ok(accountinfoService.findAll(filter, pageable))

    @GetMapping("/{id}")

    fun getAccountinfo(@PathVariable(name = "id") id: UUID): ResponseEntity<AccountInfoDTO> =
        ResponseEntity.ok(accountinfoService.get(id))

    @PostMapping
    @ApiResponse(responseCode = "201")

    fun createAccountinfo(@RequestBody @Valid accountinfoDTO: AccountInfoDTO):
            ResponseEntity<UUID> {
        val createdId = accountinfoService.create(accountinfoDTO)
        return ResponseEntity(createdId, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateAccountinfo(
        @PathVariable(name = "id") id: UUID, @RequestBody @Valid
        accountinfoDTO: AccountInfoDTO
    ): ResponseEntity<UUID> {
        accountinfoService.update(id, accountinfoDTO)
        return ResponseEntity.ok(id)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    fun deleteAccountinfo(@PathVariable(name = "id") id: UUID): ResponseEntity<Void> {
        accountinfoService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
