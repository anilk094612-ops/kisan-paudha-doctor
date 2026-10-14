package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.ui.components.AdConfig
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.LanguageSelectionDialog
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
  val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
  var showLanguageDialog by remember { mutableStateOf(false) }

  val helpfulCount = feedbacks.count { it.isHelpful }
  val incorrectCount = feedbacks.count { !it.isHelpful }
  val context = LocalContext.current

  var bannerInput by remember { mutableStateOf(AdConfig.productionBannerAdUnitId) }
  var interstitialInput by remember { mutableStateOf(AdConfig.productionInterstitialAdUnitId) }

  if (showLanguageDialog) {
    LanguageSelectionDialog(
      currentLanguage = currentLanguage,
      onLanguageSelected = { viewModel.setLanguage(it) },
      onDismiss = { showLanguageDialog = false }
    )
  }

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

      // App Language Settings (हिंदुस्तान की हर भाषा)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = PaleGreenBg),
          border = BorderStroke(1.5.dp, ForestGreen),
          modifier = Modifier.fillMaxWidth().testTag("admin_language_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🌐", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "ऐप की भाषा (App Language)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                  )
                  Text(
                    text = "वर्तमान: ${currentLanguage.nativeName} (${currentLanguage.englishName})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF263238)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = ForestGreen,
                modifier = Modifier.clickable { showLanguageDialog = true }
              ) {
                Text(
                  text = "भाषा बदलें",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "🇮🇳 उपलब्ध भाषाएं: हिन्दी, ਪੰਜਾਬੀ, বাংলা, मराठी, ગુજરાતી, తెలుగు, தமிழ், ಕನ್ನಡ, മലയാളം, ଓଡ଼ିଆ, অসমীয়া, भोजपुरी, हरियाणवी, اردو, English",
              fontSize = 12.sp,
              color = Color(0xFF37474F),
              lineHeight = 16.sp
            )
          }
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

      // Google AdMob & Live Impression Dashboard
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📢", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Google AdMob इम्प्रेशन डैशबोर्ड",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                  )
                  Text(
                    text = "लाइव एड ट्रैकिंग एवं इम्प्रेशन एनालिटिक्स",
                    fontSize = 12.sp,
                    color = Color.Gray
                  )
                }
              }

              TextButton(
                onClick = {
                  AdConfig.resetStats(context)
                  Toast.makeText(context, "इम्प्रेशन स्टैटिस्टिक्स रीसेट किए गए", Toast.LENGTH_SHORT).show()
                }
              ) {
                Text("रीसेट", fontSize = 12.sp, color = ForestGreen)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Impression Metrics Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = PaleGreenBg,
                border = BorderStroke(1.dp, Color(0xFFC8DEC8)),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "👁️ बैनर", fontSize = 11.sp, color = Color(0xFF2E5E2E))
                  Text(
                    text = "${AdConfig.bannerImpressionsCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                  )
                  Text(text = "इम्प्रेशन", fontSize = 10.sp, color = Color.Gray)
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = PaleGreenBg,
                border = BorderStroke(1.dp, Color(0xFFC8DEC8)),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "🎬 फुल-स्क्रीन", fontSize = 11.sp, color = Color(0xFF2E5E2E))
                  Text(
                    text = "${AdConfig.interstitialImpressionsCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                  )
                  Text(text = "इम्प्रेशन", fontSize = 10.sp, color = Color.Gray)
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF3F7F2),
                border = BorderStroke(1.dp, Color(0xFFD3E0D3)),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "📡 कुल अनुरोध", fontSize = 11.sp, color = Color(0xFF445544))
                  Text(
                    text = "${AdConfig.totalAdRequestsCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                  )
                  Text(text = "रिक्वेस्ट्स", fontSize = 10.sp, color = Color.Gray)
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Ad Status & Diagnostic
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (AdConfig.lastAdErrorCode != null) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
              border = BorderStroke(1.dp, if (AdConfig.lastAdErrorCode != null) Color(0xFFFFCC80) else Color(0xFFC8E6C9)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = if (AdConfig.lastAdErrorCode != null) "⚠️ विज्ञापन स्थिति:" else "🟢 विज्ञापन स्थिति:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (AdConfig.lastAdErrorCode != null) Color(0xFFE65100) else Color(0xFF1B5E20)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = AdConfig.lastAdStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF222222)
                  )
                }

                if (AdConfig.lastImpressionTime.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "🕒 अंतिम सफल इम्प्रेशन: ${AdConfig.lastImpressionTime}",
                    fontSize = 11.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                  )
                }

                // If error occurred, provide direct troubleshooting guidance
                if (AdConfig.lastAdErrorCode != null) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFFFCC80)))
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = when (AdConfig.lastAdErrorCode) {
                      3 -> "💡 नो-फिल (No Fill - Code 3): नए AdMob अकाउंट या बिना प्ले स्टोर लाइव ऐप में Google का विज्ञापन सर्वर खाली जवाब देता है। नीचे 'ऑटो-फ़ॉलबैक' चालू रखें ताकि टेस्ट विज्ञापन से इम्प्रेशन कभी न रुके!"
                      1 -> "💡 इनवैलिड रिक्वेस्ट (Code 1): दर्ज किया गया Ad Unit ID अमान्य है। AdMob कंसोल से नया Banner Ad Unit ID बनाकर नीचे पेस्ट करें।"
                      2 -> "💡 नेटवर्क त्रुटि (Code 2): इंटरनेट कनेक्शन उपलब्ध नहीं है।"
                      else -> "💡 त्रुटि कोड ${AdConfig.lastAdErrorCode}: AdMob आंतरिक स्थिति। पुनः प्रयास करें।"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFFBF360C),
                    lineHeight = 16.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch 1: Test Mode Toggle
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PaleGreenBg,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (!AdConfig.isTestMode) "🟢 लाइव रियल विज्ञापन (Live Real Ads)" else "🟡 टेस्ट मोड (Google Test Ads)",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (!AdConfig.isTestMode) ForestGreen else Color(0xFFC05621)
                    )
                    Text(
                      text = if (!AdConfig.isTestMode) "असली मोबाइल पर AdMob के लाइव विज्ञापन चलेंगे" else "परीक्षण हेतु गूगल के ऑफिशियल टेस्ट विज्ञापन चलेंगे",
                      fontSize = 12.sp,
                      color = Color(0xFF555555)
                    )
                  }
                  Switch(
                    checked = !AdConfig.isTestMode,
                    onCheckedChange = { isRealSelected ->
                      AdConfig.saveSettings(
                        context = context,
                        testMode = !isRealSelected,
                        autoFallback = AdConfig.autoFallbackToTestAd,
                        bannerUnitId = bannerInput,
                        interstitialUnitId = interstitialInput
                      )
                    },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = Color.White,
                      checkedTrackColor = ForestGreen
                    )
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFD0DCD0)))
                Spacer(modifier = Modifier.height(10.dp))

                // Switch 2: Auto Fallback Toggle
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "⚡ ऑटो-फ़ॉलबैक (Auto-Fallback)",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF262F23)
                    )
                    Text(
                      text = "अगर लाइव विज्ञापन में No-Fill या एरर आए, तो बैकअप टेस्ट विज्ञापन से इम्प्रेशन चालू रखें",
                      fontSize = 11.sp,
                      color = Color(0xFF666666)
                    )
                  }
                  Switch(
                    checked = AdConfig.autoFallbackToTestAd,
                    onCheckedChange = { fallbackEnabled ->
                      AdConfig.saveSettings(
                        context = context,
                        testMode = AdConfig.isTestMode,
                        autoFallback = fallbackEnabled,
                        bannerUnitId = bannerInput,
                        interstitialUnitId = interstitialInput
                      )
                    },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = Color.White,
                      checkedTrackColor = ForestGreen
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ad Unit ID Configuration Inputs
            Text(
              text = "⚙️ AdMob यूनिट आईडी कॉन्फ़िगरेशन",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "AdMob ऐप आईडी: ca-app-pub-3325064097619476~9127316401",
              fontSize = 12.sp,
              color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = bannerInput,
              onValueChange = { bannerInput = it },
              label = { Text("Real Banner Ad Unit ID") },
              placeholder = { Text("ca-app-pub-3325064097619476/...") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_banner_ad_unit_input"),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = interstitialInput,
              onValueChange = { interstitialInput = it },
              label = { Text("Real Interstitial Ad Unit ID") },
              placeholder = { Text("ca-app-pub-3325064097619476/...") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_interstitial_ad_unit_input"),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  AdConfig.saveSettings(
                    context = context,
                    testMode = AdConfig.isTestMode,
                    autoFallback = AdConfig.autoFallbackToTestAd,
                    bannerUnitId = bannerInput,
                    interstitialUnitId = interstitialInput
                  )
                  Toast.makeText(context, "विज्ञापन आईडी सफलतापूर्वक सुरक्षित की गई!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.weight(1f)
              ) {
                Text("💾 आईडी सुरक्षित करें", color = Color.White)
              }

              Button(
                onClick = {
                  // Switch to test mode for guaranteed working impression test
                  AdConfig.saveSettings(
                    context = context,
                    testMode = true,
                    autoFallback = true,
                    bannerUnitId = bannerInput,
                    interstitialUnitId = interstitialInput
                  )
                  Toast.makeText(context, "टेस्ट मोड सक्रिय! नीचे लाइव विज्ञापन देखें", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                modifier = Modifier.weight(1f)
              ) {
                Text("🧪 टेस्ट एड चलाएं", color = Color.White)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Ad Banner Preview directly in Admin Panel
            Text(
              text = "📲 लाइव विज्ञापन पूर्वावलोकन (Live Ad Preview):",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF262F23)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Embed AdMobBannerView so admin can verify ad load and watch impression counter update in real-time!
            AdMobBannerView()

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFE8F5E9),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "📌 महत्वपूर्ण जानकारी: Google AdMob नियमों के अनुसार नए बनाए गए AdMob अकाउंट या बिना Google Play Store लिस्टिंग वाले ऐप्स पर तुरंत लाइव विज्ञापन शुरू होने में 24-48 घंटे लग सकते हैं। ऑटो-फ़ॉलबैक चालू होने पर आपका ऐप कभी खाली नहीं दिखेगा और इम्प्रेशन हमेशा दर्ज होंगे।",
                fontSize = 11.sp,
                color = Color(0xFF1B5E20),
                modifier = Modifier.padding(10.dp),
                lineHeight = 16.sp
              )
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
