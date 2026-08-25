package com.linrjk.liquid.sample

import androidx.compose.runtime.Composable

@Composable
fun Block(content: @Composable () -> Unit) {
    content()
}
