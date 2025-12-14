package com.example.tiendaapp.backend.service

import com.example.tiendaapp.backend.dto.RawgGameResponse
import com.example.tiendaapp.backend.dto.RawgGamesResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.RestClientException
import org.springframework.web.server.ResponseStatusException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class RawgClient(
    builder: RestTemplateBuilder,
    @Value("\${rawg.apiKey:}") private val apiKey: String
) {

    private val restTemplate: RestTemplate = builder.build()
    private val baseUrl = "https://api.rawg.io/api"

    fun getGame(rawgId: Long): RawgGameResponse {
        if (apiKey.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "RAWG_API_KEY no configurada")
        }
        val encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
        val url = "$baseUrl/games/$rawgId?key=$encodedKey"
        return try {
            restTemplate.getForObject(url, RawgGameResponse::class.java)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Juego RAWG no encontrado")
        } catch (ex: HttpClientErrorException.NotFound) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Juego RAWG no encontrado")
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, "Error RAWG: ${ex.statusText}")
        } catch (ex: RestClientException) {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error conectando a RAWG: ${ex.message}")
        }
    }

    fun getGames(page: Int, pageSize: Int): RawgGamesResponse {
        if (apiKey.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "RAWG_API_KEY no configurada")
        }
        val encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
        val url = "$baseUrl/games?page=$page&page_size=$pageSize&key=$encodedKey"
        return try {
            restTemplate.getForObject(url, RawgGamesResponse::class.java)
                ?: RawgGamesResponse(emptyList())
        } catch (ex: HttpClientErrorException.NotFound) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Página RAWG no encontrada")
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, "Error RAWG: ${ex.statusText}")
        } catch (ex: RestClientException) {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error conectando a RAWG: ${ex.message}")
        }
    }
}
