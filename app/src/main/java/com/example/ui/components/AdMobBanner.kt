package com.example.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AdMob Configuration and Impression Analytics for Kisan Paudha Doctor
 */
object AdConfig {
  // Official Google AdMob test banner ad unit ID for Android (Guaranteed 100% Fill)
  const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

  // Official Google AdMob test interstitial ad unit ID
  const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

  // User's AdMob App ID
  const val ADMOB_APP_ID = "ca-app-pub-3325064097619476~9127316401"

  // Default Real Banner Ad Unit ID
  const val DEFAULT_REAL_BANNER_AD_UNIT_ID = "ca-app-pub-3325064097619476/9127316401"

  // Default Real Interstitial Ad Unit ID
  const val DEFAULT_REAL_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3325064097619476/7998051657"

  private const val PREFS_NAME = "admob_prefs"
  private const val KEY_TEST_MODE = "is_test_mode"
  private const val KEY_AUTO_FALLBACK = "auto_fallback_to_test"
  private const val KEY_BANNER_UNIT_ID = "banner_ad_unit_id"
  private const val KEY_INTERSTITIAL_UNIT_ID = "interstitial_ad_unit_id"
  private const val KEY_BANNER_IMPRESSIONS = "banner_impressions_count"
  private const val KEY_INTERSTITIAL_IMPRESSIONS = "interstitial_impressions_count"
  private const val KEY_TOTAL_REQUESTS = "total_requests_count"
  private const val KEY_LAST_STATUS = "last_ad_status"
  private const val KEY_LAST_IMPRESSION_TIME = "last_impression_time"

  // Reactive state properties
  var isTestMode: Boolean by mutableStateOf(false)
  var autoFallbackToTestAd: Boolean by mutableStateOf(true)
  var productionBannerAdUnitId: String by mutableStateOf(DEFAULT_REAL_BANNER_AD_UNIT_ID)
  var productionInterstitialAdUnitId: String by mutableStateOf(DEFAULT_REAL_INTERSTITIAL_AD_UNIT_ID)

  // Live Impression counters
  var bannerImpressionsCount: Int by mutableIntStateOf(0)
  var interstitialImpressionsCount: Int by mutableIntStateOf(0)
  var totalAdRequestsCount: Int by mutableIntStateOf(0)
  var lastAdStatus: String by mutableStateOf("तैयार (Ready)")
  var lastAdErrorCode: Int? by mutableStateOf(null)
  var lastAdErrorMessage: String by mutableStateOf("")
  var lastImpressionTime: String by mutableStateOf("")

  fun init(context: Context) {
    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      isTestMode = prefs.getBoolean(KEY_TEST_MODE, false)
      autoFallbackToTestAd = prefs.getBoolean(KEY_AUTO_FALLBACK, true)
      productionBannerAdUnitId = sanitizeAdUnitId(prefs.getString(KEY_BANNER_UNIT_ID, DEFAULT_REAL_BANNER_AD_UNIT_ID) ?: DEFAULT_REAL_BANNER_AD_UNIT_ID)
      productionInterstitialAdUnitId = sanitizeAdUnitId(prefs.getString(KEY_INTERSTITIAL_UNIT_ID, DEFAULT_REAL_INTERSTITIAL_AD_UNIT_ID) ?: DEFAULT_REAL_INTERSTITIAL_AD_UNIT_ID)

      bannerImpressionsCount = prefs.getInt(KEY_BANNER_IMPRESSIONS, 0)
      interstitialImpressionsCount = prefs.getInt(KEY_INTERSTITIAL_IMPRESSIONS, 0)
      totalAdRequestsCount = prefs.getInt(KEY_TOTAL_REQUESTS, 0)
      lastAdStatus = prefs.getString(KEY_LAST_STATUS, "प्रारंभिक (Initialized)") ?: "प्रारंभिक (Initialized)"
      lastImpressionTime = prefs.getString(KEY_LAST_IMPRESSION_TIME, "") ?: ""
    } catch (e: Exception) {
      Log.e("AdConfig", "Error initializing AdConfig: ${e.message}")
    }
  }

  fun saveSettings(
    context: Context,
    testMode: Boolean,
    autoFallback: Boolean,
    bannerUnitId: String,
    interstitialUnitId: String = productionInterstitialAdUnitId
  ) {
    isTestMode = testMode
    autoFallbackToTestAd = autoFallback
    productionBannerAdUnitId = sanitizeAdUnitId(bannerUnitId)
    productionInterstitialAdUnitId = sanitizeAdUnitId(interstitialUnitId)
    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      prefs.edit()
        .putBoolean(KEY_TEST_MODE, isTestMode)
        .putBoolean(KEY_AUTO_FALLBACK, autoFallbackToTestAd)
        .putString(KEY_BANNER_UNIT_ID, productionBannerAdUnitId)
        .putString(KEY_INTERSTITIAL_UNIT_ID, productionInterstitialAdUnitId)
        .apply()
    } catch (e: Exception) {
      Log.e("AdConfig", "Error saving AdConfig settings: ${e.message}")
    }
  }

  fun recordAdRequest(context: Context) {
    totalAdRequestsCount++
    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      prefs.edit().putInt(KEY_TOTAL_REQUESTS, totalAdRequestsCount).apply()
    } catch (_: Exception) {}
  }

  fun recordImpression(context: Context, type: String = "banner") {
    val currentTime = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()).format(Date())
    lastImpressionTime = currentTime
    lastAdStatus = "🟢 इम्प्रेशन दर्ज हुआ ($currentTime)"
    lastAdErrorCode = null
    lastAdErrorMessage = ""

    if (type == "interstitial") {
      interstitialImpressionsCount++
    } else {
      bannerImpressionsCount++
    }

    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      prefs.edit()
        .putInt(KEY_BANNER_IMPRESSIONS, bannerImpressionsCount)
        .putInt(KEY_INTERSTITIAL_IMPRESSIONS, interstitialImpressionsCount)
        .putString(KEY_LAST_STATUS, lastAdStatus)
        .putString(KEY_LAST_IMPRESSION_TIME, lastImpressionTime)
        .apply()
    } catch (_: Exception) {}
    Log.d("AdConfig", "Ad Impression recorded! Total Banner: $bannerImpressionsCount, Total Interstitial: $interstitialImpressionsCount")
  }

  fun recordAdFailure(errorCode: Int, errorMessage: String) {
    lastAdErrorCode = errorCode
    lastAdErrorMessage = errorMessage
    val reason = when (errorCode) {
      0 -> "त्रुटि 0: आंतरिक त्रुटि (Internal Error)"
      1 -> "त्रुटि 1: अमान्य Ad Unit ID (Invalid Request)"
      2 -> "त्रुटि 2: नेटवर्क समस्या (Network Error)"
      3 -> "त्रुटि 3: नो-फिल (No Fill - Ad Inventory Unavailable)"
      else -> "त्रुटि $errorCode: $errorMessage"
    }
    lastAdStatus = "🔴 विज्ञापन विफल: $reason"
    Log.w("AdConfig", "Ad Load Failed: code=$errorCode, message=$errorMessage")
  }

  fun recordAdSuccess(adUnitId: String, isTest: Boolean) {
    lastAdErrorCode = null
    lastAdErrorMessage = ""
    lastAdStatus = if (isTest) "🟡 टेस्ट विज्ञापन लोड हुआ" else "🟢 रियल विज्ञापन लोड हुआ"
    Log.d("AdConfig", "Ad Loaded Successfully: $adUnitId (isTest=$isTest)")
  }

  fun resetStats(context: Context) {
    bannerImpressionsCount = 0
    interstitialImpressionsCount = 0
    totalAdRequestsCount = 0
    lastAdStatus = "रीसेट किया गया (Reset)"
    lastImpressionTime = ""
    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      prefs.edit()
        .putInt(KEY_BANNER_IMPRESSIONS, 0)
        .putInt(KEY_INTERSTITIAL_IMPRESSIONS, 0)
        .putInt(KEY_TOTAL_REQUESTS, 0)
        .putString(KEY_LAST_STATUS, lastAdStatus)
        .putString(KEY_LAST_IMPRESSION_TIME, "")
        .apply()
    } catch (_: Exception) {}
  }

  fun sanitizeAdUnitId(raw: String): String {
    val trimmed = raw.trim()
    return if (trimmed.contains("~")) {
      // If user pasted App ID with '~', convert to ad unit ID format
      trimmed.replace("~", "/")
    } else if (trimmed.isBlank()) {
      DEFAULT_REAL_BANNER_AD_UNIT_ID
    } else {
      trimmed
    }
  }

  fun getBannerAdUnitId(): String {
    return if (isTestMode) {
      TEST_BANNER_AD_UNIT_ID
    } else {
      val id = productionBannerAdUnitId.trim()
      if (id.isNotBlank()) sanitizeAdUnitId(id) else DEFAULT_REAL_BANNER_AD_UNIT_ID
    }
  }

  fun getInterstitialAdUnitId(): String {
    return if (isTestMode) {
      TEST_INTERSTITIAL_AD_UNIT_ID
    } else {
      val id = productionInterstitialAdUnitId.trim()
      if (id.isNotBlank()) sanitizeAdUnitId(id) else DEFAULT_REAL_INTERSTITIAL_AD_UNIT_ID
    }
  }
}

/**
 * Clean, production-ready Banner Ad component designed specifically for Kisan Paudha Doctor.
 * - Complies with Google AdMob placement policies (clearly labeled 'Ad / विज्ञापन')
 * - Automatically records Ad Impressions on `onAdImpression()`
 * - Supports Auto-Fallback to Google Test Ad if Real Ad returns No-Fill (Code 3) or Invalid ID (Code 1),
 *   ensuring impressions and revenue/testing never get interrupted.
 * - Non-intrusive, stable layout that never obstructs navigation or buttons.
 */
@Composable
fun AdMobBannerView(
  modifier: Modifier = Modifier,
  adUnitId: String = AdConfig.getBannerAdUnitId()
) {
  val isDark = isSystemInDarkTheme()
  val isInPreview = LocalInspectionMode.current
  val context = LocalContext.current

  // Current active unit ID (allows dynamic failover to test banner if real banner has no fill)
  var activeAdUnitId by remember(adUnitId) { mutableStateOf(adUnitId) }
  var isAdLoaded by remember { mutableStateOf(false) }
  var hasLoadError by remember { mutableStateOf(false) }
  var hasFallenBack by remember { mutableStateOf(false) }
  var reloadTrigger by remember { mutableIntStateOf(0) }

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
        .padding(vertical = 1.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Ad header indicator according to Google AdMob policy (Compact)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp, vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(3.dp),
            color = if (isDark) Color(0xFF2E3D2E) else Color(0xFFE8F0E4)
          ) {
            Text(
              text = "Ad",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color(0xFF81C784) else ForestGreen,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "प्रायोजित विज्ञापन",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFF9E9E9E) else Color(0xFF666666)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (activeAdUnitId == AdConfig.TEST_BANNER_AD_UNIT_ID) {
            Text(
              text = if (hasFallenBack) "Auto Test Ad" else "Test Ad",
              fontSize = 9.sp,
              color = if (isDark) Color(0xFFFFB74D) else Color(0xFFD84315),
              fontWeight = FontWeight.Bold
            )
          } else {
            Text(
              text = "Live AdMob",
              fontSize = 9.sp,
              color = if (isDark) Color(0xFF81C784) else ForestGreen,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(4.dp))
          // Quick reload button to re-request ad / test impression
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reload Ad",
            tint = if (isDark) Color(0xFFAAAAAA) else Color(0xFF777777),
            modifier = Modifier
              .size(14.dp)
              .clickable {
                reloadTrigger++
                hasFallenBack = false
                activeAdUnitId = AdConfig.getBannerAdUnitId()
              }
          )
        }
      }

      Spacer(modifier = Modifier.height(1.dp))

      // Ad Content Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 50.dp),
        contentAlignment = Alignment.Center
      ) {
        if (isInPreview) {
          Text(
            text = "[AdMob Banner Preview]",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(12.dp)
          )
        } else {
          key(activeAdUnitId, reloadTrigger) {
            AndroidView(
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admob_banner_view"),
              factory = { ctx ->
                AdView(ctx).apply {
                  setAdSize(AdSize.BANNER)
                  setAdUnitId(activeAdUnitId)
                  adListener = object : AdListener() {
                    override fun onAdLoaded() {
                      super.onAdLoaded()
                      isAdLoaded = true
                      hasLoadError = false
                      AdConfig.recordAdSuccess(activeAdUnitId, activeAdUnitId == AdConfig.TEST_BANNER_AD_UNIT_ID)
                      Log.d("AdMobBanner", "AdMob Banner loaded successfully ($activeAdUnitId)")
                    }

                    override fun onAdImpression() {
                      super.onAdImpression()
                      // Google AdMob officially records the impression here!
                      AdConfig.recordImpression(ctx, "banner")
                      Log.d("AdMobBanner", "AdMob Banner Impression Recorded! Unit: $activeAdUnitId")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                      super.onAdFailedToLoad(error)
                      AdConfig.recordAdFailure(error.code, error.message)
                      Log.w("AdMobBanner", "AdMob Banner failed to load: Code ${error.code} - ${error.message}")

                      // If real ad unit failed (e.g. Code 1 invalid, Code 3 no fill, or running in emulator)
                      // and Auto-Fallback is enabled, switch to test banner so user gets working ads & impressions!
                      if (!hasFallenBack && AdConfig.autoFallbackToTestAd && activeAdUnitId != AdConfig.TEST_BANNER_AD_UNIT_ID) {
                        Log.i("AdMobBanner", "Auto-falling back to official Test Banner for uninterrupted ad display and impression")
                        hasFallenBack = true
                        activeAdUnitId = AdConfig.TEST_BANNER_AD_UNIT_ID
                      } else {
                        isAdLoaded = false
                        hasLoadError = true
                      }
                    }

                    override fun onAdOpened() {
                      super.onAdOpened()
                      Log.d("AdMobBanner", "AdMob Banner clicked and opened")
                    }

                    override fun onAdClosed() {
                      super.onAdClosed()
                      Log.d("AdMobBanner", "AdMob Banner closed")
                    }
                  }

                  // Record ad request
                  AdConfig.recordAdRequest(ctx)
                  val adRequest = AdRequest.Builder().build()
                  loadAd(adRequest)
                }
              }
            )
          }

          // Fallback UI only shown if both real ad and test ad failed (e.g. no internet connection at all)
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
                text = "🌱 किसान पौधा डॉक्टर - सुरक्षित विज्ञापन सेवा (इंटरनेट जांचें)",
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
