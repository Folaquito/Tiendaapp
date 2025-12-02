package com.example.tiendaapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.LoginViewModel
import com.example.tiendaapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: LoginViewModel) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        Text("Registro", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = when {
                    it.isBlank() -> "El correo es obligatorio"
                    !validarEmail(it) -> "Formato de correo inválido"
                    else -> null
                }
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = when {
                    it.isBlank() -> "La contraseña es obligatoria"
                    !validarPassword(it) -> "Debe tener al menos 6 caracteres"
                    else -> null
                }
            },
            label = { Text("Contraseña") },
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rut,
            onValueChange = {
                rut = it
                if (rut.isNotBlank()) viewModel.validarRut(rut) else viewModel.rutError.value = null
            },
            label = { Text("Rut") },
            isError = rutErrorMessage != null,
            supportingText = { rutErrorMessage?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = regionExpanded,
            onExpandedChange = { regionExpanded = !regionExpanded },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = region,
                onValueChange = {},
                readOnly = true,
                label = { Text("Región") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = regionExpanded,
                onDismissRequest = { regionExpanded = false }
            ) {
                regionesYComunas.keys.forEach { regionName ->
                    DropdownMenuItem(
                        text = { Text(regionName) },
                        onClick = {
                            region = regionName
                            comuna = ""
                            regionExpanded = false
                        }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = comunaExpanded,
            onExpandedChange = { if (region.isNotEmpty()) comunaExpanded = !comunaExpanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = comuna,
                onValueChange = {},
                readOnly = true,
                label = { Text("Comuna") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = comunaExpanded) },
                enabled = region.isNotEmpty(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = comunaExpanded,
                onDismissRequest = { comunaExpanded = false }
            ) {
                regionesYComunas[region]?.forEach { comunaName ->
                    DropdownMenuItem(
                        text = { Text(comunaName) },
                        onClick = {
                            comuna = comunaName
                            comunaExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección (Calle y número)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.registrar(nombre, email, password, rut, direccion, region, comuna)
            },
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Text(viewModel.mensaje.value, modifier = Modifier.padding(top = 10.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}