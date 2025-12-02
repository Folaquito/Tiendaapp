package com.example.tiendaapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tiendaapp.model.FakeDatabase
import com.example.tiendaapp.model.Usuario

class LoginViewModel : ViewModel() {
    var mensaje = mutableStateOf("")
    var usuarioActual = mutableStateOf<String?>(null)
    var rutError = mutableStateOf<String?>(null)

    fun registrar(nombre: String, email: String, password: String, rut: String, direccion: String, region: String, comuna: String) {
        if (!validarRut(rut)) {
            mensaje.value = "Registro fallido. Verifica el RUT."
            return
        }
        rutError.value = null
        val nuevo = Usuario(nombre, email, password, rut, direccion)
        if (FakeDatabase.registrar(nuevo)) {
            mensaje.value = "Registro exitoso ✅"
        } else {
            mensaje.value = "El usuario ya existe ❌"
        }
    }

    fun login(email: String, password: String): Boolean {
        return if (FakeDatabase.login(email, password)) {
            usuarioActual.value = email
            mensaje.value = "Inicio de sesión exitoso 🎉"
            true
        } else {
            mensaje.value = "Credenciales inválidas ❌"
            false
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