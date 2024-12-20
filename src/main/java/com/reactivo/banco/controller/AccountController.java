package com.reactivo.banco.controller;


import com.reactivo.banco.model.dto.AccountInDTO;
import com.reactivo.banco.model.dto.AccountOutDTO;
import com.reactivo.banco.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/cuentas")
@Tag(name = "Accounts", description = "Manage bank accounts")
public class AccountController {
    
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create Account",
            description = "Endpoint to create a new account.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Account created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountOutDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping
    public Mono<ResponseEntity<AccountOutDTO>> createAccount(@Valid @RequestBody AccountInDTO cuentaInDTO) {
        return accountService.createAccount(cuentaInDTO)
                .map(accountOutDTO -> ResponseEntity.status(HttpStatus.CREATED).body(accountOutDTO));
    }

    @Operation(
            summary = "Get All Accounts",
            description = "Retrieve a list of all bank accounts.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountOutDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping
    public Mono<ResponseEntity<Flux<AccountOutDTO>>> getAllAccounts() {
        Flux<AccountOutDTO> accounts = accountService.getAllAccounts();
        return Mono.just(ResponseEntity.ok(accounts));
    }

    @Operation(
            summary = "Get Account by ID",
            description = "Retrieve details of a specific account by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountOutDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountOutDTO>> getAccountById(@PathVariable String id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Account",
            description = "Update details of a specific account by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountOutDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountOutDTO>> updateAccount(@PathVariable String id, @Valid @RequestBody AccountInDTO cuentaInDTO) {
        return accountService.updateAccount(id, cuentaInDTO)
                .map(ResponseEntity::ok) // Devuelve 200 si la actualización fue exitosa.
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Devuelve 404 si no se encuentra.
    }

    @Operation(
            summary = "Delete Account",
            description = "Deletes a specific bank account by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Account not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable String id) {
        return accountService.deleteAccount(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }



}
