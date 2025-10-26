package com.example.tiendaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiendaapp.ui.theme.TiendaappTheme
import com.example.tiendaapp.viewmodel.JuegoViewModel
import com.example.tiendaapp.viewmodel.LoginViewModel
import com.example.tiendaapp.views.CatalogoScreen
import com.example.tiendaapp.views.DetalleJuegoScreen
import com.example.tiendaapp.views.HomeScreen
import com.example.tiendaapp.views.LoginScreen
import com.example.tiendaapp.views.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: LoginViewModel = viewModel()
            val juegoViewModel: JuegoViewModel = viewModel()

            NavHost(navController, startDestination = "register") {
                composable("register") {
                    RegisterScreen(navController, viewModel)
                }
                composable("login") {
                    LoginScreen(navController, viewModel)
                }
                composable("home/{email}") { backStack ->
                    val email = backStack.arguments?.getString("email")
                    HomeScreen(email, navController)
                }
                composable("catalogo") {
                    CatalogoScreen(navController = navController, juegoViewModel = juegoViewModel)
                }
                composable("detalle/{juegoId}") { backStack ->
                    val id = backStack.arguments?.getString("juegoId")?.toIntOrNull() ?: -1
                    DetalleJuegoScreen(id, juegoViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TiendaappTheme {
        Greeting("Android")
    }
}