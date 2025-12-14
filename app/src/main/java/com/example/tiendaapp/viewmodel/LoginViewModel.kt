package com.example.tiendaapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaapp.data.remote.AuthResponse
import com.example.tiendaapp.data.remote.LoginRequest
import com.example.tiendaapp.data.remote.MicroserviceClient
import com.example.tiendaapp.data.remote.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var mensaje = mutableStateOf("")
    var usuarioActual = mutableStateOf<String?>(null)
    var rutError = mutableStateOf<String?>(null)

    private val _currentUser = MutableStateFlow<AuthResponse?>(null)
    val currentUser: StateFlow<AuthResponse?> = _currentUser.asStateFlow()

    fun registrar(
        nombre: String,
        email: String,
        password: String,
        rut: String,
        direccion: String,
        region: String,
        comuna: String,
        onSuccess: (AuthResponse) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (!validarRut(rut)) {
            mensaje.value = "Registro fallido. Verifica el RUT."
            return
        }
        rutError.value = null
        viewModelScope.launch {
            try {
                val response = MicroserviceClient.api.register(RegisterRequest(nombre, email, password))
                _currentUser.value = response
                usuarioActual.value = response.email
                mensaje.value = "Registro exitoso ✅"
                onSuccess(response)
            } catch (e: Exception) {
                mensaje.value = e.message ?: "Error en registro"
                onError(e.message ?: "Error en registro")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = MicroserviceClient.api.login(LoginRequest(email, password))
                _currentUser.value = response
                usuarioActual.value = response.email
                mensaje.value = "Inicio de sesión exitoso 🎉"
                onSuccess()
            } catch (e: Exception) {
                mensaje.value = "Credenciales inválidas ❌"
                onError(e.message ?: "Error de inicio de sesión")
            }
        }
    }
    private fun limpiarRut(rut: String): Pair<String, Char>? {
        val rutLimpio = rut.replace(Regex("[.\\s-]"), "")
        if (rutLimpio.length < 9) return null
        val cuerpo = rutLimpio.substring(0, rutLimpio.length - 1)
        val dvChar = rutLimpio.last().uppercaseChar()
        if (!cuerpo.matches(Regex("\\d+"))) return null
        return Pair(cuerpo, dvChar)
    }

    fun validarRut(rutCompleto: String): Boolean {
        val (cuerpo, dvIngresado) = limpiarRut(rutCompleto) ?: run {
            rutError.value = "Formato de RUT inválido."
            return false
        }
        var suma = 0
        var multiplicador = 2
        for (i in cuerpo.length - 1 downTo 0) {
            val digito = cuerpo[i].toString().toInt()
            suma += digito * multiplicador
            multiplicador++
            if (multiplicador > 7) {
                multiplicador = 2
            }
        }
        val resto = suma % 11
        val dvEsperadoInt = 11 - resto
        val dvEsperado: Char = when (dvEsperadoInt) {
            11 -> '0'
            10 -> 'K'
            else -> dvEsperadoInt.toString().single()
        }
        val esValido = dvEsperado == dvIngresado
        rutError.value = if (esValido) null else "El RUT no es matemáticamente válido."
        return esValido
    }
}