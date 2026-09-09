package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisResultScreen(viewModel: PlantDoctorViewModel) {
  val diagnosis by viewModel.currentDiagnosis.collectAsStateWithLifecycle()
  val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
  val feedbackSubmitted by viewModel.feedbackSubmitted.collectAsStateWithLifecycle()

  var showFeedbackDialog by remember { mutableStateOf(false) }
  var feedbackReason by remember { mutableStateOf("") }
  var showDiaryAddedToast by remember { mutableStateOf(false) }
  var showCropAddedToast by remember { mutableStateOf(false) }

  val isDark = isSystemInDarkTheme()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "पौधे की जांच रिपोर्ट",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_home_top_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "मुख्य पृष्ठ",
              tint = Color.White
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.speakResultText() },
            modifier = Modifier.testTag("tts_button")
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "बोलकर सुनें",
              tint = if (isSpeaking) HarvestAmber else Color.White
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
    val result = diagnosis
    if (result == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "कोई रिपोर्ट उपलब्ध नहीं है।",
            fontSize = 16.sp,
            color = if (isDark) Color.White else Color(0xFF111410),
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(onClick = { viewModel.navigateTo(Screen.Home) }) {
            Text("मुख्य पृष्ठ पर जाएं", color = Color.White, fontWeight = FontWeight.Bold)
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
        // 1. Result Main Header Card (AI का मुख्य Answer)
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = BorderStroke(2.dp, if (isDark) Color(0xFF4CAF50) else LeafGreen),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("diagnosis_result_card")
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              // Tags Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Crop pill
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isDark) Color(0xFF263C26) else PaleGreenBg,
                  border = BorderStroke(1.dp, if (isDark) Color(0xFF81C784) else Color(0xFF81C784))
                ) {
                  Text(
                    text = "🌱 फसल: ${result.cropName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else ForestGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }

                // Confidence badge
                val confidenceBg = if (isDark) {
                  when (result.confidenceHindi) {
                    "उच्च संभावना" -> Color(0xFF1B381B)
                    "मध्यम संभावना" -> Color(0xFF382A10)
                    else -> Color(0xFF381414)
                  }
                } else {
                  when (result.confidenceHindi) {
                    "उच्च संभावना" -> Color(0xFFE8F5E9)
                    "मध्यम संभावना" -> Color(0xFFFFF8E1)
                    else -> Color(0xFFFFEBEE)
                  }
                }

                val confidenceText = if (isDark) {
                  when (result.confidenceHindi) {
                    "उच्च संभावना" -> Color(0xFF81C784)
                    "मध्यम संभावना" -> Color(0xFFFFB74D)
                    else -> Color(0xFFFF8A80)
                  }
                } else {
                  when (result.confidenceHindi) {
                    "उच्च संभावना" -> ForestGreen
                    "मध्यम संभावना" -> Color(0xFFE65100)
                    else -> WarningRed
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = confidenceBg,
                  border = BorderStroke(1.dp, confidenceText.copy(alpha = 0.7f))
                ) {
                  Text(
                    text = result.confidenceHindi,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = confidenceText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Heading: पौधे की समस्या (White on dark)
              Text(
                text = "🩺 पौधे की समस्या:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF111410)
              )
              Spacer(modifier = Modifier.height(4.dp))

              // Main AI Answer: Bright White on dark background
              Text(
                text = result.problemHindi,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else ForestGreen,
                lineHeight = 32.sp
              )

              if (result.isDemoMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "💡 कृषि विशेषज्ञ ज्ञानकोष (ऑफलाइन/डेमो मोड)",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF262F23)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Audio button
              Button(
                onClick = { viewModel.speakResultText() },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isSpeaking) HarvestAmber else (if (isDark) Color(0xFF2E7D32) else Color(0xFFE8F5E9)),
                  contentColor = if (isSpeaking || isDark) Color.White else ForestGreen
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_speak_audio")
              ) {
                Icon(
                  imageVector = Icons.Default.VolumeUp,
                  contentDescription = null,
                  tint = if (isSpeaking || isDark) Color.White else ForestGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (isSpeaking) "बोलना रोकें ⏹" else "🔊 पूरी रिपोर्ट बोलकर सुनें",
                  fontWeight = FontWeight.Bold,
                  color = if (isSpeaking || isDark) Color.White else ForestGreen
                )
              }
            }
          }
        }

        // 2. ⚠️ Important Disclaimer Note
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDark) Color(0xFF2C1E0A) else Color(0xFFFFF8E1)
            ),
            border = BorderStroke(1.5.dp, if (isDark) Color(0xFFFFB300) else Color(0xFFFFD54F)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = if (isDark) Color(0xFFFFD54F) else Color(0xFFF57F17),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "⚠️ ध्यान दें: फोटो के आधार पर यह केवल संभावित कारण है। सही कारण खेत की स्थिति देखकर ही निश्चित किया जा सकता है।",
                fontSize = 14.sp,
                color = if (isDark) Color.White else Color(0xFF331600),
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // 3. 🔎 संभावित कारण (Possible Causes)
        item {
          SectionCard(
            title = "🔎 संभावित कारण",
            headerColor = if (isDark) Color.White else ForestGreen,
            containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface,
            borderColor = if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8),
            dividerColor = if (isDark) Color(0xFF334A33) else Color(0xFFCDD6CA)
          ) {
            result.possibleCauses.forEach { cause ->
              Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(
                  text = "• ",
                  color = if (isDark) Color(0xFF81C784) else ForestGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = cause,
                  fontSize = 16.sp,
                  color = if (isDark) Color.White else Color(0xFF111410),
                  fontWeight = FontWeight.SemiBold,
                  lineHeight = 24.sp
                )
              }
            }
          }
        }

        // 4. 👀 क्या देखें (पौधे पर लक्षण) (Visible Symptoms)
        item {
          SectionCard(
            title = "👀 क्या देखें (पौधे पर लक्षण)",
            headerColor = if (isDark) Color.White else Color(0xFF01579B),
            containerColor = if (isDark) Color(0xFF132029) else MaterialTheme.colorScheme.surface,
            borderColor = if (isDark) Color(0xFF0288D1) else Color(0xFFBCC6B8),
            dividerColor = if (isDark) Color(0xFF263D4D) else Color(0xFFCDD6CA)
          ) {
            result.visibleSymptoms.forEach { sym ->
              Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(
                  text = "• ",
                  color = if (isDark) Color(0xFF81D4FA) else Color(0xFF01579B),
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = sym,
                  fontSize = 16.sp,
                  color = if (isDark) Color.White else Color(0xFF111410),
                  fontWeight = FontWeight.SemiBold,
                  lineHeight = 24.sp
                )
              }
            }
          }
        }

        // 5. ✅ क्या करें (तुरंत उपचार / अच्छी सलाह) (Actionable Steps)
        item {
          SectionCard(
            title = "✅ क्या करें (तुरंत उपचार)",
            headerColor = if (isDark) Color(0xFF81C784) else ForestGreen,
            containerColor = if (isDark) Color(0xFF142614) else Color(0xFFF1F8E9),
            borderColor = if (isDark) Color(0xFF4CAF50) else Color(0xFF81C784),
            dividerColor = if (isDark) Color(0xFF2E4D2E) else Color(0xFFC5E1A5)
          ) {
            result.recommendedActions.forEach { act ->
              Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(
                  text = "✔ ",
                  color = if (isDark) Color(0xFF81C784) else ForestGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = act,
                  fontSize = 16.sp,
                  color = if (isDark) Color.White else Color(0xFF0F360F),
                  lineHeight = 24.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // 6. ❌ क्या न करें (बचाव / सावधानियां) (Things to Avoid)
        item {
          SectionCard(
            title = "❌ क्या न करें (बचाव व सावधानियां)",
            headerColor = if (isDark) Color(0xFFFF5252) else WarningRed,
            containerColor = if (isDark) Color(0xFF281212) else Color(0xFFFFF0F0),
            borderColor = if (isDark) Color(0xFFFF5252) else Color(0xFFEF9A9A),
            dividerColor = if (isDark) Color(0xFF552222) else Color(0xFFFFCDD2)
          ) {
            result.thingsToAvoid.forEach { avoid ->
              Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(
                  text = "✖ ",
                  color = if (isDark) Color(0xFFFF5252) else WarningRed,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = avoid,
                  fontSize = 16.sp,
                  color = if (isDark) Color.White else Color(0xFF4A0A0A),
                  lineHeight = 24.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // 7. 📅 कब दोबारा जांच करें
        item {
          SectionCard(
            title = "📅 कब दोबारा जांच करें",
            headerColor = if (isDark) Color(0xFFFFE082) else Color(0xFF4E342E),
            containerColor = if (isDark) Color(0xFF221E14) else MaterialTheme.colorScheme.surface,
            borderColor = if (isDark) Color(0xFFFFB74D) else Color(0xFFBCC6B8),
            dividerColor = if (isDark) Color(0xFF4D422E) else Color(0xFFCDD6CA)
          ) {
            Text(
              text = result.whenToRecheckHindi,
              fontSize = 16.sp,
              color = if (isDark) Color.White else Color(0xFF111410),
              fontWeight = FontWeight.SemiBold,
              lineHeight = 24.sp
            )
          }
        }

        // 8. 👨🌾 कब विशेषज्ञ से संपर्क करें (महत्वपूर्ण चेतावनी - Bright RED badge)
        if (result.expertRequired || result.expertReasonHindi.isNotBlank()) {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF281212) else Color(0xFFFFEBEE)
              ),
              border = BorderStroke(1.5.dp, if (isDark) Color(0xFFFF5252) else Color(0xFFEF9A9A)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "👨🌾 कब कृषि विशेषज्ञ से संपर्क करें:",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color(0xFFFF5252) else WarningRed
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = if (result.expertReasonHindi.isNotBlank()) result.expertReasonHindi else "यदि 3 दिनों में कोई सुधार न दिखे या अन्य पौधों में भी लक्षण फैलें, तो तुरंत पास के कृषि विज्ञान केंद्र (KVK) से संपर्क करें।",
                  fontSize = 15.sp,
                  color = if (isDark) Color.White else Color(0xFF4A1010),
                  fontWeight = FontWeight.SemiBold,
                  lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = { viewModel.navigateTo(Screen.ExpertHelp) },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFFFF3D00) else WarningRed
                  ),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("विशेषज्ञ से पूछें", fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }
        }

        // 9. Feedback Section: क्या यह जानकारी आपके काम आई?
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDark) Color(0xFF182418) else Color(0xFFF1F8E9)
            ),
            border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFC5E1A5)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "क्या यह जानकारी आपके काम आई?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else ForestGreen
              )
              Spacer(modifier = Modifier.height(12.dp))

              if (feedbackSubmitted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF81C784) else LeafGreen
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "आपकी प्रतिक्रिया दर्ज कर ली गई है। धन्यवाद!",
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else ForestGreen,
                    fontWeight = FontWeight.Bold
                  )
                }
              } else {
                Row(
                  horizontalArrangement = Arrangement.spacedBy(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Button(
                    onClick = { viewModel.submitFeedback(true) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (isDark) Color(0xFF2E7D32) else ForestGreen,
                      contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("feedback_yes_button")
                  ) {
                    Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("👍 हाँ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                  }

                  OutlinedButton(
                    onClick = { showFeedbackDialog = true },
                    border = BorderStroke(1.5.dp, if (isDark) Color.White else Color(0xFF72846E)),
                    colors = ButtonDefaults.outlinedButtonColors(
                      contentColor = if (isDark) Color.White else Color(0xFF111410)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("feedback_no_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.ThumbDown,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp),
                      tint = if (isDark) Color.White else Color(0xFF111410)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "👎 नहीं",
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp,
                      color = if (isDark) Color.White else Color(0xFF111410)
                    )
                  }
                }
              }
            }
          }
        }

        // 10. Secondary Save Actions (Diary, My Crop, Home)
        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = {
                  viewModel.addDiaryEntry(
                    actionType = "बीमारी देखी / उपचार किया",
                    notes = "${result.cropName}: ${result.problemHindi}",
                    crop = result.cropName
                  )
                  showDiaryAddedToast = true
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier
                  .weight(1f)
                  .height(54.dp)
                  .testTag("btn_save_to_diary")
              ) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("📖 डायरी में जोड़ें", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }

              Button(
                onClick = {
                  viewModel.addMyCrop(
                    name = result.cropName,
                    emoji = "🌱",
                    plot = "मुख्य खेत / क्यारी",
                    sowingDate = "हाल ही में दर्ज",
                    ageDays = 35,
                    notes = result.problemHindi
                  )
                  showCropAddedToast = true
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HarvestAmber),
                modifier = Modifier
                  .weight(1f)
                  .height(54.dp)
                  .testTag("btn_save_to_my_crops")
              ) {
                Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("📋 मेरी फसल में जोड़ें", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }

            if (showDiaryAddedToast) {
              Text(
                text = "✓ किसान डायरी में सफलतापूर्वक दर्ज कर लिया गया!",
                color = if (isDark) Color(0xFF81C784) else LeafGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            }

            if (showCropAddedToast) {
              Text(
                text = "✓ 'मेरी फसल' सूची में जोड़ लिया गया!",
                color = if (isDark) Color(0xFFFFB74D) else HarvestAmber,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            }

            OutlinedButton(
              onClick = { viewModel.navigateTo(Screen.Home) },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_return_home"),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.5.dp, if (isDark) Color(0xFF81C784) else ForestGreen)
            ) {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = if (isDark) Color(0xFF81C784) else ForestGreen
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "🏠 मुख्य पृष्ठ पर लौटें",
                color = if (isDark) Color(0xFF81C784) else ForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }

    if (showFeedbackDialog) {
      AlertDialog(
        onDismissRequest = { showFeedbackDialog = false },
        title = {
          Text(
            text = "क्या समस्या गलत बताई गई?",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isDark) Color.White else ForestGreen
          )
        },
        text = {
          Column {
            Text(
              text = "कृपया बताएं ताकि AI और बेहतर सीख सके:",
              fontSize = 13.sp,
              color = if (isDark) Color(0xFFE0E0E0) else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = feedbackReason,
              onValueChange = { feedbackReason = it },
              placeholder = { Text("जैसे: पौधे में उकठा रोग लग रहा है, पर ब्लाइट बताया") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_feedback_reason"),
              maxLines = 3
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.submitFeedback(false, feedbackReason)
              showFeedbackDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
          ) {
            Text("भेजें", color = Color.White, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showFeedbackDialog = false }) {
            Text("रद्द करें", color = if (isDark) Color.White else Color.Unspecified)
          }
        }
      )
    }
  }
}

@Composable
fun SectionCard(
  title: String,
  headerColor: Color,
  containerColor: Color = MaterialTheme.colorScheme.surface,
  borderColor: Color = Color(0xFFBCC6B8),
  dividerColor: Color = Color(0xFFCDD6CA),
  content: @Composable () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.5.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = headerColor
      )
      Spacer(modifier = Modifier.height(8.dp))
      Divider(color = dividerColor)
      Spacer(modifier = Modifier.height(8.dp))
      content()
    }
  }
}

