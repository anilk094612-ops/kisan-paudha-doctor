package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdMobBannerView
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

data class MandiPriceInfo(
  val id: String,
  val cropName: String,
  val emoji: String,
  val variety: String,
  val mandiName: String,
  val state: String,
  val minPrice: Int,
  val maxPrice: Int,
  val modalPrice: Int,
  val trendDifference: Int, // positive = up, negative = down, 0 = flat
  val arrivalTons: String,
  val dateText: String = "आज का भाव"
)

private val SAMPLE_MANDI_PRICES = listOf(
  MandiPriceInfo(
    id = "1",
    cropName = "गेहूं",
    emoji = "🌾",
    variety = "शरबती / लोकवान",
    mandiName = "इंदौर कृषि उपज मंडी",
    state = "मध्य प्रदेश",
    minPrice = 2450,
    maxPrice = 2850,
    modalPrice = 2650,
    trendDifference = 50,
    arrivalTons = "140 टन"
  ),
  MandiPriceInfo(
    id = "2",
    cropName = "सरसों",
    emoji = "🌱",
    variety = "बारीक / 42% तेल",
    mandiName = "भरतपुर मंडी",
    state = "राजस्थान",
    minPrice = 5200,
    maxPrice = 5750,
    modalPrice = 5520,
    trendDifference = 80,
    arrivalTons = "90 टन"
  ),
  MandiPriceInfo(
    id = "3",
    cropName = "चना",
    emoji = "🫘",
    variety = "देसी / काबुली",
    mandiName = "कोटा मंडी",
    state = "राजस्थान",
    minPrice = 5800,
    maxPrice = 6400,
    modalPrice = 6150,
    trendDifference = 0,
    arrivalTons = "75 टन"
  ),
  MandiPriceInfo(
    id = "4",
    cropName = "टमाटर",
    emoji = "🍅",
    variety = "हाइब्रिड लाल",
    mandiName = "आज़ादपुर मंडी",
    state = "दिल्ली / उत्तर भारत",
    minPrice = 1200,
    maxPrice = 2200,
    modalPrice = 1800,
    trendDifference = -100,
    arrivalTons = "210 टन"
  ),
  MandiPriceInfo(
    id = "5",
    cropName = "धान (चावल)",
    emoji = "🌾",
    variety = "बासमती 1509",
    mandiName = "करनाल मंडी",
    state = "हरियाणा",
    minPrice = 3200,
    maxPrice = 3850,
    modalPrice = 3600,
    trendDifference = 60,
    arrivalTons = "320 टन"
  ),
  MandiPriceInfo(
    id = "6",
    cropName = "सोयाबीन",
    emoji = "🌱",
    variety = "पीला सोयाबीन",
    mandiName = "उज्जैन मंडी",
    state = "मध्य प्रदेश",
    minPrice = 4200,
    maxPrice = 4750,
    modalPrice = 4500,
    trendDifference = -40,
    arrivalTons = "180 टन"
  ),
  MandiPriceInfo(
    id = "7",
    cropName = "प्याज",
    emoji = "🧅",
    variety = "लाल प्याज (नासिक)",
    mandiName = "लासलगांव मंडी",
    state = "महाराष्ट्र",
    minPrice = 1500,
    maxPrice = 2600,
    modalPrice = 2100,
    trendDifference = 120,
    arrivalTons = "450 टन"
  ),
  MandiPriceInfo(
    id = "8",
    cropName = "आलू",
    emoji = "🥔",
    variety = "पुखराज / चिप्सोना",
    mandiName = "आगरा मंडी",
    state = "उत्तर प्रदेश",
    minPrice = 1100,
    maxPrice = 1650,
    modalPrice = 1400,
    trendDifference = 0,
    arrivalTons = "380 टन"
  ),
  MandiPriceInfo(
    id = "9",
    cropName = "लहसुन",
    emoji = "🧄",
    variety = "उटी / देसी",
    mandiName = "मंदसौर मंडी",
    state = "मध्य प्रदेश",
    minPrice = 11000,
    maxPrice = 19500,
    modalPrice = 15500,
    trendDifference = 300,
    arrivalTons = "60 टन"
  ),
  MandiPriceInfo(
    id = "10",
    cropName = "मक्का",
    emoji = "🌽",
    variety = "हाइब्रिड पीली",
    mandiName = "गुलाबबाग मंडी",
    state = "बिहार",
    minPrice = 2150,
    maxPrice = 2450,
    modalPrice = 2300,
    trendDifference = 30,
    arrivalTons = "160 टन"
  ),
  MandiPriceInfo(
    id = "11",
    cropName = "कपास (नरमा)",
    emoji = "☁️",
    variety = "बीटी कॉटन",
    mandiName = "अबोहर मंडी",
    state = "पंजाब",
    minPrice = 6800,
    maxPrice = 7650,
    modalPrice = 7300,
    trendDifference = 90,
    arrivalTons = "110 टन"
  ),
  MandiPriceInfo(
    id = "12",
    cropName = "गन्ना",
    emoji = "🎋",
    variety = "अगेती Co 0238 / सामान्य",
    mandiName = "मुजफ्फरनगर गुड़ मंडी व मिल",
    state = "उत्तर प्रदेश",
    minPrice = 370,
    maxPrice = 425,
    modalPrice = 395,
    trendDifference = 15,
    arrivalTons = "540 टन"
  )
)

private val CROP_FILTERS = listOf("सभी फसलें", "गेहूं", "धान", "गन्ना", "सरसों", "चना", "टमाटर", "सोयाबीन", "प्याज", "आलू", "लहसुन", "मक्का", "कपास")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiBhavScreen(viewModel: PlantDoctorViewModel) {
  val isDark = isSystemInDarkTheme()
  var searchQuery by remember { mutableStateOf("") }
  var selectedCropFilter by remember { mutableStateOf("सभी फसलें") }

  BackHandler {
    viewModel.navigateTo(Screen.Home)
  }

  val filteredList = remember(searchQuery, selectedCropFilter) {
    SAMPLE_MANDI_PRICES.filter { item ->
      val matchesCrop = if (selectedCropFilter == "सभी फसलें") true else item.cropName.contains(selectedCropFilter) || selectedCropFilter.contains(item.cropName)
      val matchesSearch = searchQuery.isBlank() ||
        item.cropName.contains(searchQuery, ignoreCase = true) ||
        item.mandiName.contains(searchQuery, ignoreCase = true) ||
        item.state.contains(searchQuery, ignoreCase = true) ||
        item.variety.contains(searchQuery, ignoreCase = true)
      matchesCrop && matchesSearch
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "🌾 मंडी भाव (बाजार दरें)",
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "नजदीकी मंडियों की दैनिक फसल कीमतें",
              fontSize = 12.sp,
              color = Color(0xFFD7E8CD)
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("mandi_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "वापस जाएं",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ForestGreen)
      )
    },
    bottomBar = {
      AdMobBannerView()
    },
    containerColor = if (isDark) Color(0xFF121811) else PaleGreenBg
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Info & Guidance Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E281C) else Color(0xFFEAF5E5)
          ),
          border = BorderStroke(1.5.dp, ForestGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ForestGreen),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "📊", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "आज का ताजा मंडी भाव • ₹/क्विंटल",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDark) Color.White else ForestGreen
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "दरें प्रमुख APMC मंडियों के मॉडल भाव पर आधारित हैं। बेचने से पहले स्थानीय मंडी में पुष्टि करें।",
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFB0BEC5) else Color(0xFF2E382B),
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      // 2. Search Box
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("mandi_search_input"),
          placeholder = {
            Text(
              text = "फसल या मंडी खोजें (जैसे: गेहूं, इंदौर, सरसों)...",
              fontSize = 14.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "खोजें",
              tint = ForestGreen
            )
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = if (isDark) Color(0xFF374733) else Color(0xFFBCC6B8),
            focusedContainerColor = if (isDark) Color(0xFF1A2218) else Color.White,
            unfocusedContainerColor = if (isDark) Color(0xFF1A2218) else Color.White
          )
        )
      }

      // 3. Crop Filter Chips
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          items(CROP_FILTERS) { filterName ->
            val isSelected = filterName == selectedCropFilter
            FilterChip(
              selected = isSelected,
              onClick = { selectedCropFilter = filterName },
              label = {
                Text(
                  text = filterName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ForestGreen,
                selectedLabelColor = Color.White,
                containerColor = if (isDark) Color(0xFF242E21) else Color.White,
                labelColor = if (isDark) Color.White else Color(0xFF1E281C)
              ),
              border = BorderStroke(
                1.dp,
                if (isSelected) ForestGreen else if (isDark) Color(0xFF3D4F39) else Color(0xFFCDD6CA)
              ),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // 4. Mandi Rates List
      if (filteredList.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E281C) else Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "🔍", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "कोई मंडी भाव नहीं मिला",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isDark) Color.White else Color.DarkGray
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "कृपया दूसरा फसल नाम या फिल्टर चुनकर प्रयास करें।",
                fontSize = 13.sp,
                color = Color.Gray
              )
            }
          }
        }
      } else {
        items(filteredList) { item ->
          MandiPriceCard(item = item, isDark = isDark)
        }
      }

      // 5. Farmer Advisory Tips Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF221E14) else Color(0xFFFFF9E6)
          ),
          border = BorderStroke(1.5.dp, HarvestAmber),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = HarvestAmber,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "💡 किसान भाई के लिए मंडी सुझाव",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isDark) Color(0xFFFFD54F) else Color(0xFF8A5B00)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = if (isDark) Color(0xFF4A3E20) else Color(0xFFFFECB3))
            Spacer(modifier = Modifier.height(10.dp))

            MandiTipRow("1. नमी जांच:", "उपज में 12% से कम नमी होने पर फसल का सबसे अच्छा भाव मिलता है।")
            MandiTipRow("2. ग्रेडिंग:", "दाने की छंटाई और सफाई करने पर औसतन ₹100-200 प्रति क्विंटल अधिक दाम मिलता है।")
            MandiTipRow("3. e-NAM पोर्टल:", "राष्ट्रीय कृषि बाजार (e-NAM) के माध्यम से देश भर के व्यापारियों को उपज ऑनलाइन बेचें।")
            MandiTipRow("4. किसान कॉल सेंटर:", "📞 1800-180-1551 पर कॉल कर किसी भी फसल की न्यूनतम समर्थन मूल्य (MSP) दरें निःशुल्क जानें।")
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun MandiPriceCard(item: MandiPriceInfo, isDark: Boolean) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF1B2319) else Color.White
    ),
    border = BorderStroke(1.5.dp, if (isDark) Color(0xFF2E3D2B) else Color(0xFFD3DEC8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top Row: Crop & Mandi Name
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(if (isDark) Color(0xFF253322) else PaleGreenBg),
            contentAlignment = Alignment.Center
          ) {
            Text(text = item.emoji, fontSize = 22.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = item.cropName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else Color(0xFF111410)
            )
            Text(
              text = item.variety,
              fontSize = 12.sp,
              color = if (isDark) Color(0xFFB0BEC5) else Color(0xFF556052)
            )
          }
        }

        // Trend Badge
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = when {
            item.trendDifference > 0 -> if (isDark) Color(0xFF142F16) else Color(0xFFE8F5E9)
            item.trendDifference < 0 -> if (isDark) Color(0xFF331616) else Color(0xFFFFEBEE)
            else -> if (isDark) Color(0xFF222822) else Color(0xFFF5F5F5)
          }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = when {
                item.trendDifference > 0 -> Icons.AutoMirrored.Filled.TrendingUp
                item.trendDifference < 0 -> Icons.AutoMirrored.Filled.TrendingDown
                else -> Icons.AutoMirrored.Filled.TrendingFlat
              },
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = when {
                item.trendDifference > 0 -> LeafGreen
                item.trendDifference < 0 -> WarningRed
                else -> Color.Gray
              }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = when {
                item.trendDifference > 0 -> "+₹${item.trendDifference}"
                item.trendDifference < 0 -> "-₹${-item.trendDifference}"
                else -> "स्थिर"
              },
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = when {
                item.trendDifference > 0 -> LeafGreen
                item.trendDifference < 0 -> WarningRed
                else -> Color.Gray
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Mandi and Location
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Place,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = ForestGreen
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${item.mandiName} (${item.state})",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDark) Color(0xFF81C784) else ForestGreen
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = if (isDark) Color(0xFF2C3829) else Color(0xFFE5EDE0))
      Spacer(modifier = Modifier.height(12.dp))

      // Rate Grid: Min, Modal (Primary), Max
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(horizontalAlignment = Alignment.Start) {
          Text(text = "न्यूनतम भाव", fontSize = 11.sp, color = Color.Gray)
          Text(
            text = "₹${item.minPrice}",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else Color(0xFF2E382B)
          )
        }

        // Primary Modal Rate Highlight Box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isDark) Color(0xFF18331A) else Color(0xFFE8F5E9),
          border = BorderStroke(1.dp, ForestGreen)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "औसत / मॉडल भाव",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color(0xFF81C784) else ForestGreen
            )
            Text(
              text = "₹${item.modalPrice} /क्विं.",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else ForestGreen
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(text = "अधिकतम भाव", fontSize = 11.sp, color = Color.Gray)
          Text(
            text = "₹${item.maxPrice}",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else Color(0xFF2E382B)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Row: Arrival Tons & Update Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "आवक: ${item.arrivalTons}",
          fontSize = 11.sp,
          color = if (isDark) Color(0xFF9E9E9E) else Color(0xFF616161)
        )
        Text(
          text = "• ${item.dateText}",
          fontSize = 11.sp,
          color = if (isDark) Color(0xFF9E9E9E) else Color(0xFF616161)
        )
      }
    }
  }
}

@Composable
private fun MandiTipRow(title: String, desc: String) {
  Row(
    modifier = Modifier.padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = "$title ",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = ForestGreen
    )
    Text(
      text = desc,
      fontSize = 13.sp,
      color = Color.DarkGray,
      lineHeight = 18.sp
    )
  }
}
