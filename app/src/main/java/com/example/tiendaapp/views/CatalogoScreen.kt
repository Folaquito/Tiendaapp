package com.example.tiendaapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tiendaapp.Helper.toClp
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.viewmodel.JuegoViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.tiendaapp.R
import kotlinx.coroutines.launch

// --- COLORES GLOBALES ---
private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFD500F9)
private val DarkBg = Color(0xFF0B1221)
private val MenuBg = Color(0xFF1F2536)
private val TextWhite = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    gamesViewModel: JuegoViewModel,
    cartViewModel: CartViewModel
) {

    val juegos by gamesViewModel.games.collectAsState()
    val favorites by gamesViewModel.favorites.collectAsState()

    // --- ESTADOS DE FILTRO ---
    var showFavorites by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var selectedPlatform by remember { mutableStateOf<String?>(null) }

    // --- ESTADOS DE NOTAS ---
    var showNoteDialog by remember { mutableStateOf(false) }
    var currentNoteGameId by remember { mutableStateOf<Int?>(null) }
    var currentNoteContent by remember { mutableStateOf("") }

    // --- ESTADOS PARA SNACKBAR (NUEVO) ---
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Lógica de listas únicas
    val availableGenres = remember(juegos) {
        juegos.flatMap { it.genres ?: emptyList() }.map { it.name }.distinct().sorted()
    }
    val availablePlatforms = remember(juegos) {
        juegos.flatMap { it.platforms ?: emptyList() }.map { it.platform.name }.distinct().sorted()
    }

    // Lógica de filtrado
    val displayedGames = remember(juegos, favorites, showFavorites, selectedGenre, selectedPlatform) {
        juegos.filter { game ->
            val matchesFavorite = if (showFavorites) favorites.any { it.gameId == game.id } else true
            val matchesGenre = selectedGenre == null || game.genres?.any { it.name == selectedGenre } == true
            val matchesPlatform = selectedPlatform == null || game.platforms?.any { it.platform.name == selectedPlatform } == true
            matchesFavorite && matchesGenre && matchesPlatform
        }
    }

    // --- DIÁLOGO DE NOTAS ---
    if (showNoteDialog && currentNoteGameId != null) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            containerColor = MenuBg,
            titleContentColor = NeonCyan,
            textContentColor = TextWhite,
            title = { Text("Nota Personal") },
            text = {
                OutlinedTextField(
                    value = currentNoteContent,
                    onValueChange = { currentNoteContent = it },
                    label = { Text("Escribe una nota...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        gamesViewModel.updateNote(currentNoteGameId!!, currentNoteContent)
                        showNoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) { Text("Guardar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBg,
        // --- AGREGAMOS EL HOST DEL SNACKBAR ---
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MenuBg, // Fondo oscuro estilo Gamer
                    contentColor = NeonCyan, // Texto Neón
                    shape = RoundedCornerShape(8.dp),
                    actionColor = NeonPurple
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (showFavorites) "Mis Favoritos" else "Catálogo",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg,
                    titleContentColor = NeonCyan,
                    actionIconContentColor = NeonCyan
                ),
                actions = {
                    IconButton(onClick = { showFavorites = !showFavorites }) {
                        Icon(
                            imageVector = if (showFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Ver Favoritos",
                            tint = if (showFavorites) Color.Red else NeonCyan
                        )
                    }
                    IconButton(onClick = { navController.navigate("carrito") }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Ir al Carrito",
                            tint = NeonCyan
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // --- BARRA DE FILTROS ---
            if (!showFavorites) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterDropdown(
                        label = "Género",
                        options = availableGenres,
                        selectedOption = selectedGenre,
                        onOptionSelected = { selectedGenre = it },
                        modifier = Modifier.weight(1f)
                    )
                    FilterDropdown(
                        label = "Plataforma",
                        options = availablePlatforms,
                        selectedOption = selectedPlatform,
                        onOptionSelected = { selectedPlatform = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (selectedGenre != null || selectedPlatform != null) {
                    Text(
                        text = "Encontrados: ${displayedGames.size} juegos",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = NeonPurple
                    )
                }
                Divider(color = Color.Gray.copy(alpha = 0.2f))
            }

            // --- LISTA DE JUEGOS ---
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (displayedGames.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron juegos.", color = Color.Gray)
                        }
                    }
                }

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
                        onClick = { navController.navigate("detalle/${juego.id}") },
                        onAddToCart = {
                            // --- LÓGICA DE AGREGAR + MENSAJE ---
                            cartViewModel.agregarAlCarrito(juego)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "¡${juego.name} agregado al carrito!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor = if (selectedOption != null) NeonCyan else Color.Gray

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (selectedOption != null) NeonCyan else TextWhite,
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedOption ?: label,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = TextWhite
                )
                if (selectedOption != null) {
                    IconButton(
                        onClick = { onOptionSelected(null) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = NeonCyan)
                    }
                } else {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextWhite)
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 250.dp)
                .background(MenuBg)
                .border(1.dp, Color.Gray.copy(0.3f), RoundedCornerShape(4.dp))
        ) {
            DropdownMenuItem(
                text = { Text("Todos", color = TextWhite) },
                onClick = {
                    onOptionSelected(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = TextWhite) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
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
    val context = LocalContext.current
    val imageModel = remember(juego.imageUrl) {
        ImageRequest.Builder(context)
            .data(juego.imageUrl.ifBlank { null })
            .crossfade(true)
            .build()
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenuBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Imagen de ${juego.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_error),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐ ${juego.rating}", color = Color.Yellow, style = MaterialTheme.typography.labelMedium)
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else TextWhite
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = juego.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                if (!juego.genres.isNullOrEmpty()) {
                    Text(
                        text = juego.genres.take(2).joinToString(", ") { it.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isFavorite) {
                    if (!note.isNullOrBlank()) {
                        Text(
                            text = "Nota: $note",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = NeonPurple
                        )
                    }
                    TextButton(
                        onClick = onEditNote,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (note.isNullOrBlank()) "Agregar nota" else "Editar nota", color = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = juego.price.toClp(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontSize = 18.sp
                    )

                    Button(
                        onClick = onAddToCart,
                        enabled = juego.stock > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AddShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (juego.stock > 0) "Agregar" else "Sin stock",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}