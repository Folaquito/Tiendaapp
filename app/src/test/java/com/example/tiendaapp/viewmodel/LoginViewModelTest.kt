package com.example.tiendaapp.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    private val viewModel = LoginViewModel()

    @Test
    fun validarRutCorrectoLimpiaError() {
        val esValido = viewModel.validarRut("12.345.678-5")

        assertTrue(esValido)
        assertNull(viewModel.rutError.value)
    }

    @Test
    fun validarRutInvalidoEntregaMensaje() {
        val esValido = viewModel.validarRut("12.345.678-4")

        assertFalse(esValido)
        assertTrue(viewModel.rutError.value?.contains("válido", ignoreCase = true) == true)
    }
}
