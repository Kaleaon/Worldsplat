package com.worldsplat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Light = lightColorScheme()
private val Dark = darkColorScheme()

@Composable
fun WorldsplatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Light,
        typography = MaterialTheme.typography,
        content = content
    )
}
