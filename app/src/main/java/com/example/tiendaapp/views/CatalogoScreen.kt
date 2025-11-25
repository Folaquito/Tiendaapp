package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tiendaapp.Helper.toClp
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.viewmodel.JuegoViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    gamesViewModel: JuegoViewModel,
    cartViewModel: CartViewModel
) {

    val juegos by gamesViewModel.games.collectAsState()
    val externos by gamesViewModel.externalGames.collectAsState()
    val externalError by gamesViewModel.externalError.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Juegos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ExternalApiSection(
                    externalGames = externos,
                    errorMessage = externalError,
                    onRetry = { gamesViewModel.loadExternalGames() }
                )
            }

            items(juegos, key = { it.id }) { juego ->
                JuegoCard(
                    juego = juego,
                    onClick = {
                        navController.navigate("detalle/${juego.id}")
                    },
                    onAddToCart = {
                        cartViewModel.agregarAlCarrito(juego)
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuegoCard(
    juego: JuegoEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = juego.imageUrl,
                contentDescription = "Imagen de ${juego.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = juego.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Valoración: ⭐ ${juego.rating}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = juego.price.toClp(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    FilledTonalButton(onClick = onAddToCart) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExternalApiSection(
    externalGames: List<JuegoEntity>,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Recomendados desde RAWG (API externa)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Se muestran solo a modo informativo y se obtienen en vivo desde la API pública RAWG.io.",
                style = MaterialTheme.typography.bodyMedium
            )

            when {
                externalGames.isNotEmpty() -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(externalGames, key = { it.id }) { juego ->
                            ExternalGameCard(juego)
                        }
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintentar")
                    }
                }

                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExternalGameCard(juego: JuegoEntity) {
    Card(
        modifier = Modifier
            .width(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = juego.imageUrl,
                contentDescription = juego.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = juego.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐ ${juego.rating}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
