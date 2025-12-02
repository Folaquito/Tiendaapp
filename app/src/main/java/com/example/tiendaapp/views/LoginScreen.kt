package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validarEmail(valor: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return regex.matches(valor)
    }

    fun validarPassword(valor: String): Boolean = valor.length >= 6

    val canSubmit = email.isNotBlank() && password.isNotBlank() &&
            emailError == null && passwordError == null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Inicio de Sesión", style = MaterialTheme.typography.titleLarge)

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
            supportingText = {
                emailError?.let { Text(it) }
            }
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
            supportingText = {
                passwordError?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (email == "admin@gmail.com" && password == "Admin123") {
                    navController.navigate("backoffice")
                }
                else {
                    if (viewModel.login(email, password)) {
                        navController.navigate("home/$email")
                    } else {
                    }
                }
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}
