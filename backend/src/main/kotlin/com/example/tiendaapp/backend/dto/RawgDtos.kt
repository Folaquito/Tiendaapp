package com.example.tiendaapp.backend.dto

data class RawgGameResponse(
    val id: Long?,
    val name: String?,
    val description_raw: String?,
    val description: String?,
    val background_image: String?,
    val metacritic: Int?,
    val rating: Double?,
    val esrb_rating: RawgEsrbRating?,
    val genres: List<RawgNamedItem>?,
    val platforms: List<RawgPlatformHolder>?
)

data class RawgGamesResponse(
    val results: List<RawgGameResponse>?
)

data class RawgEsrbRating(
    val name: String?
)

data class RawgNamedItem(
    val name: String?
)

data class RawgPlatformHolder(
    val platform: RawgNamedItem?
)

data class ImportRawgRequest(
    val precio: Int?,
    val stock: Int?
)
