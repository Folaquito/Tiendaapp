package com.example.tiendaapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiendaapp.viewmodel.JuegoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleJuegoScreen(juegoId: Int, viewModel: JuegoViewModel) {
    val juego = remember { viewModel.buscarProductoPorId(juegoId) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(juego?.nombre ?: "Detalle") })
    }) { padding ->
        juego?.let { p ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val context = LocalContext.current
                val resourceId = context.resources.getIdentifier(juego.imagen, "drawable", context.packageName)

                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Poster de ${juego.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Text(p.nombre, style = MaterialTheme.typography.titleLarge)
                Text("$${p.precio}", style = MaterialTheme.typography.titleMedium)
                Text(p.descripcion ?: "", style = MaterialTheme.typography.bodyMedium)
                Text(p.genero ?: "", style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = { /* TODO: agregar al carrito */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar al carrito")
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Producto no encontrado")
        }
    }
}