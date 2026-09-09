package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.INITIAL_CROPS
import com.example.data.model.MyCrop
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCropScreen(viewModel: PlantDoctorViewModel) {
  val myCrops by viewModel.allMyCrops.collectAsStateWithLifecycle()
  var showAddDialog by remember { mutableStateOf(false) }

  var cropNameInput by remember { mutableStateOf("टमाटर") }
  var plotNameInput by remember { mutableStateOf("उत्तर वाला खेत") }
  var daysInput by remember { mutableStateOf("30") }
  var notesInput by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "मेरी फसल (फसल समय-सारणी)",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_my_crop_button")
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
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = LeafGreen,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_crop")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "नई फसल जोड़ें")
      }
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Timeline Guide Header
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = PaleGreenBg),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "🌱 वैज्ञानिक फसल समय-सारणी (Timeline):",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            TimelineRow(stage = "दिन 1", desc = "बुवाई / रोपाई व प्रथम नमी")
            TimelineRow(stage = "दिन 15", desc = "अंकुरण व शुरुआती पौधे की जांच")
            TimelineRow(stage = "दिन 30", desc = "पोषण, गुड़ाई व पहला जैविक छिड़काव")
            TimelineRow(stage = "दिन 45", desc = "फूल व कली जांच (कीट निगरानी)")
            TimelineRow(stage = "दिन 60+", desc = "फल विकास व रोग रोकथाम")
          }
        }
      }

      if (myCrops.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFD6E2D6)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "🌾", fontSize = 42.sp)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "अभी कोई फसल दर्ज नहीं है",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "अपनी खेत की फसलें जोड़ें ताकि समय पर देख-रेख कर सकें।",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF262F23),
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(50.dp)
              ) {
                Text("+ पहली फसल जोड़ें", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
              }
            }
          }
        }
      } else {
        items(myCrops) { crop ->
          CropProgressCard(
            crop = crop,
            onScan = {
              val match = INITIAL_CROPS.find { it.nameHindi == crop.cropNameHindi } ?: INITIAL_CROPS[0]
              viewModel.setCrop(match)
              viewModel.startNewScanFlow()
            },
            onDelete = { viewModel.deleteMyCrop(crop.id) }
          )
        }
      }
    }

    if (showAddDialog) {
      AlertDialog(
        onDismissRequest = { showAddDialog = false },
        title = {
          Text(
            text = "मेरी फसल जोड़ें",
            fontWeight = FontWeight.Bold,
            color = ForestGreen
          )
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = cropNameInput,
              onValueChange = { cropNameInput = it },
              label = { Text("फसल का नाम (उदा. टमाटर, मिर्च)") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = plotNameInput,
              onValueChange = { plotNameInput = it },
              label = { Text("खेत / क्यारी का नाम") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = daysInput,
              onValueChange = { daysInput = it },
              label = { Text("बुवाई के कितने दिन हो गए?") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = notesInput,
              onValueChange = { notesInput = it },
              label = { Text("कोई खास बात या किस्म") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val days = daysInput.toIntOrNull() ?: 30
              val emoji = when {
                cropNameInput.contains("टमाटर") -> "🍅"
                cropNameInput.contains("मिर्च") -> "🌶️"
                cropNameInput.contains("बैंगन") -> "🍆"
                cropNameInput.contains("खीरा") -> "🥒"
                cropNameInput.contains("भिंडी") -> "🫛"
                cropNameInput.contains("लौकी") -> "🎃"
                else -> "🌱"
              }
              viewModel.addMyCrop(
                name = cropNameInput.trim(),
                emoji = emoji,
                plot = plotNameInput.trim(),
                sowingDate = "दिन: $days",
                ageDays = days,
                notes = notesInput.trim()
              )
              showAddDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = LeafGreen)
          ) {
            Text("जोड़ें")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = false }) {
            Text("रद्द करें")
          }
        }
      )
    }
  }
}

@Composable
fun TimelineRow(stage: String, desc: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 3.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(6.dp),
      color = Color(0xFFC8E6C9),
      modifier = Modifier.width(60.dp)
    ) {
      Text(
        text = stage,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = ForestGreen,
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
      )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = desc,
      fontSize = 13.sp,
      color = Color(0xFF1B3B1B)
    )
  }
}

@Composable
fun CropProgressCard(
  crop: MyCrop,
  onScan: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = crop.cropEmoji, fontSize = 34.sp)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = crop.cropNameHindi,
              fontSize = 19.sp,
              fontWeight = FontWeight.ExtraBold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "खेत: ${crop.fieldOrPotName} • उम्र: ${crop.daysSinceSowing} दिन",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF111410)
            )
          }
        }
        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "हटाएं",
            tint = Color(0xFFB71C1C)
          )
        }
      }

      if (crop.notesHindi.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "टिप्पणी: ${crop.notesHindi}",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF262F23)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onScan,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
      ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "इस फसल की फोटो स्कैन करें", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
      }
    }
  }
}
