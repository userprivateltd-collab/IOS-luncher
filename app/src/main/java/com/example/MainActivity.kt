package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.LauncherViewModel
import com.example.ui.LauncherViewModelFactory
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SimulatedAppContainer
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize manual DI components
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(database.appDao())
        
        val viewModel: LauncherViewModel by viewModels {
            LauncherViewModelFactory(repository)
        }

        // 2. Index apps and build default layouts
        viewModel.initializeLauncher(applicationContext)

        setContent {
            val darkModeSetting by viewModel.isDarkMode.collectAsState()
            
            // Set dynamic dark mode according to setting preference
            val useDarkTheme = when (darkModeSetting) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Render launcher layout
                    HomeScreen(viewModel = viewModel)

                    // Overlay for interactive simulated full screen experiences (Safari, Weather, etc.)
                    SimulatedAppContainer(viewModel = viewModel)
                }
            }
        }
    }
}

