package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.example.tiendaapp.model.CartItem
import com.example.tiendaapp.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val items by cartViewModel.items.collectAsState()
    val total = cartViewModel.calcularTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.game.id }) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = {
                                cartViewModel.eliminarDelCarrito(item.game.id)
                            },
                            onDecrease = {
                                cartViewModel.disminuirCantidad(item.game.id)
                            }
                        )
                    }
                }

                Divider()

                TotalSection(
                    total = total,
                    onPay = {
                        if (total > 0) {
                            cartViewModel.vaciarCarrito()
                            navController.navigate("compra_exitosa")
                        } else {
                            navController.navigate("compra_rechazada")
                        }
                    },
                    onRetry = {
                        navController.navigate("compra_rechazada")
                    }
                )
            }
        }
    }
}

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
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(16.dp))
        Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Explora nuestro catálogo y encuentra tus juegos favoritos")
        Spacer(Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack("catalogo", inclusive = false) }) {
            Text("Ir al Catálogo")
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AsyncImage(
                model = item.game.imageUrl,
                contentDescription = item.game.name,
                modifier = Modifier
                    .size(80.dp), // Tamaño cuadrado fijo
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.game.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rating: ⭐ ${item.game.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.game.price.toClp(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                OutlinedButton(
                    onClick = onDecrease,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (item.cantidad > 1) "-1" else "X",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cant: ${item.cantidad}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun TotalSection(total: Int, onPay: () -> Unit, onRetry: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total a pagar", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = total.toClp(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onPay
                ) {
                    Text("Pagar Ahora")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onRetry
                ) {
                    Text("Simular Error")
                }
            }
        }
    }
}
