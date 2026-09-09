package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.ui.components.AdMobBannerView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GeometricBg
import com.example.ui.theme.GeometricBorder
import com.example.ui.theme.GeometricSurface
import com.example.ui.theme.GeometricText
import com.example.ui.theme.GeometricTextMuted
import com.example.ui.theme.GeometricTextSubtle
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.SageContainer
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: PlantDoctorViewModel,
  onOpenVoiceDialog: () -> Unit
) {
  val weather by viewModel.weatherInfo.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      // Geometric Balance Header
      Surface(
        color = GeometricSurface,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GeometricBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "🌱",
                fontSize = 24.sp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "किसान पौधा डॉक्टर AI",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = ForestGreen
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "आपकी फसल, आपकी मेहनत, बेहतर कमाई",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF262F23)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.navigateTo(Screen.ScanHistory) },
              modifier = Modifier
                .size(42.dp)
                .testTag("history_button")
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "स्कैन इतिहास",
                tint = ForestGreen
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
            // Geometric circular avatar button leading to Admin/Controls
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SageContainer)
                .clickable { viewModel.navigateTo(Screen.AdminPanel) }
                .testTag("admin_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "👤", fontSize = 20.sp)
            }
          }
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(GeometricSurface)
      ) {
        // Google AdMob Banner Ad (Cleanly demarcated, non-intrusive)
        AdMobBannerView()

        // Geometric Balance Bottom Navigation Bar
        Surface(
          color = GeometricSurface,
          modifier = Modifier.fillMaxWidth(),
          border = BorderStroke(1.dp, GeometricBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            NavTabItem(
              icon = "🏠",
              label = "होम",
              isActive = true,
              onClick = { /* Already on Home */ }
            )
            NavTabItem(
              icon = "🚜",
              label = "मेरी फसल",
              isActive = false,
              onClick = { viewModel.navigateTo(Screen.MyCropScreen) }
            )
            NavTabItem(
              icon = "🌦️",
              label = "मौसम",
              isActive = false,
              onClick = { viewModel.navigateTo(Screen.WeatherScreen) }
            )
            NavTabItem(
              icon = "👨🌾",
              label = "विशेषज्ञ",
              isActive = false,
              onClick = { viewModel.navigateTo(Screen.ExpertHelp) }
            )
          }
        }
      }
    },
    containerColor = GeometricBg
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Geometric Hero: 📷 पौधे की फोटो स्कैन करें (rounded-[2rem] 32dp, border-4 border-[#D7E8CD])
      item {
        Card(
          onClick = { viewModel.startNewScanFlow() },
          shape = RoundedCornerShape(32.dp),
          colors = CardDefaults.cardColors(containerColor = OliveGreen),
          border = BorderStroke(4.dp, SageContainer),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("scan_plant_primary_button")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "📷", fontSize = 34.sp)
            }
            Text(
              text = "पौधे की फोटो स्कैन करें",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "बीमारी और कीड़ों की तुरंत जांच करें",
              fontSize = 14.sp,
              color = Color.White.copy(alpha = 0.90f)
            )
          }
        }
      }

      // 2. Geometric 2x2 Grid: अक्सर आने वाली समस्याएं (rounded-3xl 24dp, border border-[#E1E3DA])
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            GeometricQuickButton(
              emoji = "🌼",
              title = "फूल गिर रहे हैं?",
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_flower_drop"),
              onClick = { viewModel.startProblemSpecialFlow("flower_drop") }
            )
            GeometricQuickButton(
              emoji = "🍃",
              title = "पत्ते पीले हैं?",
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_yellow_leaves"),
              onClick = { viewModel.startProblemSpecialFlow("yellow_leaves") }
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            GeometricQuickButton(
              emoji = "🐛",
              title = "कीड़े लग गए हैं?",
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_insects"),
              onClick = { viewModel.startProblemSpecialFlow("insects") }
            )
            GeometricQuickButton(
              emoji = "🍅",
              title = "फल नहीं लग रहा?",
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_fruit_issue"),
              onClick = { viewModel.startProblemSpecialFlow("fruit_issue") }
            )
          }
        }
      }

      // 3. Geometric Weather Banner with Integrated Voice Trigger (rounded-3xl, bg-[#D7E8CD])
      item {
        Card(
          onClick = { viewModel.navigateTo(Screen.WeatherScreen) },
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = SageContainer),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("weather_banner_card")
        ) {
          Box(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.White),
                contentAlignment = Alignment.Center
              ) {
                Text(text = weather.conditionEmoji.ifBlank { "🌦️" }, fontSize = 28.sp)
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "आज का मौसम • ${weather.temperature}",
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen,
                  fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = weather.farmerAlertHindi.ifBlank { "आज बारिश की संभावना है। सिंचाई करने से पहले मौसम देखें।" },
                  fontSize = 13.sp,
                  color = OliveGreen,
                  lineHeight = 18.sp,
                  maxLines = 2
                )
              }
              Spacer(modifier = Modifier.width(44.dp)) // Space for mic button
            }

            // Mic action button top right
            Box(
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(OliveGreen)
                .clickable { onOpenVoiceDialog() }
                .testTag("voice_problem_card"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "बोलकर समस्या बताएं",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }

      // 4. Kisan Seva Kendra - Balanced Feature Navigation Cards
      item {
        Text(
          text = "किसान सेवा केंद्र:",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = GeometricText
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            GeometricFeatureCard(
              emoji = "🌱",
              title = "फसल समस्या हब",
              subtitle = "रोग व पोषण निवारण",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_problem_hub"),
              onClick = { viewModel.navigateTo(Screen.ProblemHub) }
            )
            GeometricFeatureCard(
              emoji = "📚",
              title = "रोग पहचान कोष",
              subtitle = "लक्षण व जैविक उपचार",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_disease_library"),
              onClick = { viewModel.navigateTo(Screen.DiseaseLibrary) }
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            GeometricFeatureCard(
              emoji = "📖",
              title = "किसान डायरी",
              subtitle = "सिंचाई व खाद रिकॉर्ड",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_farmer_diary"),
              onClick = { viewModel.navigateTo(Screen.FarmerDiary) }
            )
            GeometricFeatureCard(
              emoji = "👨🌾",
              title = "वैज्ञानिक सलाह",
              subtitle = "विशेषज्ञ से पूछें",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_expert_help"),
              onClick = { viewModel.navigateTo(Screen.ExpertHelp) }
            )
          }
        }
      }

      // 5. Disclaimer Notice (High contrast, clearly legible)
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "यह AI संभावित समस्या बताता है। गंभीर समस्या में कृषि विशेषज्ञ की सलाह लें।",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF262F23),
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }
  }
}

@Composable
fun GeometricQuickButton(
  emoji: String,
  title: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = GeometricSurface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.height(112.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(text = emoji, fontSize = 34.sp)
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111410),
        textAlign = TextAlign.Center,
        maxLines = 2
      )
    }
  }
}

@Composable
fun GeometricFeatureCard(
  emoji: String,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = GeometricSurface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.height(90.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(SageContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(text = emoji, fontSize = 24.sp)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(verticalArrangement = Arrangement.Center) {
        Text(
          text = title,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreen,
          maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF262F23),
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun NavTabItem(
  icon: String,
  label: String,
  isActive: Boolean,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = icon,
      fontSize = 22.sp
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
      color = if (isActive) ForestGreen else Color(0xFF262F23)
    )
  }
}
