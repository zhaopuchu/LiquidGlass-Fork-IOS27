package com.linrjk.liquid.consumer

import android.app.Activity
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.linrjk.liquid.shapes.RoundedRectangle

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    Box(Modifier.fillMaxSize()) {
                        Box(
                            Modifier
                                .size(120f.dp)
                                .background(Color.White, RoundedRectangle(32f.dp))
                        )
                    }
                }
            }
        )
    }
}
