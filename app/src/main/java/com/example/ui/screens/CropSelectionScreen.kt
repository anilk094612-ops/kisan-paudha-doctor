package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.data.model.Crop
import com.example.data.model.INITIAL_CROPS
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropSelectionScreen(viewModel: PlantDoctorViewModel) {
  val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()
  var showCustomCropDialog by remember { mutableStateOf(false) }
  var customCropName by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "अपनी फसल चुनें",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.PlantScan) },
            modifier = Modifier.testTag("back_to_scan_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "वापस जाएं",
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
    bottomBar = {
      SurfaceCardBottomBar(
        selectedCrop = selectedCrop,
        onProceed = { viewModel.onCropChosen(selectedCrop) }
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .testTag("crop_selection_grid"),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item(span = { GridItemSpan(2) }) {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F3E4)),
          border = BorderStroke(1.5.dp, Color(0xFF8BA683)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "जिस पौधे में समस्या है, उस फसल को छुएं:",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "सही फसल चुनने से AI बिल्कुल सटीक कारण और सलाह देगा।",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF142C14)
            )
          }
        }
      }

      items(INITIAL_CROPS) { crop ->
        val isSelected = crop.id == selectedCrop.id
        CropTileCard(
          crop = crop,
          isSelected = isSelected,
          onClick = { viewModel.setCrop(crop) }
        )
      }

      item(span = { GridItemSpan(2) }) {
        OutlinedButton(
          onClick = { showCustomCropDialog = true },
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(2.dp, HarvestAmber),
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .testTag("btn_custom_crop_search")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = HarvestAmber
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "मुझे अपनी फसल नहीं मिल रही",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HarvestAmber
          )
        }
      }
    }

    if (showCustomCropDialog) {
      AlertDialog(
        onDismissRequest = { showCustomCropDialog = false },
        title = {
          Text(
            text = "अपनी फसल का नाम लिखें",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ForestGreen
          )
        },
        text = {
          Column {
            Text(
              text = "उदाहरण: धनिया, शिमला मिर्च, प्याज, पालक, गाजर आदि",
              fontSize = 13.sp,
              color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = customCropName,
              onValueChange = { customCropName = it },
              placeholder = { Text("फसल का नाम दर्ज करें") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_custom_crop")
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (customCropName.isNotBlank()) {
                val newCrop = Crop(
                  id = "custom_${System.currentTimeMillis()}",
                  nameHindi = customCropName.trim(),
                  emoji = "🌿",
                  commonIssuesHindi = listOf("रोग व कीट", "पोषण समस्या")
                )
                viewModel.setCrop(newCrop)
                showCustomCropDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = LeafGreen)
          ) {
            Text("जोड़ें और चुनें")
          }
        },
        dismissButton = {
          TextButton(onClick = { showCustomCropDialog = false }) {
            Text("रद्द करें")
          }
        }
      )
    }
  }
}

@Composable
fun CropTileCard(
  crop: Crop,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
    ),
    border = BorderStroke(
      width = if (isSelected) 2.5.dp else 1.5.dp,
      color = if (isSelected) ForestGreen else Color(0xFF72846E)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
    modifier = Modifier
      .height(96.dp)
      .fillMaxWidth()
      .testTag("crop_tile_${crop.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = crop.emoji,
        fontSize = 36.sp
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = crop.nameHindi,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = if (isSelected) ForestGreen else Color(0xFF111410)
        )
        if (isSelected) {
          Text(
            text = "चयनित ✓",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
          )
        }
      }
    }
  }
}

@Composable
fun SurfaceCardBottomBar(
  selectedCrop: Crop,
  onProceed: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, Color(0xFFBCC6B8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = "चुनी गई फसल:",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF262E24)
        )
        Text(
          text = "${selectedCrop.emoji} ${selectedCrop.nameHindi}",
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          color = ForestGreen
        )
      }
      Button(
        onClick = onProceed,
        modifier = Modifier
          .height(54.dp)
          .testTag("btn_confirm_crop"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
      ) {
        Text(
          text = "आगे बढ़ें ▶",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
