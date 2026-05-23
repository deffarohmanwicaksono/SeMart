package com.Kelompok4.semart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.Kelompok4.semart.navigation.NavGraph
import com.Kelompok4.semart.ui.theme.SeMartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeMartTheme {
                NavGraph()
            }
        }
    }
}