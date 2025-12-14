package com.example.tiendaapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.JuegoViewModel

private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFD500F9)
private val DarkBg = Color(0xFF0B1221)
private val MenuBg = Color(0xFF1F2536)
private val TextWhite = Color(0xFFEEEEEE)
private val ErrorRed = Color(0xFFFF5252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController, juegoViewModel: JuegoViewModel) {
    val nombre = remember { mutableStateOf("") }
    val precio = remember { mutableStateOf("") }
    val imagenUrl = remember { mutableStateOf("") }
    val valoracion = remember { mutableStateOf("4.5") }
    val descripcion = remember { mutableStateOf("") }
    val stock = remember { mutableStateOf("") }
    val mensaje = remember { mutableStateOf("") }

    val estadoMensaje by juegoViewModel.backOfficeMessage.collectAsState()
    val isLoading by juegoViewModel.isOperating.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Item", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = NeonPurple)
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UploadBox()

            Divider(color = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

            Text("Detalles del Producto", color = NeonPurple, fontWeight = FontWeight.Bold)

            AdminTextField(label = "Nombre del juego", value = nombre.value) { nombre.value = it }
            AdminTextField(label = "Precio (CLP)", value = precio.value) { precio.value = it }
            AdminTextField(label = "URL de imagen", value = imagenUrl.value) { imagenUrl.value = it }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(label = "Rating (0-5)", value = valoracion.value) { valoracion.value = it }
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(label = "Stock", value = stock.value) { stock.value = it }
                }
            }

            AdminTextField(
                label = "Descripción",
                value = descripcion.value,
                singleLine = false,
                modifier = Modifier.height(120.dp)
            ) { descripcion.value = it }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    val campos = listOf(nombre.value, precio.value, imagenUrl.value, valoracion.value, descripcion.value, stock.value)
                    val todosCompletos = campos.all { it.isNotBlank() }

                    if (!todosCompletos) {
                        mensaje.value = "Faltan campos por completar"
                        return@Button
                    }

                    val precioInt = precio.value.toIntOrNull()
                    val stockInt = stock.value.toIntOrNull()
                    val ratingDouble = valoracion.value.toDoubleOrNull()

                    if (precioInt == null || stockInt == null || ratingDouble == null) {
                        mensaje.value = "Error en campos numéricos"
                        return@Button
                    }

                    mensaje.value = ""
                    juegoViewModel.crearProducto(
                        nombre = nombre.value,
                        descripcion = descripcion.value,
                        imagen = imagenUrl.value,
                        precio = precioInt,
                        valoracion = ratingDouble.coerceIn(0.0, 5.0),
                        stock = stockInt
                    )
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isLoading) Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
                            else Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TextWhite)
                    } else {
                        Text("Guardar en Base de Datos", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (mensaje.value.isNotBlank()) {
                Text(
                    text = mensaje.value,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            estadoMensaje?.let {
                Text(
                    text = it,
                    color = if (it.contains("error", ignoreCase = true)) ErrorRed else NeonCyan,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun UploadBox() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MenuBg.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { /* Acción futura de abrir galería */ }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = "Subir",
                tint = NeonPurple,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("Toca para subir carátula", color = TextWhite, fontWeight = FontWeight.Bold)
            Text("PNG, JPG (Max 5MB)", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminTextField(
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
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonPurple,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = NeonPurple,
            unfocusedLabelColor = Color.Gray,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = NeonPurple,
            focusedContainerColor = MenuBg,
            unfocusedContainerColor = MenuBg
        )
    )
}