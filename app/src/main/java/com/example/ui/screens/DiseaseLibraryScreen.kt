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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

data class DiseaseItem(
  val id: String,
  val nameHindi: String,
  val cropHindi: String,
  val type: String, // "रोग (Disease)", "कीट (Pest)", "पोषक तत्व (Nutrient)"
  val symptomsHindi: String,
  val preventionHindi: String,
  val organicControlHindi: String
)

val LIBRARY_DATA = listOf(
  DiseaseItem(
    id = "1",
    nameHindi = "पत्ती मरोड़िया रोग (Leaf Curl Virus)",
    cropHindi = "मिर्च, टमाटर",
    type = "रोग (वायरस)",
    symptomsHindi = "पत्ते ऊपर की ओर मुड़कर नाव जैसे बन जाते हैं, पौधे की बढ़वार रुक जाती है।",
    preventionHindi = "सफेद मक्खी पर नियंत्रण रखें, पीला चिपचिपा ट्रैप लगाएं।",
    organicControlHindi = "नीम का तेल (1500 PPM) 3 मिली/लीटर का 5 दिन के अंतराल पर 2 बार छिड़काव करें।"
  ),
  DiseaseItem(
    id = "2",
    nameHindi = "फल व तना छेदक इल्ली (Fruit & Shoot Borer)",
    cropHindi = "बैंगन, टमाटर",
    type = "कीट (इल्ली)",
    symptomsHindi = "मुलायम टहनियां सूखकर लटक जाती हैं और फल में बारीक छेद दिखता है जिसके बाहर बुरादा रहता है।",
    preventionHindi = "फेरोमोन ट्रैप 5 प्रति एकड़ लगाएं, ग्रसित टहनी काटकर नष्ट करें।",
    organicControlHindi = "नीम बीज अर्क (NSKE 5%) या बीटी (Bacillus thuringiensis) का छिड़काव करें।"
  ),
  DiseaseItem(
    id = "3",
    nameHindi = "पीला शिरा मोज़ेक (Yellow Vein Mosaic)",
    cropHindi = "भिंडी",
    type = "रोग (वायरस)",
    symptomsHindi = "पत्तियों की नसें पहले पीली होती हैं, फिर पूरी पत्ती पीली हो जाती है।",
    preventionHindi = "रोगरोधी किस्में लगाएं, सफेद मक्खी को तुरंत रोकें।",
    organicControlHindi = "ग्रसित पौधों को तुरंत उखाड़कर जमीन में दबाएं। खट्टी छाछ और नीम तेल छिड़कें।"
  ),
  DiseaseItem(
    id = "4",
    nameHindi = "अगेती व पछेती झुलसा (Blight)",
    cropHindi = "टमाटर, आलू",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों पर भूरे-काले छल्लेदार धब्बे बनते हैं और पत्तियां झुलसी हुई दिखती हैं।",
    preventionHindi = "जड़ों के पास पानी न भरने दें, पौधों पर पानी का छिड़काव न करें।",
    organicControlHindi = "ट्राइकोडर्मा विरिडी 5 ग्राम प्रति लीटर पानी में मिलाकर छिड़कें।"
  ),
  DiseaseItem(
    id = "5",
    nameHindi = "चूर्णिल आसिता (Powdery Mildew)",
    cropHindi = "खीरा, लौकी, करेला",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों की ऊपरी सतह पर सफेद आटे जैसा पाउडर दिखाई देता है।",
    preventionHindi = "खेत में वायु संचार अच्छा रखें, खरपतवार साफ करें।",
    organicControlHindi = "दूध का घोल (10% दूध पानी में) या बेकिंग सोडा 3 ग्राम प्रति लीटर का छिड़काव करें।"
  ),
  DiseaseItem(
    id = "6",
    nameHindi = "फूल झड़ना व फल मक्खी (Flower Drop & Fruit Fly)",
    cropHindi = "लौकी, तोरी, करेला",
    type = "कीट व परागण",
    symptomsHindi = "छोटे फल सड़कर गिर जाते हैं, फलों में डंक के निशान और मुड़ाव दिखता है।",
    preventionHindi = "मिथाइल यूजेनॉल ट्रैप लगाएं, शाम को हल्की नमी बनाए रखें।",
    organicControlHindi = "हस्त परागण करें और नीम तेल का नियमित छिड़काव करें।"
  )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseLibraryScreen(viewModel: PlantDoctorViewModel) {
  var searchQuery by remember { mutableStateOf("") }
  var expandedId by remember { mutableStateOf<String?>(null) }

  val filteredList = LIBRARY_DATA.filter {
    searchQuery.isBlank() ||
      it.nameHindi.contains(searchQuery, ignoreCase = true) ||
      it.cropHindi.contains(searchQuery, ignoreCase = true) ||
      it.type.contains(searchQuery, ignoreCase = true)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "रोग व कीट पहचान पुस्तिका",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_library_button")
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
        .padding(16.dp)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("बीमारी या फसल का नाम खोजें (उदा. टमाटर, इल्ली)") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ForestGreen)
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "साफ करें")
            }
          }
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_search_disease")
      )

      Spacer(modifier = Modifier.height(14.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredList) { item ->
          val isExpanded = expandedId == item.id
          val isDark = isSystemInDarkTheme()
          Card(
            onClick = { expandedId = if (isExpanded) null else item.id },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.nameHindi,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else ForestGreen
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "फसल: ${item.cropHindi} • ${item.type}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF111410)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isDark) Color(0xFF263C26) else PaleGreenBg,
                  border = BorderStroke(1.dp, if (isDark) Color(0xFF81C784) else Color(0xFF81C784))
                ) {
                  Text(
                    text = if (isExpanded) "कम देखें ▲" else "उपचार देखें ▼",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else ForestGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "👀 लक्षण:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isDark) Color(0xFF81D4FA) else Color(0xFF01579B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.symptomsHindi,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color.White else Color(0xFF111410),
                  lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "🛡️ बचाव उपाय:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isDark) Color(0xFF81C784) else ForestGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.preventionHindi,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color.White else Color(0xFF111410),
                  lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "🌿 जैविक / देसी उपचार:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isDark) Color(0xFFFFB74D) else Color(0xFFB75500)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.organicControlHindi,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color.White else Color(0xFF0A330A),
                  lineHeight = 22.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
