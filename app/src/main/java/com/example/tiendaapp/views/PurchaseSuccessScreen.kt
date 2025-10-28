package com.example.tiendaapp.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSuccessScreen(navController: NavController, cartViewModel: CartViewModel) {
    val total = cartViewModel.calcularTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra exitosa") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack("catalogo", inclusive = false)
                    }) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_success),
                contentDescription = "Compra confirmada",
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = "¡Compra confirmada!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text("Tu pedido ha sido procesado exitosamente")
            OrderSummary(total)
            Button(onClick = {
                cartViewModel.vaciarCarrito()
                navController.navigate("catalogo") {
                    popUpTo("catalogo") { inclusive = true }
                }
            }) {
                Text("Volver al catálogo")
            }
        }
    }
}

@Composable
private fun OrderSummary(total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total pagado")
                Text("$${total}", fontWeight = FontWeight.Bold)
            }
            Text("Un correo de confirmación llegará en minutos")
        }
    }
    Spacer(Modifier.height(8.dp))
}
