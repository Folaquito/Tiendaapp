package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder

import androidx.compose.ui.text.font.FontStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    gamesViewModel: JuegoViewModel,
    cartViewModel: CartViewModel
) {

    val juegos by gamesViewModel.games.collectAsState()
    val favorites by gamesViewModel.favorites.collectAsState()
    var showFavorites by remember { mutableStateOf(false) }

    // State for Note Dialog
    var showNoteDialog by remember { mutableStateOf(false) }
    var currentNoteGameId by remember { mutableStateOf<Int?>(null) }
    var currentNoteContent by remember { mutableStateOf("") }

    val displayedGames = if (showFavorites) {
        juegos.filter { game -> favorites.any { it.gameId == game.id } }
    } else {
        juegos
    }

    if (showNoteDialog && currentNoteGameId != null) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Nota Personal") },
            text = {
                OutlinedTextField(
                    value = currentNoteContent,
                    onValueChange = { currentNoteContent = it },
                    label = { Text("Escribe una nota...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    gamesViewModel.updateNote(currentNoteGameId!!, currentNoteContent)
                    showNoteDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showFavorites) "Mis Favoritos" else "Catálogo de Juegos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { showFavorites = !showFavorites }) {
                        Icon(
                            imageVector = if (showFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Ver Favoritos",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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

            items(displayedGames, key = { it.id }) { juego ->
                val favoriteGame = favorites.find { it.gameId == juego.id }
                val isFavorite = favoriteGame != null
                val note = favoriteGame?.note

                JuegoCard(
                    juego = juego,
                    isFavorite = isFavorite,
                    note = note,
                    onToggleFavorite = { gamesViewModel.toggleFavorite(juego) },
                    onEditNote = {
                        currentNoteGameId = juego.id
                        currentNoteContent = note ?: ""
                        showNoteDialog = true
                    },
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
    isFavorite: Boolean,
    note: String?,
    onToggleFavorite: () -> Unit,
    onEditNote: () -> Unit,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = juego.imageUrl,
                    contentDescription = "Imagen de ${juego.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop,
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface
                    )
                }
            }

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

                if (isFavorite) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!note.isNullOrBlank()) {
                        Text(
                            text = "Nota: $note",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    TextButton(onClick = onEditNote) {
                        Text(if (note.isNullOrBlank()) "Agregar Nota Personal" else "Editar Nota")
                    }
                }

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
