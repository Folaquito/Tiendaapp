package com.example.tiendaapp.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController) {
    val nombre = remember { mutableStateOf("") }
    val precio = remember { mutableStateOf("") }
    val plataforma = remember { mutableStateOf("") }
    val genero = remember { mutableStateOf("") }
    val descripcion = remember { mutableStateOf("") }
    val stock = remember { mutableStateOf("") }
    val mensaje = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UploadBox()
            ProductField(label = "Nombre del juego", value = nombre.value) { nombre.value = it }
            ProductField(label = "Precio (CLP)", value = precio.value) { precio.value = it }
            ProductField(label = "Plataforma", value = plataforma.value) { plataforma.value = it }
            ProductField(label = "Género", value = genero.value) { genero.value = it }
            ProductField(
                label = "Descripción",
                value = descripcion.value,
                singleLine = false,
                modifier = Modifier.height(120.dp)
            ) { descripcion.value = it }
            ProductField(label = "Stock disponible", value = stock.value) { stock.value = it }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val campos = listOf(nombre.value, precio.value, plataforma.value, genero.value, descripcion.value, stock.value)
                    val todosCompletos = campos.all { it.isNotBlank() }
                    mensaje.value = if (todosCompletos) {
                        "Producto guardado (vista demostrativa)"
                    } else {
                        "Completa todos los campos para continuar"
                    }
                }
            ) {
                Text("Guardar producto")
            }
            if (mensaje.value.isNotBlank()) {
                Text(
                    text = mensaje.value,
                    color = if (mensaje.value.startsWith("Producto")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun UploadBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Subir imagen",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Text("Subir imagen")
        Text("PNG, JPG hasta 5MB", style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductField(
    label: String,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine
    )
}
