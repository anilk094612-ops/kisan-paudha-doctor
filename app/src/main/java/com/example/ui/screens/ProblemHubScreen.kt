package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemHubScreen(viewModel: PlantDoctorViewModel) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("🌼 फूल गिरना", "🍅 फल न लगना", "🍃 पत्ते पीले", "🐛 कीट प्रकोप")

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "फसल की आम समस्याएं",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_from_hub_button")
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = ForestGreen,
        contentColor = Color.White
      ) {
        tabs.forEachIndexed { index, title ->
          val isSelected = selectedTab == index
          Tab(
            selected = isSelected,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFFE2EBE2),
                maxLines = 1
              )
            }
          )
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        when (selectedTab) {
          0 -> {
            item {
              ProblemDetailCard(
                title = "🌼 फूल क्यों गिर रहे हैं? (Flower Dropping)",
                tagline = "फूलों का गिरना उपज को सीधे 40-60% घटा सकता है।",
                causes = listOf(
                  "अचानक तापमान में वृद्धि (35°C से अधिक) या रात में अधिक ठंडक",
                  "सिंचाई में अनियमितता: मिट्टी बहुत ज्यादा सूखी या बहुत गीली होना",
                  "पोषक तत्वों की कमी: विशेषकर बोरोन और कैल्शियम का अभाव",
                  "रस चूसक कीट: थ्रिप्स व सफेद मक्खी द्वारा फूलों का रस चूसना",
                  "परागण (Pollination) में बाधा: हवा का न चलना या मधुमक्खियों की कमी"
                ),
                treatments = listOf(
                  "नमी का संतुलन: फूल आते समय खेत में केवल हल्की नमी रखें, कभी भी खेत में पानी भरने न दें।",
                  "शाम के समय सिंचाई: हमेशा शाम को पानी लगाएं ताकि जड़ों को ठंडक मिले।",
                  "बोरोन का स्प्रे: 20% बोरोन को 1 ग्राम प्रति लीटर पानी में मिलाकर शाम को हल्का छिड़काव करें।",
                  "नीम का तेल: कीटों से बचाव के लिए 1500 PPM नीम का तेल 2 मिली/लीटर का छिड़काव करें।"
                ),
                onStartScan = { viewModel.startProblemSpecialFlow("flower_drop") }
              )
            }
          }
          1 -> {
            item {
              ProblemDetailCard(
                title = "🍅 फल क्यों नहीं लग रहा? (Poor Fruit Setting)",
                tagline = "फूल तो खूब आते हैं लेकिन फल में नहीं बदलते या छोटे गिर जाते हैं।",
                causes = listOf(
                  "परागण न होना: परागकणों का मादा फूल तक न पहुंच पाना",
                  "तापमान का असंतुलन: अत्यधिक गर्मी में परागकण सूख जाते हैं",
                  "पोटाश और फॉस्फोरस की कमी",
                  "केवल यूरिया (नाइट्रोजन) अधिक डालना जिससे केवल पत्ते बढ़ते हैं, फल नहीं बनते"
                ),
                treatments = listOf(
                  "प्राकृतिक परागण बढ़ाएं: खेत के किनारे पीले गेंदे के फूल लगाएं ताकि मधुमक्खियां आएं।",
                  "हस्त परागण (Manual Pollination): लौकी, खीरा, करेला में सुबह 7 से 9 बजे नर फूल को मादा फूल पर हल्के से छुएं।",
                  "NPK 0:52:34 का छिड़काव: 4-5 ग्राम प्रति लीटर की दर से 10 दिन के अंतराल पर दें।",
                  "पानी का तनाव न दें।"
                ),
                onStartScan = { viewModel.startProblemSpecialFlow("fruit_issue") }
              )
            }
          }
          2 -> {
            item {
              ProblemDetailCard(
                title = "🍃 पत्ते पीले क्यों हो रहे हैं? (Yellow Leaves)",
                tagline = "पत्तियों का पीलापन जड़ों की घुटन या सूक्ष्म पोषक तत्वों की कमी दर्शाता है।",
                causes = listOf(
                  "जलभराव (Waterlogging): जड़ों में हवा न मिलने से जड़ें सड़ने लगती हैं",
                  "नाइट्रोजन की कमी: निचली पुरानी पत्तियां पहले पीली पड़ती हैं",
                  "आयरन या जिंक की कमी: ऊपरी नई पत्तियां पीली और नसें हरी रहती हैं",
                  "पीला शिरा मोज़ेक वायरस: सफेद मक्खी द्वारा फैलाया जाने वाला गंभीर रोग"
                ),
                treatments = listOf(
                  "अतिरिक्त पानी निकालें: खेत में जल निकासी की नाली तुरंत साफ करें।",
                  "हल्की निराई-गुड़ाई: पौधे के चारों ओर गुड़ाई करें ताकि जड़ों को ताजी हवा मिले।",
                  "पीले चिपचिपे ट्रैप: सफेद मक्खी को रोकने के लिए 12-15 ट्रैप प्रति एकड़ लगाएं।",
                  "जीवामृत या अच्छी सड़ी गोबर की खाद का प्रयोग करें।"
                ),
                onStartScan = { viewModel.startProblemSpecialFlow("yellow_leaves") }
              )
            }
          }
          3 -> {
            item {
              ProblemDetailCard(
                title = "🐛 कीट व इल्ली की पहचान (Pest Infestation)",
                tagline = "कीटों का समय रहते जैविक और सुरक्षित प्रबंधन जरूरी है।",
                causes = listOf(
                  "फल छेदक व तना छेदक इल्ली (Fruit & Shoot Borer)",
                  "थ्रिप्स, एफिड्स (माहो) और सफेद मक्खी",
                  "पत्ती सुरंगक (Leaf Miner) कीड़ा",
                  "मौसम में अधिक उमस और खरपतवार का जमाव"
                ),
                treatments = listOf(
                  "फेरोमोन ट्रैप लगाएं: 5 ट्रैप प्रति एकड़ लगाने से नर पतंगे फंस जाते हैं और अंडे नहीं दे पाते।",
                  "संक्रमित फल व टहनी नष्ट करें: छेद वाले फलों को तोड़कर जमीन में गहरा दबाएं।",
                  "नीम का काढ़ा: 5% नीम बीज अर्क या नीम तेल का छिड़काव करें।",
                  "रासायनिक कीटनाशक केवल विशेषज्ञ की सलाह पर और उत्पाद लेबल पढ़कर ही छिड़कें।"
                ),
                onStartScan = { viewModel.startProblemSpecialFlow("insects") }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProblemDetailCard(
  title: String,
  tagline: String,
  causes: List<String>,
  treatments: List<String>,
  onStartScan: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = title,
        fontSize = 19.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (isDark) Color.White else ForestGreen
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = tagline,
        fontSize = 14.sp,
        color = if (isDark) Color(0xFFFFB74D) else Color(0xFFB75500),
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "🔎 मुख्य कारण:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color.White else Color(0xFF111410)
      )
      Spacer(modifier = Modifier.height(6.dp))
      causes.forEach { cause ->
        Row(modifier = Modifier.padding(vertical = 3.dp)) {
          Text(
            text = "• ",
            color = if (isDark) Color(0xFF81C784) else ForestGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Text(
            text = cause,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else Color(0xFF111410),
            lineHeight = 22.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "✅ खेत में करने योग्य उपाय:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color(0xFF81C784) else ForestGreen
      )
      Spacer(modifier = Modifier.height(6.dp))
      treatments.forEachIndexed { i, treat ->
        Row(modifier = Modifier.padding(vertical = 3.dp)) {
          Text(
            text = "${i + 1}. ",
            color = if (isDark) Color(0xFF81C784) else ForestGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = treat,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else Color(0xFF111410),
            lineHeight = 22.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))
      Button(
        onClick = onStartScan,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDark) Color(0xFF2E7D32) else ForestGreen
        )
      ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "इस समस्या की फोटो स्कैन करें ▶", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
      }
    }
  }
}
