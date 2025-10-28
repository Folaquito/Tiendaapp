package com.example.tiendaapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
                title = { Text("Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                    items(items, key = { it.juego.id }) { item ->
                        CartItemCard(item, onRemove = {
                            cartViewModel.eliminarDelCarrito(item.juego.id)
                        }, onDecrease = {
                            cartViewModel.disminuirCantidad(item.juego.id)
                        })
                    }
                }
                TotalSection(total = total, onPay = {
                    // Navegación sencilla que simula flujo de validación mostrado en clase
                    if (total > 0) {
                        navController.navigate("compra_exitosa")
                    } else {
                        navController.navigate("compra_rechazada")
                    }
                }, onRetry = {
                    navController.navigate("compra_rechazada")
                })
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
        Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Explora nuestro catálogo y encuentra tus juegos favoritos")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack("catalogo", inclusive = false) }) {
            Text("Explorar destacados")
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onDecrease: () -> Unit
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        item.juego.imagen,
        "drawable",
        context.packageName
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = item.juego.nombre,
                modifier = Modifier
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.background)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.juego.nombre, fontWeight = FontWeight.Bold)
                Text(item.juego.genero)
                Text("$${item.juego.precio}", color = MaterialTheme.colorScheme.primary)
                Text("Cantidad: ${item.cantidad}")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
                TextButton(onClick = onDecrease) {
                    Text("-1")
                }
            }
        }
    }
}

@Composable
private fun TotalSection(total: Int, onPay: () -> Unit, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", style = MaterialTheme.typography.titleMedium)
            Text("$${total}", style = MaterialTheme.typography.titleMedium)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onPay
            ) {
                Text("Pagar")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onRetry
            ) {
                Text("Reintentar")
            }
        }
    }
}
