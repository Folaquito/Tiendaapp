package com.example.tiendaapp

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
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
import com.example.tiendaapp.data.local.AppDatabase
import com.example.tiendaapp.data.remote.RetrofitClient
import com.example.tiendaapp.repository.JuegoRepository
import com.example.tiendaapp.ui.theme.TiendaappTheme
import com.example.tiendaapp.viewmodel.CartViewModel
import com.example.tiendaapp.viewmodel.JuegoViewModel
import com.example.tiendaapp.viewmodel.JuegoViewModelFactory
import com.example.tiendaapp.viewmodel.LoginViewModel
import com.example.tiendaapp.views.AddProductScreen
import com.example.tiendaapp.views.BackOfficeScreen
import com.example.tiendaapp.views.CartScreen
import com.example.tiendaapp.views.CatalogoScreen
import com.example.tiendaapp.views.DetalleJuegoScreen
import com.example.tiendaapp.views.HomeScreen
import com.example.tiendaapp.views.LoginScreen
import com.example.tiendaapp.views.PurchaseErrorScreen
import com.example.tiendaapp.views.PurchaseSuccessScreen
import com.example.tiendaapp.views.RegisterScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val api = RetrofitClient.apiService
        val backend = RetrofitClient.backendService
        val microservice = RetrofitClient.microserviceApiService
        val repository = JuegoRepository(api, database.juegoDao(), backend, microservice)
        val viewModelFactory = JuegoViewModelFactory(repository)

        setContent {
            val navController = rememberNavController()
            val viewModel: LoginViewModel = viewModel()
            val cartViewModel: CartViewModel = viewModel()
            val juegoViewModel: JuegoViewModel = viewModel(
                factory = viewModelFactory
            )

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
                    CatalogoScreen(
                        navController = navController,
                        gamesViewModel = juegoViewModel,
                        cartViewModel = cartViewModel
                    )
                }
                composable("detalle/{juegoId}") { backStack ->
                    val id = backStack.arguments?.getString("juegoId")?.toIntOrNull() ?: -1
                    DetalleJuegoScreen(navController, id, juegoViewModel, cartViewModel)
                }
                composable("carrito") {
                    CartScreen(navController, cartViewModel, viewModel)
                }
                composable("compra_exitosa") {
                    PurchaseSuccessScreen(navController, cartViewModel)
                }
                composable("compra_rechazada") {
                    PurchaseErrorScreen(navController)
                }
                composable("backoffice") {
                    BackOfficeScreen(navController, juegoViewModel)
                }
                composable("backoffice/agregar") {
                    AddProductScreen(navController, juegoViewModel)
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