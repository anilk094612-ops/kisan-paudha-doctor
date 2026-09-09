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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.theme.SkyBlue
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

data class AgriDistrict(val name: String, val lat: Double, val lon: Double)

val DISTRICTS = listOf(
  AgriDistrict("लखनऊ / अवध कृषि क्षेत्र", 26.8467, 80.9462),
  AgriDistrict("पटना / बिहार मैदान", 25.5941, 85.1376),
  AgriDistrict("भोपाल / मालवा क्षेत्र", 23.2599, 77.4126),
  AgriDistrict("जयपुर / पूर्वी राजस्थान", 26.9124, 75.7873),
  AgriDistrict("चंडीगढ़ / पंजाब-हरियाणा", 30.7333, 76.7794),
  AgriDistrict("वाराणसी / पूर्वांचल", 25.3176, 82.9739),
  AgriDistrict("रायपुर / छत्तीसगढ़ धान क्षेत्र", 21.2514, 81.6296)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: PlantDoctorViewModel) {
  val weather by viewModel.weatherInfo.collectAsStateWithLifecycle()
  var expanded by remember { mutableStateOf(false) }
  var selectedDistrict by remember { mutableStateOf(DISTRICTS[0]) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "कृषि मौसम जानकारी",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_weather_button")
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
            onClick = { viewModel.loadWeather(selectedDistrict.lat, selectedDistrict.lon, selectedDistrict.name) },
            modifier = Modifier.testTag("refresh_weather_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "रिफ्रेश करें",
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
      // Region Selector
      item {
        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded }
        ) {
          OutlinedTextField(
            value = selectedDistrict.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("कृषि क्षेत्र / जिला चुनें") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor(),
            shape = RoundedCornerShape(14.dp)
          )
          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            DISTRICTS.forEach { dist ->
              DropdownMenuItem(
                text = { Text(dist.name) },
                onClick = {
                  selectedDistrict = dist
                  expanded = false
                  viewModel.loadWeather(dist.lat, dist.lon, dist.name)
                }
              )
            }
          }
        }
      }

      // Main Weather Hero
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = weather.conditionEmoji,
              fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = weather.temperature,
              fontSize = 44.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF01579B)
            )
            Text(
              text = weather.conditionHindi,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF01579B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = weather.locationName,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF111410)
            )
          }
        }
      }

      // Stats Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          WeatherStatCard(
            title = "हवा में नमी",
            value = weather.humidity,
            emoji = "💧",
            modifier = Modifier.weight(1f)
          )
          WeatherStatCard(
            title = "बारिश की संभावना",
            value = weather.rainProbability,
            emoji = "🌧️",
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Farmer Agricultural Alert
      item {
        val isDark = isSystemInDarkTheme()
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF142614) else PaleGreenBg
          ),
          border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFF81C784)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "📢", fontSize = 22.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "आज का किसान मौसम परामर्श:",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFF81C784) else ForestGreen
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = weather.farmerAlertHindi,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else Color(0xFF0A330A),
              lineHeight = 24.sp
            )
          }
        }
      }

      // Practical Weather Guidelines
      item {
        val isDark = isSystemInDarkTheme()
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "🌾 मौसम अनुसार जरूरी सावधानियां:",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color(0xFF81C784) else ForestGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "• यदि बारिश की संभावना 50% से अधिक हो, तो किसी भी प्रकार की दवा का छिड़काव न करें।\n• तेज धूप में दोपहर के समय सिंचाई से बचें, इससे पौधे की जड़ें झुलस सकती हैं।\n• नमी 80% से अधिक होने पर फफूंद जनित रोगों (झुलसा/सड़न) की जांच नियमित करें।",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isDark) Color.White else Color(0xFF111410),
              lineHeight = 24.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun WeatherStatCard(
  title: String,
  value: String,
  emoji: String,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.height(108.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = emoji, fontSize = 26.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (isDark) Color.White else ForestGreen
      )
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF262F23)
      )
    }
  }
}
