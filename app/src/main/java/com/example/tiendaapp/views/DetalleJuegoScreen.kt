package com.example.tiendaapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.tiendaapp.viewmodel.JuegoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleJuegoScreen(juegoId: Int, viewModel: JuegoViewModel) {
    val juego = remember { viewModel.buscarProductoPorId(juegoId) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(juego?.nombre ?: "Detalle") })
    }) { padding ->
        juego?.let { p ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item{
                    val context = LocalContext.current
                    val resourceId = context.resources.getIdentifier(juego.imagen, "drawable", context.packageName)
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = "Carátula de ${juego.nombre}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        )
                }
                item{
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        val context = LocalContext.current
                        val imgD1Id = context.resources.getIdentifier(p.imagend1, "drawable", context.packageName)
                        Image(
                            painter = painterResource(id = imgD1Id),
                            contentDescription = "Imagen extra de ${p.nombre}",
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                        )
                        val imgD2Id = context.resources.getIdentifier(p.imagend2, "drawable", context.packageName)
                        Image(
                            painter = painterResource(id = imgD2Id),
                            contentDescription = "Imagen extra de ${p.nombre}",
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                        )
                    }
                }
                item{
                    Text(p.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("$${p.precio}", style = MaterialTheme.typography.titleMedium)
                    Text(p.genero ?: "", style = MaterialTheme.typography.bodyMedium)
                    Text("Metacritic: ${p.valoracion}", style = MaterialTheme.typography.titleMedium)
                }
                item{
                    Text(p.descripcionlarga ?: "", style = MaterialTheme.typography.bodyMedium)
                }
                item{
                    Button(
                        onClick = { /* TODO: agregar al carrito */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar al carrito")
                    }
                }
                item {
                    Text(
                        "Comentarios de la comunidad",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                val comentarios = listOf(p.comentario1, p.comentario2).filter { it.isNotBlank() }
                items(comentarios) { comentario ->
                    ComentarioCardSimple(texto = comentario)
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Producto no encontrado")
        }
    }
}
@Composable
fun ComentarioCardSimple(texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = "\"$texto\"",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}