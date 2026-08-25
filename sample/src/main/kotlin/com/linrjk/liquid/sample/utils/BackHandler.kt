package com.linrjk.liquid.sample.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    BackHandler(enabled, onBack)
}
