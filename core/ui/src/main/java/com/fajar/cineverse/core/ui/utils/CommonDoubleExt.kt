package com.fajar.cineverse.core.ui.utils

fun Double.formatDecimalPlaces(decimalPlaces: Int = 2): String {
    return "%.${decimalPlaces}f".format(this)
}