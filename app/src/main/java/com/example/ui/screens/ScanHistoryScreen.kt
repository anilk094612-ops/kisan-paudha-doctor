package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ScanResult
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(viewModel: PlantDoctorViewModel) {
  val allScans by viewModel.allScans.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "पौधों का स्कैन इतिहास",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_history_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "मुख्य पृष्ठ",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = ForestGreen,
          titleContentColor = Color.White
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    if (allScans.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📷", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "अभी तक कोई स्कैन नहीं किया गया है",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "पौधे की फोटो खींचें और उसकी बीमारी या समस्या पहचानें।",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF262F23),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { viewModel.startNewScanFlow() },
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(50.dp)
          ) {
            Text("पहला स्कैन करें", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(allScans) { scan ->
          ScanHistoryCard(
            scan = scan,
            onClick = {
              // Open this diagnosis in result screen
              // Note: could set current diagnosis in VM and navigate
              viewModel.navigateTo(Screen.DiagnosisResult)
            }
          )
        }
      }
    }
  }
}

@Composable
fun ScanHistoryCard(
  scan: ScanResult,
  onClick: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  val dateFormatted = try {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale("hi", "IN"))
    sdf.format(Date(scan.timestamp))
  } catch (e: Exception) {
    "हाल ही में"
  }

  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isDark) Color(0xFF263C26) else PaleGreenBg,
            border = BorderStroke(1.dp, if (isDark) Color(0xFF81C784) else Color(0xFF81C784))
          ) {
            Text(
              text = "🌱 ${scan.cropName}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else ForestGreen,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = scan.confidenceHindi,
            fontSize = 13.sp,
            color = if (isDark) Color(0xFFFFB74D) else HarvestAmber,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = scan.problemHindi,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDark) Color.White else Color(0xFF111410)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = dateFormatted,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF262F23)
        )
      }

      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = "देखें",
        tint = if (isDark) Color(0xFF81C784) else ForestGreen
      )
    }
  }
}
