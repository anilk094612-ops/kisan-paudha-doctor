package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppLanguage
import com.example.data.model.LanguageDictionary
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.LanguageSelectionDialog
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

// Play Store App Link (भविष्य में Play Store URL बदलने के लिए केवल इसे बदलें)
const val PLAY_STORE_APP_LINK = "[PLAY_STORE_LINK]"

fun shareApp(context: Context) {
  val shareMessage = """
🌱 किसान पौधा डॉक्टर AI
पौधे की फोटो खींचकर संभावित बीमारी, कीट और खेती की समस्या समझने में मदद लें।
ऐप डाउनलोड करके अपनी फसल की जांच करें:
$PLAY_STORE_APP_LINK
  """.trimIndent()

  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "किसान पौधा डॉक्टर AI")
    putExtra(Intent.EXTRA_TEXT, shareMessage)
  }
  context.startActivity(Intent.createChooser(intent, "दोस्तों को ऐप भेजें"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: PlantDoctorViewModel,
  onOpenVoiceDialog: () -> Unit
) {
  val weather by viewModel.weatherInfo.collectAsStateWithLifecycle()
  val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
  val strings = remember(currentLanguage) { LanguageDictionary.getStrings(currentLanguage) }
  var showLanguageDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current

  if (showLanguageDialog) {
    LanguageSelectionDialog(
      currentLanguage = currentLanguage,
      onLanguageSelected = { lang -> viewModel.setLanguage(lang) },
      onDismiss = { showLanguageDialog = false }
    )
  }

  Scaffold(
    topBar = {
      // Geometric Balance Header - Compact & High-Visibility Language Button
      Surface(
        color = GeometricSurface,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GeometricBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left side: Title & subtitle (bounded so it never overflows or crushes right buttons)
          Row(
            modifier = Modifier
              .weight(1f, fill = false)
              .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "🌱",
              fontSize = 22.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = strings.appName,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = ForestGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "AI फसल निदान",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A5647),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // Right side: Prominent Language Button + Share + History + Admin
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // 🌐 PROMINENT LANGUAGE SELECTOR BUTTON (Always visible, stands out)
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = ForestGreen,
              shadowElevation = 2.dp,
              modifier = Modifier
                .clickable { showLanguageDialog = true }
                .testTag("top_language_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "🌐", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = currentLanguage.nativeName,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "▾",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color.White.copy(alpha = 0.9f)
                )
              }
            }

            IconButton(
              onClick = { shareApp(context) },
              modifier = Modifier
                .size(34.dp)
                .testTag("top_share_button")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "दोस्तों को भेजें",
                tint = ForestGreen,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = { viewModel.navigateTo(Screen.ScanHistory) },
              modifier = Modifier
                .size(34.dp)
                .testTag("history_button")
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = strings.historyTitle,
                tint = ForestGreen,
                modifier = Modifier.size(18.dp)
              )
            }

            // Admin / Settings button
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SageContainer)
                .clickable { viewModel.navigateTo(Screen.AdminPanel) }
                .testTag("admin_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "⚙️", fontSize = 15.sp)
            }
          }
        }
      }
    },
    bottomBar = {
      // Sleek, compact bottom navigation bar (Saves over 80dp vertical height)
      Surface(
        color = GeometricSurface,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GeometricBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          NavTabItem(
            icon = "🏠",
            label = strings.homeTab,
            isActive = true,
            onClick = { /* Already on Home */ }
          )
          NavTabItem(
            icon = "🚜",
            label = strings.myCropsTitle,
            isActive = false,
            onClick = { viewModel.navigateTo(Screen.MyCropScreen) }
          )
          NavTabItem(
            icon = "🌾",
            label = strings.mandiBhavTitle,
            isActive = false,
            onClick = { viewModel.navigateTo(Screen.MandiBhav) }
          )
          NavTabItem(
            icon = "🌦️",
            label = strings.weatherTitle,
            isActive = false,
            onClick = { viewModel.navigateTo(Screen.WeatherScreen) }
          )
          NavTabItem(
            icon = "👨‍🌾",
            label = strings.expertHelpTitle,
            isActive = false,
            onClick = { viewModel.navigateTo(Screen.ExpertHelp) }
          )
        }
      }
    },
    containerColor = GeometricBg
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 0. All India Multi-Language Banner (Compact & Clearly Visible)
      item {
        Card(
          onClick = { showLanguageDialog = true },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = GeometricSurface),
          border = BorderStroke(1.5.dp, ForestGreen.copy(alpha = 0.4f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("language_switch_banner")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(SageContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🇮🇳", fontSize = 16.sp)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "भाषा: ${currentLanguage.nativeName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ForestGreen
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "(${currentLanguage.englishName})",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }
                Text(
                  text = "15+ भारतीय भाषाओं में उपलब्ध",
                  fontSize = 10.sp,
                  color = Color(0xFF4A5647)
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ForestGreen
            ) {
              Text(
                text = "बदलें ▾",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 1. Geometric Hero: 📷 पौधे की फोटो स्कैन करें (Compact, Height Optimized)
      item {
        Card(
          onClick = { viewModel.startNewScanFlow() },
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = OliveGreen),
          border = BorderStroke(3.dp, SageContainer),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("scan_plant_primary_button")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "📷", fontSize = 26.sp)
            }
            Text(
              text = strings.scanCropPhoto,
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = strings.scanCropPhotoSubtitle,
              fontSize = 12.sp,
              color = Color.White.copy(alpha = 0.90f)
            )
          }
        }
      }

      // 2. Geometric 2x2 Grid: अक्सर आने वाली समस्याएं (Compact)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GeometricQuickButton(
              emoji = "🌼",
              title = strings.flowerDropTitle,
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_flower_drop"),
              onClick = { viewModel.startProblemSpecialFlow("flower_drop") }
            )
            GeometricQuickButton(
              emoji = "🍃",
              title = strings.yellowLeavesTitle,
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_yellow_leaves"),
              onClick = { viewModel.startProblemSpecialFlow("yellow_leaves") }
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GeometricQuickButton(
              emoji = "🐛",
              title = strings.insectPestTitle,
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_insects"),
              onClick = { viewModel.startProblemSpecialFlow("insects") }
            )
            GeometricQuickButton(
              emoji = "🍅",
              title = strings.fruitNotSettingTitle,
              modifier = Modifier
                .weight(1f)
                .testTag("shortcut_fruit_issue"),
              onClick = { viewModel.startProblemSpecialFlow("fruit_issue") }
            )
          }
        }
      }

      // 3. AdMob Banner Ad (Cleanly placed in scroll feed, not blocking viewport)
      item {
        AdMobBannerView(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
        )
      }

      // 4. Geometric Weather Banner with Integrated Voice Trigger (Compact)
      item {
        Card(
          onClick = { viewModel.navigateTo(Screen.WeatherScreen) },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SageContainer),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("weather_banner_card")
        ) {
          Box(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White),
                contentAlignment = Alignment.Center
              ) {
                Text(text = weather.conditionEmoji.ifBlank { "🌦️" }, fontSize = 22.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "आज का मौसम • ${weather.temperature}",
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen,
                  fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                  text = weather.farmerAlertHindi.ifBlank { "आज बारिश की संभावना है। सिंचाई करने से पहले मौसम देखें।" },
                  fontSize = 11.sp,
                  color = OliveGreen,
                  lineHeight = 15.sp,
                  maxLines = 2
                )
              }
              Spacer(modifier = Modifier.width(38.dp)) // Space for mic button
            }

            // Mic action button top right
            Box(
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .size(36.dp)
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
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // 5. 🤝 दोस्तों को ऐप भेजें (Compact)
      item {
        Card(
          onClick = { shareApp(context) },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GeometricSurface),
          border = BorderStroke(1.5.dp, ForestGreen),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_share_app")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(SageContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🤝", fontSize = 22.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "🤝 ${strings.shareAppTitle}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                  text = "साथी किसानों की फसल सुरक्षा में मदद करें",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF262F23)
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = ForestGreen
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = "शेयर करें",
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "शेयर",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        }
      }

      // 6. Kisan Seva Kendra - Balanced Feature Navigation Cards (Compact)
      item {
        Text(
          text = "किसान सेवा केंद्र:",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = GeometricText
        )
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GeometricFeatureCard(
              emoji = "🌱",
              title = strings.problemHubTitle,
              subtitle = strings.problemHubSubtitle,
              modifier = Modifier
                .weight(1f)
                .testTag("btn_problem_hub"),
              onClick = { viewModel.navigateTo(Screen.ProblemHub) }
            )
            GeometricFeatureCard(
              emoji = "📚",
              title = strings.cropLibraryTitle,
              subtitle = "लक्षण व जैविक उपचार",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_disease_library"),
              onClick = { viewModel.navigateTo(Screen.DiseaseLibrary) }
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GeometricFeatureCard(
              emoji = "📖",
              title = strings.farmerDiaryTitle,
              subtitle = "सिंचाई व खाद रिकॉर्ड",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_farmer_diary"),
              onClick = { viewModel.navigateTo(Screen.FarmerDiary) }
            )
            GeometricFeatureCard(
              emoji = "👨‍🌾",
              title = strings.expertHelpTitle,
              subtitle = "विशेषज्ञ से पूछें",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_expert_help"),
              onClick = { viewModel.navigateTo(Screen.ExpertHelp) }
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GeometricFeatureCard(
              emoji = "🌾",
              title = strings.mandiBhavTitle,
              subtitle = strings.mandiBhavSubtitle,
              modifier = Modifier
                .weight(1f)
                .testTag("btn_mandi_bhav"),
              onClick = { viewModel.navigateTo(Screen.MandiBhav) }
            )
            GeometricFeatureCard(
              emoji = "📜",
              title = strings.historyTitle,
              subtitle = "पुरानी जांच रिपोर्ट",
              modifier = Modifier
                .weight(1f)
                .testTag("btn_scan_history_hub"),
              onClick = { viewModel.navigateTo(Screen.ScanHistory) }
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
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = GeometricSurface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    modifier = modifier.height(72.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(text = emoji, fontSize = 24.sp)
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111410),
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
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
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GeometricSurface),
    border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    modifier = modifier.height(64.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(SageContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(text = emoji, fontSize = 20.sp)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreen,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF262F23),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
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
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 4.dp, vertical = 3.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = icon,
      fontSize = 17.sp
    )
    Spacer(modifier = Modifier.height(1.dp))
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
      color = if (isActive) ForestGreen else Color(0xFF262F23),
      maxLines = 1
    )
  }
}
