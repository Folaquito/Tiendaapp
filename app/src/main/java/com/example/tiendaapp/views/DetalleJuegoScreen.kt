package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tiendaapp.Helper.toClp
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.viewmodel.JuegoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleJuegoScreen(
    navController: NavController,
    juegoId: Int,
    viewModel: JuegoViewModel,
    cartViewModel: CartViewModel
) {
    LaunchedEffect(juegoId) {
        viewModel.loadDescription(juegoId)
    }

    val juegoState by viewModel.getGameFlow(juegoId).collectAsState(initial = null)
    val juego = juegoState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(juego?.name ?: "Cargando...") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (juego != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = juego.imageUrl,
                            contentDescription = "Carátula de ${juego.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = juego.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = juego.price.toClp(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "Metacritic: ${juego.rating}",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Descripción", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (juego.description != null) {
                        Text(
                            text = juego.description, // ¡Aquí está tu descripción real!
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("Cargando descripción...", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Button(
                        onClick = {
                            cartViewModel.agregarAlCarrito(juego)
                            navController.navigate("carrito")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar al carrito")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Comentarios de la comunidad",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                val comentariosSimulados = listOf(
                    "¡Increíble juego, totalmente recomendado!",
                    "Los gráficos son espectaculares en esta versión.",
                    "El precio vale totalmente la pena."
                )

                items(comentariosSimulados) { comentario ->
                    ComentarioCardSimple(texto = comentario)
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
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
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}
