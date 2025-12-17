package com.example.tiendaapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tiendaapp.Helper.toClp
import com.example.tiendaapp.model.CartItem
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

// --- COLORES GLOBALES ---
private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFD500F9)
private val DarkBg = Color(0xFF0B1221)
private val MenuBg = Color(0xFF1F2536)
private val TextWhite = Color(0xFFEEEEEE)
private val ErrorRed = Color(0xFFFF5252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel, loginViewModel: LoginViewModel) {
    // 1. Observamos la lista de items. Si esta cambia, la pantalla se redibuja.
    val items by cartViewModel.items.collectAsState()
    val currentUser by loginViewModel.currentUser.collectAsState(initial = null)

    // --- CORRECCIÓN AQUÍ ---
    // En lugar de llamar a una función del ViewModel, calculamos el total aquí mismo
    // basándonos en la lista 'items' que acabamos de recibir.
    // Esto asegura que el total SIEMPRE coincida con lo que ves en pantalla.
    val total = remember(items) {
        items.sumOf { it.game.price * it.cantidad }
    }
    // -----------------------

    val isProcessing by cartViewModel.isProcessing.collectAsState()
    val errorMessage by cartViewModel.errorMessage.collectAsState()
    val scope = rememberCoroutineScope()
    var lastError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(errorMessage) {
        lastError = errorMessage
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Carrito", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (items.isEmpty()) {
                EmptyCart(navController)
            } else {
                // Lista de productos
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.game.id }) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { cartViewModel.eliminarDelCarrito(item.game.id) },
                            onDecrease = { cartViewModel.disminuirCantidad(item.game.id) }
                        )
                    }
                }

                Divider(color = Color.Gray.copy(alpha = 0.3f))

                // Sección de Pago
                TotalSection(
                    total = total,
                    isProcessing = isProcessing,
                    errorMessage = lastError,
                    onPay = {
                        if (total <= 0 || isProcessing) return@TotalSection
                        scope.launch {
                            val result = cartViewModel.realizarCompra(
                                currentUser = currentUser,
                                buyerName = currentUser?.name ?: "Cliente App",
                                buyerEmail = currentUser?.email ?: "cliente@example.com"
                            )
                            result.onSuccess {
                                navController.navigate("compra_exitosa")
                            }.onFailure {
                                navController.navigate("compra_rechazada")
                            }
                        }
                    },
                    onRetry = { navController.navigate("compra_rechazada") }
                )
            }
        }
    }
}

// ... (El resto de funciones: EmptyCart, CartItemCard, TotalSection se mantienen IGUAL) ...
// No es necesario repetir todo el código de abajo si ya lo tienes,
// el cambio importante fue solamente en la variable 'total' al inicio.
@Composable
private fun EmptyCart(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart, // Icono cambiado a carrito
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Tu carrito está vacío",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Explora nuestro catálogo para encontrar tu próxima aventura.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(32.dp))

        // Botón estilo Gamer
        Button(
            onClick = { navController.popBackStack("catalogo", inclusive = false) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)))
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Ir al Catálogo", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenuBg // Tarjeta gris oscura
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen con bordes redondeados
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(80.dp)
            ) {
                AsyncImage(
                    model = item.game.imageUrl,
                    contentDescription = item.game.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Info Central
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.game.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rating: ⭐ ${item.game.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Precio Unitario en Cyan
                Text(
                    text = item.game.price.toClp(),
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Controles de Cantidad
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = ErrorRed // Rojo neón para eliminar
                    )
                }

                // Botón Disminuir personalizado (Outlined pequeño)
                OutlinedButton(
                    onClick = onDecrease,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(width = 32.dp, height = 32.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPurple)
                ) {
                    Text(
                        text = if (item.cantidad > 1) "-" else "x",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "x${item.cantidad}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextWhite
                )
            }
        }
    }
}

@Composable
private fun TotalSection(total: Int, isProcessing: Boolean, errorMessage: String?, onPay: () -> Unit, onRetry: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MenuBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fila de Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a pagar", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Text(
                    text = total.toClp(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan // Cyan Grande y Brillante
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón "Simular Error" (Estilo Danger Outlined)
                OutlinedButton(
                    modifier = Modifier.weight(1f).height(50.dp),
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text("Simular Error", fontWeight = FontWeight.Bold)
                }

                // Botón "Pagar Ahora" (Estilo Gradient Principal)
                Button(
                    modifier = Modifier.weight(1f).height(50.dp),
                    onClick = onPay,
                    enabled = total > 0 && !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Pagar Ahora", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}