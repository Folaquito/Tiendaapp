package com.example.tiendaapp.backend.controller

import com.example.tiendaapp.backend.dto.FavoriteDto
import com.example.tiendaapp.backend.dto.toDto
import com.example.tiendaapp.backend.dto.toEntity
import com.example.tiendaapp.backend.repository.FavoriteRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.transaction.annotation.Transactional

@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = ["http://localhost:8081", "http://10.0.2.2:8081", "*"])
class FavoriteController(
    private val repository: FavoriteRepository
) {

    @GetMapping
    fun list(): List<FavoriteDto> = repository.findAll().map { it.toDto() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun upsert(@RequestBody dto: FavoriteDto): FavoriteDto {
        val existing = repository.findByGameId(dto.gameId)
        val saved = repository.save(dto.toEntity(existing?.id))
        return saved.toDto()
    }

    @PutMapping("/{gameId}")
    fun update(
        @PathVariable gameId: Int,
        @RequestBody dto: FavoriteDto
    ): FavoriteDto {
        val existing = repository.findByGameId(gameId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "favorite no encontrado")

        val toSave = dto.copy(gameId = gameId, id = existing.id).toEntity(existing.id)
        return repository.save(toSave).toDto()
    }

    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun delete(@PathVariable gameId: Int) {
        repository.findByGameId(gameId)?.let { repository.deleteByGameId(gameId) }
    }
}
