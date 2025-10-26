package com.example.tiendaapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(email: String?, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido 👋", style = MaterialTheme.typography.titleLarge)
        Text("Has iniciado sesión como: $email")
        Button(onClick = {
            navController.navigate("catalogo")
        }){
            Text("Continuar")
        }
    }
}