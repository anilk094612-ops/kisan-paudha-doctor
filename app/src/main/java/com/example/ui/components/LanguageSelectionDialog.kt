package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GeometricBorder
import com.example.ui.theme.GeometricSurface
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg

@Composable
fun LanguageSelectionDialog(
  currentLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredLanguages = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      AppLanguage.entries
    } else {
      AppLanguage.entries.filter {
        it.nativeName.contains(searchQuery, ignoreCase = true) ||
          it.englishName.contains(searchQuery, ignoreCase = true) ||
          it.regionName.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("language_selection_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(ForestGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "भाषा",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "अपनी भाषा चुनें",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = ForestGreen
            )
            Text(
              text = "Select App Language (15+ भाषाएं)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF556052)
            )
          }
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_language_dialog")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "बंद करें",
            tint = Color.Gray
          )
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "🇮🇳 हिंदुस्तान के हर राज्य और किसान के लिए अपनी मातृभाषा में उपलब्ध",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF263238),
          modifier = Modifier.padding(bottom = 10.dp)
        )

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("भाषा या राज्य खोजें...", fontSize = 14.sp) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "खोजें",
              tint = ForestGreen
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_language_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Language List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 380.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredLanguages, key = { it.code }) { lang ->
            val isSelected = lang == currentLanguage
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) PaleGreenBg else GeometricSurface
              ),
              border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ForestGreen else GeometricBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onLanguageSelected(lang)
                  onDismiss()
                }
                .testTag("lang_item_${lang.code}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Text(
                    text = lang.symbol,
                    fontSize = 24.sp
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = lang.nativeName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isSelected) ForestGreen else Color(0xFF1E261C)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "(${lang.englishName})",
                        fontSize = 13.sp,
                        color = Color.Gray
                      )
                    }
                    Text(
                      text = lang.regionName,
                      fontSize = 12.sp,
                      color = Color(0xFF5A6658)
                    )
                  }
                }

                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "चुनी गई",
                    tint = ForestGreen,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("confirm_language_button")
      ) {
        Text("ठीक है (Done)", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  )
}
