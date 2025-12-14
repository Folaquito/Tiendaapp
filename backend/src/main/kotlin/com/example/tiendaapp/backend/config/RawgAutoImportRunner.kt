package com.example.tiendaapp.backend.config

import com.example.tiendaapp.backend.repository.ProductoRepository
import com.example.tiendaapp.backend.service.RawgImportService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class RawgAutoImportRunner(
    private val productoRepository: ProductoRepository,
    private val rawgImportService: RawgImportService,
    @Value("\${app.rawg.auto-import.enabled:true}") private val enabled: Boolean,
    @Value("\${app.rawg.auto-import.page:1}") private val page: Int,
    @Value("\${app.rawg.auto-import.page-size:20}") private val pageSize: Int,
    @Value("\${app.rawg.auto-import.precio:24990}") private val defaultPrice: Int,
    @Value("\${app.rawg.auto-import.stock:5}") private val defaultStock: Int
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (!enabled) return
        val current = productoRepository.count()
        if (current > 3) return

        try {
            val imported = rawgImportService.importGames(page, pageSize, defaultPrice, defaultStock)
            logger.info("RAWG auto-import completado: ${imported.size} productos")
        } catch (ex: ResponseStatusException) {
            logger.warn("RAWG auto-import omitido: ${ex.reason}")
        } catch (ex: Exception) {
            logger.warn("RAWG auto-import falló: ${ex.message}")
        }
    }
}
