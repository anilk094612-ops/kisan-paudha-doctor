package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FarmerDiaryEntry
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FarmerDiaryScreen(viewModel: PlantDoctorViewModel) {
  val diaryEntries by viewModel.allDiaryEntries.collectAsStateWithLifecycle()
  val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()

  var quickAction by remember { mutableStateOf("💧 सिंचाई की") }
  var noteText by remember { mutableStateOf("") }
  var cropName by remember { mutableStateOf(selectedCrop.nameHindi) }

  val quickActions = listOf(
    "💧 सिंचाई की",
    "🌾 खाद डाली",
    "🌿 दवा/जैविक छिड़काव",
    "⚠️ बीमारी देखी",
    "🌼 फूल आए",
    "🍅 फल बने"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "किसान डायरी",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_diary_button")
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
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Add Entry Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, Color(0xFFD6E2D6)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "आज खेत में क्या काम किया?",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              quickActions.forEach { act ->
                val isSelected = quickAction == act
                FilterChip(
                  selected = isSelected,
                  onClick = { quickAction = act },
                  label = {
                    Text(
                      text = act,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else Color(0xFF111410)
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ForestGreen,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color(0xFF111410)
                  ),
                  border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFF72846E),
                    selectedBorderColor = ForestGreen,
                    borderWidth = if (isSelected) 2.dp else 1.5.dp
                  ),
                  shape = RoundedCornerShape(12.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = cropName,
              onValueChange = { cropName = it },
              label = { Text("फसल का नाम (उदा. टमाटर, मिर्च)", fontWeight = FontWeight.SemiBold, color = Color(0xFF111410)) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = noteText,
              onValueChange = { noteText = it },
              label = { Text("कोई विवरण (उदा. 200 ग्राम वर्मीकम्पोस्ट दी)", fontWeight = FontWeight.SemiBold, color = Color(0xFF111410)) },
              modifier = Modifier.fillMaxWidth(),
              maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = {
                viewModel.addDiaryEntry(
                  actionType = quickAction,
                  notes = noteText.ifBlank { "नियमित कार्य पूरा किया" },
                  crop = cropName.ifBlank { "सब्जी फसल" }
                )
                noteText = ""
              },
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_submit_diary")
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text("डायरी में दर्ज करें", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
          }
        }
      }

      // Past logs list
      item {
        Text(
          text = "पिछले कार्य व रिकॉर्ड:",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF111410)
        )
      }

      if (diaryEntries.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF8)),
            border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "📝", fontSize = 32.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "अभी कोई प्रविष्टि नहीं है। ऊपर से दर्ज करें।",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF262F23)
              )
            }
          }
        }
      } else {
        items(diaryEntries) { entry ->
          DiaryLogCard(
            entry = entry,
            onDelete = { viewModel.deleteDiaryEntry(entry.id) }
          )
        }
      }
    }
  }
}

@Composable
fun DiaryLogCard(
  entry: FarmerDiaryEntry,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = entry.actionTypeHindi,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "• ${entry.cropName}",
            fontSize = 14.sp,
            color = Color(0xFF111410),
            fontWeight = FontWeight.Bold
          )
        }
        if (entry.notesHindi.isNotBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = entry.notesHindi,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111410),
            lineHeight = 20.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = entry.dateDisplay,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF262F23)
        )
      }

      IconButton(onClick = onDelete) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "हटाएं", tint = Color(0xFFB71C1C))
      }
    }
  }
}
