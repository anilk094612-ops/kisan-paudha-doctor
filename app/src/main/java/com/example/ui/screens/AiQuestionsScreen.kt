package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiQuestionsScreen(viewModel: PlantDoctorViewModel) {
  val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()
  val plantAge by viewModel.plantAge.collectAsStateWithLifecycle()
  val symptomLocation by viewModel.symptomLocation.collectAsStateWithLifecycle()
  val hasInsects by viewModel.hasInsects.collectAsStateWithLifecycle()
  val flowerDropping by viewModel.flowerDropping.collectAsStateWithLifecycle()
  val leavesYellowing by viewModel.leavesYellowing.collectAsStateWithLifecycle()
  val freeFormText by viewModel.freeFormProblemText.collectAsStateWithLifecycle()
  val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "कुछ आसान सवाल",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.CropSelection) },
            modifier = Modifier.testTag("back_to_crops_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "वापस जाएं",
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
    if (isAnalyzing) {
      // Analyzing state screen
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(64.dp),
              color = ForestGreen,
              strokeWidth = 6.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
              text = "फोटो का विश्लेषण हो रहा है...",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "AI आपकी फसल (${selectedCrop.nameHindi}) और लक्षणों का विश्लेषण कर रहा है",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1B241A),
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
              modifier = Modifier.fillMaxWidth(),
              color = HarvestAmber
            )
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Banner
        item {
          val isDark = isSystemInDarkTheme()
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDark) Color(0xFF142614) else Color(0xFFE8F3E4)
            ),
            border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFF8BA683)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = selectedCrop.emoji, fontSize = 36.sp)
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(
                  text = "फसल: ${selectedCrop.nameHindi}",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color.White else ForestGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "सटीक परिणाम हेतु नीचे केवल 2-3 सरल विकल्प चुनें:",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF142C14)
                )
              }
            }
          }
        }

        // Question 1: पौधे की उम्र कितनी है?
        item {
          QuestionCard(
            questionNumber = "1",
            questionTitle = "पौधे की उम्र कितनी है?"
          ) {
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf("0–30 दिन", "1–2 महीने", "2–3 महीने", "3 महीने से ज्यादा", "पता नहीं").forEach { opt ->
                OptionChip(
                  text = opt,
                  isSelected = plantAge == opt,
                  onSelect = { viewModel.plantAge.value = opt }
                )
              }
            }
          }
        }

        // Question 2: समस्या कहाँ दिखाई दे रही है?
        item {
          QuestionCard(
            questionNumber = "2",
            questionTitle = "समस्या कहाँ दिखाई दे रही है?"
          ) {
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf("पत्तियों में", "फूलों में", "फल में", "तने में", "जड़ में", "पूरे पौधे में").forEach { opt ->
                OptionChip(
                  text = opt,
                  isSelected = symptomLocation == opt,
                  onSelect = { viewModel.symptomLocation.value = opt }
                )
              }
            }
          }
        }

        // Question 3: क्या पौधे पर कीड़े दिखाई दे रहे हैं?
        item {
          QuestionCard(
            questionNumber = "3",
            questionTitle = "क्या पौधे पर कीड़े दिखाई दे रहे हैं?"
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              listOf("हाँ", "नहीं", "पता नहीं").forEach { opt ->
                OptionChip(
                  text = opt,
                  isSelected = hasInsects == opt,
                  modifier = Modifier.weight(1f),
                  onSelect = { viewModel.hasInsects.value = opt }
                )
              }
            }
          }
        }

        // Question 4: फूल लगकर गिर रहे हैं?
        item {
          QuestionCard(
            questionNumber = "4",
            questionTitle = "फूल लगकर गिर रहे हैं?"
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              listOf("हाँ", "नहीं", "पता नहीं").forEach { opt ->
                OptionChip(
                  text = opt,
                  isSelected = flowerDropping == opt,
                  modifier = Modifier.weight(1f),
                  onSelect = { viewModel.flowerDropping.value = opt }
                )
              }
            }
          }
        }

        // Question 5: पत्ते पीले पड़ रहे हैं?
        item {
          QuestionCard(
            questionNumber = "5",
            questionTitle = "पत्ते पीले पड़ रहे हैं?"
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              listOf("हाँ", "नहीं", "पता नहीं").forEach { opt ->
                OptionChip(
                  text = opt,
                  isSelected = leavesYellowing == opt,
                  modifier = Modifier.weight(1f),
                  onSelect = { viewModel.leavesYellowing.value = opt }
                )
              }
            }
          }
        }

        // Optional Farmer Note
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "कोई अन्य बात (वैकल्पिक):",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111410)
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedTextField(
                value = freeFormText,
                onValueChange = { viewModel.freeFormProblemText.value = it },
                placeholder = {
                  Text(
                    text = "जैसे: 3 दिन पहले पानी दिया था, अचानक पत्तियां मुड़ गईं",
                    color = Color(0xFF333E30),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                  )
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_optional_notes"),
                maxLines = 3
              )
            }
          }
        }

        // CTA Analyze Button
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = { viewModel.analyzePlant() },
            modifier = Modifier
              .fillMaxWidth()
              .height(64.dp)
              .testTag("btn_start_ai_diagnosis"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "जांच करें और समाधान जानें ▶",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
fun QuestionCard(
  questionNumber: String,
  questionTitle: String,
  content: @Composable () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .background(if (isDark) Color(0xFF2E7D32) else ForestGreen, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = questionNumber,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = questionTitle,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDark) Color.White else Color(0xFF111410)
        )
      }
      Spacer(modifier = Modifier.height(14.dp))
      content()
    }
  }
}

@Composable
fun OptionChip(
  text: String,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onSelect: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  FilterChip(
    selected = isSelected,
    onClick = onSelect,
    label = {
      Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) Color.White else if (isDark) Color.White else Color(0xFF111410),
        textAlign = TextAlign.Center
      )
    },
    leadingIcon = if (isSelected) {
      {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    } else null,
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = if (isDark) Color(0xFF2E7D32) else ForestGreen,
      selectedLabelColor = Color.White,
      containerColor = if (isDark) Color(0xFF1E2D1E) else Color.White,
      labelColor = if (isDark) Color.White else Color(0xFF111410)
    ),
    border = FilterChipDefaults.filterChipBorder(
      enabled = true,
      selected = isSelected,
      borderColor = if (isDark) Color(0xFF4CAF50) else Color(0xFF72846E),
      selectedBorderColor = if (isDark) Color(0xFF81C784) else ForestGreen,
      borderWidth = if (isSelected) 2.dp else 1.5.dp
    ),
    shape = RoundedCornerShape(12.dp),
    modifier = modifier.height(48.dp)
  )
}
