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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.data.model.ExpertInquiry
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertConsultationScreen(viewModel: PlantDoctorViewModel) {
  val inquiries by viewModel.allInquiries.collectAsStateWithLifecycle()
  val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()

  var farmerName by remember { mutableStateOf("") }
  var cropName by remember { mutableStateOf(selectedCrop.nameHindi) }
  var problemDesc by remember { mutableStateOf("") }
  var hasVoiceNote by remember { mutableStateOf(false) }
  var submittedSuccess by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "कृषि विशेषज्ञ से पूछें",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_expert_button")
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
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header Info
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7)),
          border = BorderStroke(1.5.dp, Color(0xFFFFCCBC)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "👨🌾", fontSize = 30.sp)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "कृषि विज्ञान केंद्र एवं विशेषज्ञ सहायता",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFFBF360C)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "यदि AI से स्पष्ट न हो तो सीधे विशेषज्ञ से परामर्श लें",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF3E2723)
                )
              }
            }
          }
        }
      }

      // Inquiry Form
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "अपनी समस्या का विवरण दर्ज करें:",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = farmerName,
              onValueChange = { farmerName = it },
              label = { Text("किसान का नाम", fontWeight = FontWeight.SemiBold, color = Color(0xFF111410)) },
              placeholder = { Text("उदा. रामसिंह पटेल") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_farmer_name")
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = cropName,
              onValueChange = { cropName = it },
              label = { Text("फसल का नाम", fontWeight = FontWeight.SemiBold, color = Color(0xFF111410)) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = problemDesc,
              onValueChange = { problemDesc = it },
              label = { Text("पौधे में क्या परेशानी दिख रही है?", fontWeight = FontWeight.SemiBold, color = Color(0xFF111410)) },
              placeholder = { Text("उदा. 5 दिन से फल में छेद हो रहे हैं, पत्ते मुड़ रहे हैं") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_farmer_problem"),
              maxLines = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Voice recording simulator chip
            Button(
              onClick = { hasVoiceNote = !hasVoiceNote },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (hasVoiceNote) HarvestAmber else Color(0xFFFFF3E0),
                contentColor = if (hasVoiceNote) Color.White else Color(0xFFB75500)
              ),
              border = BorderStroke(1.5.dp, if (hasVoiceNote) HarvestAmber else Color(0xFFB75500)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = if (hasVoiceNote) Color.White else Color(0xFFB75500))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (hasVoiceNote) "✓ आवाज रिकॉर्ड हो गई (10 सेकंड)" else "🎤 बोलकर ऑडियो संदेश जोड़ें (वैकल्पिक)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (hasVoiceNote) Color.White else Color(0xFFB75500)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = {
                if (problemDesc.isNotBlank()) {
                  viewModel.submitExpertInquiry(
                    farmerName = farmerName,
                    crop = cropName,
                    problem = problemDesc,
                    hasVoice = hasVoiceNote
                  )
                  problemDesc = ""
                  hasVoiceNote = false
                  submittedSuccess = true
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_submit_expert_inquiry")
            ) {
              Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "विशेषज्ञ को भेजें", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (submittedSuccess) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "✓ आपका प्रश्न कृषि विशेषज्ञ को भेज दिया गया है। जल्द ही सलाह प्राप्त होगी।",
                color = ForestGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Past Inquiries List
      item {
        Text(
          text = "आपके पूछे गए प्रश्न एवं स्थिति:",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF111410)
        )
      }

      if (inquiries.isEmpty()) {
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
              Text(text = "📩", fontSize = 32.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "अभी कोई प्रश्न लंबित नहीं है।",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF262F23)
              )
            }
          }
        }
      } else {
        items(inquiries) { inq ->
          InquiryCard(inquiry = inq)
        }
      }
    }
  }
}

@Composable
fun InquiryCard(inquiry: ExpertInquiry) {
  Card(
    shape = RoundedCornerShape(14.dp),
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
        Text(
          text = "फसल: ${inquiry.cropName}",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreen
        )
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFFFF8E1),
          border = BorderStroke(1.dp, Color(0xFFFFCC80))
        ) {
          Text(
            text = inquiry.statusHindi,
            fontSize = 12.sp,
            color = Color(0xFFE65100),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = inquiry.problemDescriptionHindi,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF111410),
        lineHeight = 21.sp
      )

      if (inquiry.hasVoiceNote) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "🎙️ ऑडियो संदेश संलग्न है",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFB75500)
        )
      }
    }
  }
}
