package com.example.tiendaapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.LoginViewModel
import com.example.tiendaapp.R
import kotlinx.coroutines.launch

private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFD500F9)
private val DarkBg = Color(0xFF0B1221)
private val TextWhite = Color(0xFFEEEEEE)
private val ErrorRed = Color(0xFFFF5252)
private val MenuBg = Color(0xFF1F2536)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: LoginViewModel) {
    // --- ESTADOS (Tu lógica original intacta) ---
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var regionExpanded by remember { mutableStateOf(false) }
    var comunaExpanded by remember { mutableStateOf(false) }

    val regionesYComunas = mapOf(
        "Arica y Parinacota" to listOf("Arica", "Camarones", "Putre", "General Lagos"),
        "Valparaíso" to listOf("Valparaíso", "Viña del Mar", "Quilpué", "Villa Alemana", "San Antonio"),
        "Metropolitana" to listOf("Santiago", "Providencia", "Las Condes", "Maipú", "Puente Alto", "La Florida"),
        "Biobío" to listOf("Concepción", "Talcahuano", "Chiguayante", "San Pedro de la Paz"),
        "Araucanía" to listOf("Temuco", "Padre Las Casas", "Villarrica", "Pucón")
    )

    val rutErrorMessage by viewModel.rutError
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // --- FUNCIONES DE VALIDACIÓN ---
    fun validarEmail(input: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return regex.matches(input)
    }

    fun validarPassword(input: String): Boolean = input.length >= 6

    val isButtonEnabled = nombre.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            rut.isNotBlank() &&
            direccion.isNotBlank() &&
            region.isNotBlank() &&
            comuna.isNotBlank() &&
            rutErrorMessage == null &&
            emailError == null &&
            passwordError == null

    // --- UI PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg) // CAMBIO: Fondo oscuro
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.tiendita_app_moviles),
            contentDescription = "Logo de la tienda",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Registro",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite, // CAMBIO: Texto blanco
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // USAMOS EL COMPONENTE PERSONALIZADO "GamerTextField"
        GamerTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre"
        )

        Spacer(modifier = Modifier.height(12.dp))

        GamerTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = when {
                    it.isBlank() -> "El correo es obligatorio"
                    !validarEmail(it) -> "Formato de correo inválido"
                    else -> null
                }
            },
            label = "Email",
            isError = emailError != null,
            errorMessage = emailError
        )

        Spacer(modifier = Modifier.height(12.dp))

        GamerTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = when {
                    it.isBlank() -> "La contraseña es obligatoria"
                    !validarPassword(it) -> "Debe tener al menos 6 caracteres"
                    else -> null
                }
            },
            label = "Contraseña",
            isError = passwordError != null,
            errorMessage = passwordError,
            isPassword = true // Para ocultar caracteres si quieres implementarlo luego
        )

        Spacer(modifier = Modifier.height(12.dp))

        GamerTextField(
            value = rut,
            onValueChange = {
                rut = it
                if (rut.isNotBlank()) viewModel.validarRut(rut) else viewModel.rutError.value = null
            },
            label = "Rut",
            isError = rutErrorMessage != null,
            errorMessage = rutErrorMessage
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- DROPDOWN REGION (Estilizado) ---
        ExposedDropdownMenuBox(
            expanded = regionExpanded,
            onExpandedChange = { regionExpanded = !regionExpanded },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            // Usamos un TextField normal pero con los colores Gamer manuales
            OutlinedTextField(
                value = region,
                onValueChange = {},
                readOnly = true,
                label = { Text("Región", color = TextWhite) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = regionExpanded,
                onDismissRequest = { regionExpanded = false },
                modifier = Modifier.background(MenuBg).border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                regionesYComunas.keys.forEach { regionName ->
                    DropdownMenuItem(
                        text = { Text(regionName, color = TextWhite) },
                        onClick = {
                            region = regionName
                            comuna = ""
                            regionExpanded = false
                        }
                    )
                }
            }
        }

        // --- DROPDOWN COMUNA (Estilizado) ---
        ExposedDropdownMenuBox(
            expanded = comunaExpanded,
            onExpandedChange = { if (region.isNotEmpty()) comunaExpanded = !comunaExpanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = comuna,
                onValueChange = {},
                readOnly = true,
                label = { Text("Comuna", color = TextWhite) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = comunaExpanded) },
                enabled = region.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    disabledTextColor = Color.Gray,
                    disabledBorderColor = Color.DarkGray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = comunaExpanded,
                onDismissRequest = { comunaExpanded = false },
                modifier = Modifier.background(MenuBg).border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                regionesYComunas[region]?.forEach { comunaName ->
                    DropdownMenuItem(
                        text = { Text(comunaName, color = TextWhite) },
                        onClick = {
                            comuna = comunaName
                            comunaExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        GamerTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = "Dirección (Calle y número)"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTÓN CON DEGRADADO ---
        GamerButton(
            text = "Registrar",
            onClick = {
                scope.launch {
                    viewModel.registrar(
                        nombre,
                        email,
                        password,
                        rut,
                        direccion,
                        region,
                        comuna,
                        onSuccess = { navController.navigate("home/${'$'}{it.email}") },
                        onError = { }
                    )
                }
            },
            enabled = isButtonEnabled
        )

        if (viewModel.mensaje.value.isNotEmpty()) {
            Text(
                text = viewModel.mensaje.value,
                color = if(viewModel.mensaje.value.contains("Error")) ErrorRed else NeonCyan,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = NeonPurple, // Link en morado
                fontSize = 16.sp
            )
        }
    }
}

// ==========================================
// COMPONENTES PERSONALIZADOS (Reutilizables)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = NeonCyan,
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                cursorColor = NeonCyan,
                errorBorderColor = ErrorRed,
                errorLabelColor = ErrorRed,
                errorTextColor = TextWhite,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent
            )
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun GamerButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Definimos el degradado: Si está habilitado (Cyan->Morado), si no (Gris->GrisOscuro)
    val gradientColors = if (enabled) {
        listOf(NeonCyan, NeonPurple)
    } else {
        listOf(Color.Gray, Color.DarkGray)
    }

    val brush = Brush.horizontalGradient(gradientColors)

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // Hacemos transparente el contenedor nativo
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if(enabled) Color.White else Color.LightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}