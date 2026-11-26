package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TropicalHomeScreen
import com.example.ui.TropicalViewModel
import com.example.ui.theme.TropicalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT,
            ),
        )
        setContent {
            TropicalTheme {
                val viewModel: TropicalViewModel = viewModel()
                TropicalHomeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
