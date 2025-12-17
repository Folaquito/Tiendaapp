package com.example.tiendaapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tiendaapp.Helper.toClp
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.viewmodel.JuegoViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.tiendaapp.R
import kotlinx.coroutines.launch

private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFD500F9)
private val DarkBg = Color(0xFF0B1221)
private val MenuBg = Color(0xFF1F2536)
private val TextWhite = Color(0xFFEEEEEE)

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
    val context = LocalContext.current

    // Estado para el Snackbar (mensaje flotante)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // Para lanzar la animación del snackbar

    val imageRequest = remember(juego?.imageUrl) {
        ImageRequest.Builder(context)
            .data(juego?.imageUrl?.ifBlank { null })
            .crossfade(true)
            .build()
    }

    Scaffold(
        containerColor = DarkBg,
        // Agregamos el Host del Snackbar aquí
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                // Personalizamos el Snackbar para que sea estilo Gamer
                Snackbar(
                    snackbarData = data,
                    containerColor = MenuBg,
                    contentColor = NeonCyan,
                    shape = RoundedCornerShape(8.dp),
                    actionColor = NeonPurple
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = juego?.name ?: "Cargando...",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg,
                    titleContentColor = TextWhite
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ... (IMAGEN Y DATOS IGUAL QUE ANTES) ...
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = "Carátula de ${juego.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.ic_error),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                        )
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = juego.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = juego.price.toClp(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                GamerBadge(text = "⭐ ${juego.rating}")
                                if (juego.esrbRating != null) {
                                    GamerBadge(text = "ESRB: ${juego.esrbRating}")
                                }
                            }
                        }
                    }
                }
                item {
                    if (!juego.genres.isNullOrEmpty()) {
                        Column {
                            Text("Géneros", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(juego.genres) { genero ->
                                    GamerChip(text = genero.name)
                                }
                            }
                        }
                    }
                }
                item {
                    if (!juego.platforms.isNullOrEmpty()) {
                        Column {
                            Text("Plataformas", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(juego.platforms) { container ->
                                    GamerChip(text = container.platform.name, isPlatform = true)
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("Descripción", style = MaterialTheme.typography.titleMedium, color = NeonPurple, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = juego.description ?: "Sumérgete en esta aventura épica...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite,
                            lineHeight = 22.sp
                        )
                    }
                }

                // --- AQUÍ ESTÁ EL CAMBIO EN EL BOTÓN ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    val gradientBrush = Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))

                    Button(
                        onClick = {
                            cartViewModel.agregarAlCarrito(juego)
                            // 1. Quitamos la navegación
                            // navController.navigate("carrito")

                            // 2. Mostramos un mensaje de confirmación
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "¡${juego.name} agregado al carrito!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = (juego.stock > 0),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(gradientBrush),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Agregar al carrito",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }

                // ... (RESTO DE ITEMS: COMENTARIOS, ETC) ...
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Comentarios de la comunidad",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
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
            Box(
                Modifier.fillMaxSize().background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonCyan)
            }
        }
    }
}

// ... (GamerBadge, GamerChip, ComentarioCardSimple se mantienen igual abajo) ...
@Composable
fun GamerBadge(text: String) {
    Surface(
        color = MenuBg,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = TextWhite
        )
    }
}

@Composable
fun GamerChip(text: String, isPlatform: Boolean = false) {
    val colorAccent = if (isPlatform) NeonCyan else NeonPurple

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorAccent)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = TextWhite
        )
    }
}

@Composable
fun ComentarioCardSimple(texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MenuBg
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "💬",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "\"$texto\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = Color.LightGray
            )
        }
    }
}