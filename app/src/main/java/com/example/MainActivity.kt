package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiQuestionsScreen
import com.example.ui.screens.CropSelectionScreen
import com.example.ui.screens.DiagnosisResultScreen
import com.example.ui.screens.DiseaseLibraryScreen
import com.example.ui.screens.ExpertConsultationScreen
import com.example.ui.screens.FarmerDiaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyCropScreen
import com.example.ui.screens.PlantScanScreen
import com.example.ui.screens.ProblemHubScreen
import com.example.ui.screens.ScanHistoryScreen
import com.example.ui.screens.VoiceAssistantDialog
import com.example.ui.screens.WeatherScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Google Mobile Ads SDK safely in background
    try {
      MobileAds.initialize(this) { status ->
        android.util.Log.d("MainActivity", "AdMob initialized: $status")
      }
    } catch (e: Exception) {
      android.util.Log.e("MainActivity", "Error initializing MobileAds", e)
    }

    setContent {
      MyApplicationTheme {
        PlantDoctorApp()
      }
    }
  }
}

@Composable
fun PlantDoctorApp(viewModel: PlantDoctorViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  var showVoiceDialog by remember { mutableStateOf(false) }

  // Handle system back navigation to return home gracefully
  if (currentScreen != Screen.Home) {
    BackHandler {
      when (currentScreen) {
        Screen.CropSelection -> viewModel.navigateTo(Screen.PlantScan)
        Screen.AiQuestions -> viewModel.navigateTo(Screen.CropSelection)
        Screen.DiagnosisResult -> viewModel.navigateTo(Screen.Home)
        else -> viewModel.navigateTo(Screen.Home)
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (currentScreen) {
      Screen.Home -> HomeScreen(
        viewModel = viewModel,
        onOpenVoiceDialog = { showVoiceDialog = true }
      )
      Screen.PlantScan -> PlantScanScreen(viewModel = viewModel)
      Screen.CropSelection -> CropSelectionScreen(viewModel = viewModel)
      Screen.AiQuestions -> AiQuestionsScreen(viewModel = viewModel)
      Screen.DiagnosisResult -> DiagnosisResultScreen(viewModel = viewModel)
      Screen.ProblemHub -> ProblemHubScreen(viewModel = viewModel)
      Screen.MyCropScreen -> MyCropScreen(viewModel = viewModel)
      Screen.FarmerDiary -> FarmerDiaryScreen(viewModel = viewModel)
      Screen.DiseaseLibrary -> DiseaseLibraryScreen(viewModel = viewModel)
      Screen.WeatherScreen -> WeatherScreen(viewModel = viewModel)
      Screen.ExpertHelp -> ExpertConsultationScreen(viewModel = viewModel)
      Screen.AdminPanel -> AdminPanelScreen(viewModel = viewModel)
      Screen.ScanHistory -> ScanHistoryScreen(viewModel = viewModel)
    }

    if (showVoiceDialog) {
      VoiceAssistantDialog(
        viewModel = viewModel,
        onDismiss = { showVoiceDialog = false }
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
