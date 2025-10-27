package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: LoginViewModel) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val rutErrorMessage by viewModel.rutError

    val isButtonEnabled = nombre.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            rut.isNotBlank() &&
            direccion.isNotBlank() &&
            rutErrorMessage == null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") }
        )

        OutlinedTextField(
            value = rut,
            onValueChange = {
                rut = it
                if (rut.isNotBlank()) {
                    viewModel.validarRut(rut)
                } else {
                    viewModel.rutError.value = null
                }
            },
            label = { Text("Rut") },
            isError = rutErrorMessage != null,
            supportingText = {
                if (rutErrorMessage != null) {
                    Text(text = rutErrorMessage!!)
                }
            }
        )
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                viewModel.registrar(nombre, email, password, rut, direccion)
            },
            enabled = isButtonEnabled
        ) {
            Text("Registrar")
        }
        Text(viewModel.mensaje.value, modifier = Modifier.padding(top = 10.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}