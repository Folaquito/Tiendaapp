package com.example.tiendaapp.views

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tiendaapp.viewmodel.LoginViewModel

// --- COLORES ---
private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFD500F9)
private val DarkBg = Color(0xFF0B1221)
private val TextWhite = Color(0xFFEEEEEE)
private val ErrorRed = Color(0xFFFF5252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // --- NUEVO: Estado de Carga Local ---
    var isLoading by remember { mutableStateOf(false) }
    // ------------------------------------

    val loginMessage by remember { viewModel.mensaje }
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val executor = remember { ContextCompat.getMainExecutor(context) }

    // Función de Biometría (Igual que antes)
    fun launchBiometric() {
        if (activity == null) {
            Toast.makeText(context, "Error de hardware", Toast.LENGTH_SHORT).show()
            return
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso Biométrico")
            .setSubtitle("Inicia sesión con tu huella")
            .setNegativeButtonText("Usar contraseña")
            .build()
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val sharedPref = context.getSharedPreferences("TiendaAppPrefs", android.content.Context.MODE_PRIVATE)
                val savedEmail = sharedPref.getString("biometric_email", null)
                if (savedEmail != null) {
                    Toast.makeText(context, "Identidad confirmada", Toast.LENGTH_SHORT).show()
                    navController.navigate("home/$savedEmail")
                } else {
                    Toast.makeText(context, "No hay cuenta vinculada. Inicia sesión normal.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context, "Huella no reconocida", Toast.LENGTH_SHORT).show()
            }
        })
        biometricPrompt.authenticate(promptInfo)
    }

    // Validaciones
    fun validarEmail(valor: String): Boolean = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex().matches(valor)
    fun validarPassword(valor: String): Boolean = valor.length >= 6

    val canSubmit = email.isNotBlank() && password.isNotBlank() &&
            emailError == null && passwordError == null

    // UI Principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("SYSTEM LOGIN", style = MaterialTheme.typography.headlineMedium, color = NeonCyan, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(40.dp))

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (it.isBlank()) "Campo obligatorio" else if (!validarEmail(it)) "Correo inválido" else null
            },
            label = { Text("ID de Usuario") },
            isError = emailError != null,
            supportingText = { emailError?.let { msg -> Text(msg, color = ErrorRed) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.Gray,
                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                errorTextColor = TextWhite, errorBorderColor = ErrorRed,
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (it.isBlank()) "Campo obligatorio" else if (!validarPassword(it)) "Mínimo 6 caracteres" else null
            },
            label = { Text("Clave de Acceso") },
            isError = passwordError != null,
            supportingText = { passwordError?.let { msg -> Text(msg, color = ErrorRed) } },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.Gray,
                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                errorTextColor = TextWhite, errorBorderColor = ErrorRed,
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        val gradientColors = if (canSubmit && !isLoading) listOf(NeonCyan, NeonPurple) else listOf(Color.Gray, Color.DarkGray)
        val brush = Brush.horizontalGradient(gradientColors)

        Button(
            onClick = {
                if (email == "admin@gmail.com" && password == "Admin123") {
                    navController.navigate("backoffice")
                } else {
                    isLoading = true

                    viewModel.login(
                        email = email,
                        password = password,
                        onSuccess = {
                            isLoading = false
                            navController.navigate("home/$email")
                        },
                        onError = { mensajeError ->
                            isLoading = false
                            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            enabled = canSubmit && !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(brush),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = TextWhite,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "ACCEDER",
                        color = if(canSubmit) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        if (loginMessage.isNotEmpty() && !isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = loginMessage,
                color = if (loginMessage.contains("exitoso", true)) NeonCyan else ErrorRed,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("O inicia con biometría", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, NeonPurple, CircleShape)
                .background(Color.Transparent)
                .clickable { launchBiometric() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Fingerprint, null, tint = NeonPurple, modifier = Modifier.size(32.dp))
        }
    }
}