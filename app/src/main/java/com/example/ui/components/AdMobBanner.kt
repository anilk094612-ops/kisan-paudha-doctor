package com.example.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GeometricBorder
import com.example.ui.theme.GeometricSurface
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * AdMob Configuration for Kisan Paudha Doctor
 */
object AdConfig {
  // Official Google AdMob test banner ad unit ID for Android
  const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

  // User's provided AdMob App ID
  const val ADMOB_APP_ID = "ca-app-pub-3325064097619476~9127316401"

  // Production Ad Unit ID placeholder (can be customized once created in AdMob console)
  var productionBannerAdUnitId: String = ""

  // Use Google Test Ads as requested by the user during testing
  var isTestMode: Boolean = true

  fun getBannerAdUnitId(): String {
    return if (isTestMode || productionBannerAdUnitId.isBlank()) {
      TEST_BANNER_AD_UNIT_ID
    } else {
      productionBannerAdUnitId
    }
  }
}

/**
 * Clean, production-ready Banner Ad component designed specifically for
 * Kisan Paudha Doctor.
 * - Complies with AdMob policies (clearly labeled as 'Ad / विज्ञापन')
 * - Never obscures content, positioned cleanly above/below navigation
 * - Graceful fallback in offline/test environments
 */
@Composable
fun AdMobBannerView(
  modifier: Modifier = Modifier,
  adUnitId: String = AdConfig.getBannerAdUnitId()
) {
  val isDark = isSystemInDarkTheme()
  val isInPreview = LocalInspectionMode.current
  val context = LocalContext.current

  var isAdLoaded by remember { mutableStateOf(false) }
  var hasLoadError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("admob_banner_container"),
    color = if (isDark) Color(0xFF141C14) else GeometricSurface,
    border = BorderStroke(1.dp, if (isDark) Color(0xFF263C26) else GeometricBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Ad label indicator to comply with Google AdMob placement policy
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isDark) Color(0xFF2E3D2E) else Color(0xFFE8F0E4)
          ) {
            Text(
              text = "Ad",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color(0xFF81C784) else ForestGreen,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "प्रायोजित विज्ञापन",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFF9E9E9E) else Color(0xFF666666)
          )
        }

        if (AdConfig.isTestMode) {
          Text(
            text = "Google Test Ad",
            fontSize = 10.sp,
            color = if (isDark) Color(0xFFFFB74D) else Color(0xFFD84315),
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      // Ad Content Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 50.dp),
        contentAlignment = Alignment.Center
      ) {
        if (isInPreview) {
          // Preview placeholder for IDE / testing
          Text(
            text = "[AdMob Banner Ad Preview]",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(12.dp)
          )
        } else {
          AndroidView(
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("admob_banner_view"),
            factory = { ctx ->
              AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)
                adListener = object : AdListener() {
                  override fun onAdLoaded() {
                    super.onAdLoaded()
                    isAdLoaded = true
                    hasLoadError = false
                    Log.d("AdMobBanner", "AdMob Banner loaded successfully")
                  }

                  override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    isAdLoaded = false
                    hasLoadError = true
                    errorMessage = error.message
                    Log.w("AdMobBanner", "AdMob Banner failed to load: ${error.code} - ${error.message}")
                  }
                }
                val adRequest = AdRequest.Builder().build()
                loadAd(adRequest)
              }
            }
          )

          // If ad failed to load (e.g. no network or emulator without play services),
          // show a polite fallback that keeps layout stable without crashing
          if (hasLoadError && !isAdLoaded) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = if (isDark) Color(0xFF81C784) else ForestGreen,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🌱 किसान पौधा डॉक्टर - परीक्षण विज्ञापन मोड सक्रिय",
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF333333),
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
}
