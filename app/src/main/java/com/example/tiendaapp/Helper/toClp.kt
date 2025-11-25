package com.example.tiendaapp.Helper

import java.text.NumberFormat
import java.util.Locale

fun Int.toClp(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(this)
}