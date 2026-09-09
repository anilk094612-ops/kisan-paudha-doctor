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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(viewModel: PlantDoctorViewModel) {
  val scanCount by viewModel.scanCount.collectAsStateWithLifecycle()
  val cropCount by viewModel.cropCount.collectAsStateWithLifecycle()
  val diaryCount by viewModel.diaryCount.collectAsStateWithLifecycle()
  val inquiryCount by viewModel.inquiryCount.collectAsStateWithLifecycle()
  val feedbacks by viewModel.allFeedback.collectAsStateWithLifecycle()

  val helpfulCount = feedbacks.count { it.isHelpful }
  val incorrectCount = feedbacks.count { !it.isHelpful }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "व्यवस्थापक डैशबोर्ड (Admin Panel)",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_admin_button")
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
          colors = CardDefaults.cardColors(containerColor = PaleGreenBg),
          border = BorderStroke(1.5.dp, Color(0xFF81C784)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Assessment,
              contentDescription = null,
              tint = ForestGreen,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "ऐप उपयोग सांख्यिकी एवं नियंत्रण",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ForestGreen
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "रोग निदान की गुणवत्ता एवं किसानों की प्रतिक्रिया",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F360F)
              )
            }
          }
        }
      }

      // Metrics Grid
      item {
        Text(
          text = "मुख्य मेट्रिक्स:",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF111410)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AdminMetricCard(
            title = "कुल स्कैन",
            value = "$scanCount",
            emoji = "📷",
            modifier = Modifier.weight(1f)
          )
          AdminMetricCard(
            title = "सक्रिय फसलें",
            value = "$cropCount",
            emoji = "🌾",
            modifier = Modifier.weight(1f)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AdminMetricCard(
            title = "डायरी रिकॉर्ड",
            value = "$diaryCount",
            emoji = "📖",
            modifier = Modifier.weight(1f)
          )
          AdminMetricCard(
            title = "विशेषज्ञ प्रश्न",
            value = "$inquiryCount",
            emoji = "👨🌾",
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Feedback Quality Stats
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "AI निदान सटीकता एवं प्रतिक्रिया:",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, tint = ForestGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "संतुष्ट किसान: $helpfulCount", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0A330A))
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ThumbDown, contentDescription = null, tint = WarningRed)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "असंतोष / सुधार: $incorrectCount", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WarningRed)
              }
            }
          }
        }
      }

      // Incorrect Diagnosis Reports
      item {
        Text(
          text = "किसानों द्वारा रिपोर्ट की गई आपत्तियां:",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF111410)
        )
      }

      val incorrectList = feedbacks.filter { !it.isHelpful && it.incorrectReasonHindi.isNotBlank() }
      if (incorrectList.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF8)),
            border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "अभी तक कोई गलत निदान रिपोर्ट नहीं किया गया है।",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF262F23),
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      } else {
        items(incorrectList) { fb ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            border = BorderStroke(1.5.dp, Color(0xFFEF9A9A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "स्कैन #${fb.scanId} पर आपत्ति:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WarningRed
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = fb.incorrectReasonHindi,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3E0C0C)
              )
            }
          }
        }
      }

      // Safe Agronomy Rules Compliance Verification
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = PaleGreenBg),
          border = BorderStroke(1.5.dp, Color(0xFF81C784)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "सुरक्षा दिशानिर्देश सक्रिय:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "• 100% सटीक निदान का दावा प्रतिबंधित है (हमेशा 'संभावित' प्रयुक्त)\n• रासायनिक कीटनाशकों का मनमाना डोज अवरुद्ध है\n• जैविक एवं देसी उपचार प्राथमिकता पर हैं",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF0F360F),
              lineHeight = 22.sp
            )
          }
        }
      }

      // Google AdMob Configuration Status
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "📢", fontSize = 22.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Google AdMob विज्ञापन कॉन्फ़िगरेशन",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PaleGreenBg,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "🟢 स्थिति: बैनर विज्ञापन सक्रिय (होम स्क्रीन)",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "• AdMob App ID: ca-app-pub-3325064097619476~9127316401\n• मोड: गूगल टेस्ट ऐड्स (सुरक्षित परीक्षण)\n• Test Ad Unit ID: ca-app-pub-3940256099942544/6300978111",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF262F23)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminMetricCard(
  title: String,
  value: String,
  emoji: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.height(92.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = emoji, fontSize = 28.sp)
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = value,
          fontSize = 22.sp,
          fontWeight = FontWeight.ExtraBold,
          color = ForestGreen
        )
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF111410)
        )
      }
    }
  }
}
