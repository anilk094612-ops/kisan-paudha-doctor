package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun VoiceAssistantDialog(
  viewModel: PlantDoctorViewModel,
  onDismiss: () -> Unit
) {
  var spokenText by remember { mutableStateOf("") }

  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
      val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      if (!matches.isNullOrEmpty()) {
        spokenText = matches[0]
      }
    }
  }

  val samplePrompts = listOf(
    "मेरी मिर्च में फूल लगकर गिर रहे हैं",
    "टमाटर के पत्ते पीले और मुड़ रहे हैं",
    "बैंगन के फल में छेद और कीड़े हैं",
    "खीरे में फूल तो हैं पर फल नहीं बन रहे"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(HarvestAmber, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "बोलकर समस्या बताएं",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ForestGreen
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "बंद करें")
        }
      }
    },
    text = {
      Column {
        Text(
          text = "माइक बटन दबाएं और अपनी भाषा (हिंदी) में पौधे की परेशानी बोलें:",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF111410)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
              putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
              putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
              putExtra(RecognizerIntent.EXTRA_PROMPT, "पौधे की समस्या बोलें...")
            }
            try {
              speechLauncher.launch(intent)
            } catch (e: Exception) {
              // Ignore if speech not available on device
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = HarvestAmber),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("btn_trigger_speech")
        ) {
          Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text("माइक चालू करें और बोलें 🎙️", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = spokenText,
          onValueChange = { spokenText = it },
          placeholder = { Text("या यहाँ लिखकर बताएं...", fontWeight = FontWeight.Medium, color = Color(0xFF555555)) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_speech_text"),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "या इनमें से किसी एक को छुएं:",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF111410)
        )
        Spacer(modifier = Modifier.height(6.dp))

        samplePrompts.forEach { prompt ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = PaleGreenBg,
            border = BorderStroke(1.5.dp, Color(0xFF81C784)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable { spokenText = prompt }
          ) {
            Text(
              text = "“$prompt”",
              fontSize = 14.sp,
              color = ForestGreen,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (spokenText.isNotBlank()) {
            onDismiss()
            viewModel.handleVoiceQuery(spokenText)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .height(48.dp)
          .testTag("btn_submit_voice")
      ) {
        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(6.dp))
        Text("जांच करें ▶", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
      }
    },
    dismissButton = {}
  )
}
