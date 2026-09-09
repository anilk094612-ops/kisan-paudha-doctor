package com.example.data.service

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiPlantService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }

  suspend fun diagnosePlant(
    cropName: String,
    bitmap: Bitmap?,
    symptomLocation: String,
    plantAge: String,
    hasInsects: String,
    flowerDropping: String,
    leavesYellowing: String,
    freeFormProblem: String = ""
  ): ScanResult = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
      ""
    }

    val isKeyValid = apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY")

    if (!isKeyValid) {
      Log.d("GeminiPlantService", "No valid API key found. Using agronomy knowledge base demo mode.")
      return@withContext AgronomyKnowledgeBase.diagnose(
        cropName = cropName,
        symptomLocation = symptomLocation,
        plantAge = plantAge,
        hasInsects = hasInsects,
        flowerDropping = flowerDropping,
        leavesYellowing = leavesYellowing,
        freeFormText = freeFormProblem
      )
    }

    try {
      val prompt = """
        आप एक भारतीय कृषि विशेषज्ञ एवं पौधा डॉक्टर AI हैं।
        छोटे और ग्रामीण किसानों के लिए अत्यंत सरल हिंदी में पौधे की संभावित समस्या का विश्लेषण करें।
        
        फसल: $cropName
        समस्या का स्थान: $symptomLocation
        पौधे की उम्र: $plantAge
        क्या कीड़े दिख रहे हैं: $hasInsects
        क्या फूल गिर रहे हैं: $flowerDropping
        क्या पत्ते पीले हैं: $leavesYellowing
        किसान द्वारा बताई गई समस्या: $freeFormProblem
        
        नियम:
        1. कभी भी 100% सही होने का दावा न करें। हमेशा 'संभावित' शब्द का उपयोग करें।
        2. भाषा अत्यंत सरल ग्रामीण हिंदी हो। कठिन अंग्रेजी या लैटिन वैज्ञानिक शब्दों से बचें।
        3. खतरनाक कीटनाशकों का मनमाना डोज न दें। हमेशा उत्पाद लेबल व स्थानीय कृषि विशेषज्ञ की सलाह लेने को कहें।
        4. जैविक और सरल देसी प्राथमिक उपचारों को प्राथमिकता दें।
        
        उत्तर केवल मान्य JSON प्रारूप में दें:
        {
          "possible_problem": "संभावित समस्या का सरल नाम",
          "confidence": "उच्च संभावना या मध्यम संभावना या कम संभावना",
          "possible_causes": ["कारण 1", "कारण 2", "कारण 3"],
          "visible_symptoms": ["लक्षण 1", "लक्षण 2"],
          "recommended_actions": ["कार्रवाई 1", "कार्रवाई 2", "कार्रवाई 3"],
          "things_to_avoid": ["सावधानी 1", "सावधानी 2"],
          "when_to_recheck": "कब दोबारा जांच करें",
          "expert_required": true/false,
          "expert_reason": "यदि आवश्यक हो तो कारण"
        }
      """.trimIndent()

      val partsArray = JSONArray()
      partsArray.put(JSONObject().put("text", prompt))

      if (bitmap != null) {
        val base64Data = bitmapToBase64(bitmap)
        val inlineDataObj = JSONObject().apply {
          put("mimeType", "image/jpeg")
          put("data", base64Data)
        }
        partsArray.put(JSONObject().put("inlineData", inlineDataObj))
      }

      val contentsArray = JSONArray().put(
        JSONObject().put("parts", partsArray)
      )

      val generationConfig = JSONObject().apply {
        put("responseMimeType", "application/json")
        put("temperature", 0.4)
      }

      val requestJson = JSONObject().apply {
        put("contents", contentsArray)
        put("generationConfig", generationConfig)
      }

      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
      val httpRequest = Request.Builder()
        .url(url)
        .post(body)
        .build()

      val response = client.newCall(httpRequest).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful || responseString.isBlank()) {
        Log.e("GeminiPlantService", "API call failed with code ${response.code}: $responseString")
        return@withContext AgronomyKnowledgeBase.diagnose(
          cropName = cropName,
          symptomLocation = symptomLocation,
          plantAge = plantAge,
          hasInsects = hasInsects,
          flowerDropping = flowerDropping,
          leavesYellowing = leavesYellowing,
          freeFormText = freeFormProblem
        )
      }

      val rootJson = JSONObject(responseString)
      val candidates = rootJson.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val contentObj = firstCandidate?.optJSONObject("content")
      val parts = contentObj?.optJSONArray("parts")
      val textResponse = parts?.optJSONObject(0)?.optString("text") ?: ""

      val parsed = JSONObject(textResponse)
      val problem = parsed.optString("possible_problem", "संभावित समस्या")
      val confidence = parsed.optString("confidence", "मध्यम संभावना")

      val causesList = mutableListOf<String>()
      val causesArr = parsed.optJSONArray("possible_causes")
      if (causesArr != null) {
        for (i in 0 until causesArr.length()) {
          causesList.add("• " + causesArr.getString(i))
        }
      }

      val symptomsList = mutableListOf<String>()
      val symptomsArr = parsed.optJSONArray("visible_symptoms")
      if (symptomsArr != null) {
        for (i in 0 until symptomsArr.length()) {
          symptomsList.add("• " + symptomsArr.getString(i))
        }
      }

      val actionsList = mutableListOf<String>()
      val actionsArr = parsed.optJSONArray("recommended_actions")
      if (actionsArr != null) {
        for (i in 0 until actionsArr.length()) {
          actionsList.add("${i + 1}. " + actionsArr.getString(i))
        }
      }

      val avoidList = mutableListOf<String>()
      val avoidArr = parsed.optJSONArray("things_to_avoid")
      if (avoidArr != null) {
        for (i in 0 until avoidArr.length()) {
          avoidList.add("• " + avoidArr.getString(i))
        }
      }

      val recheck = parsed.optString("when_to_recheck", "4 से 5 दिन बाद पौधे की नई पत्तियों को देखें")
      val expertReq = parsed.optBoolean("expert_required", false)
      val expertReason = parsed.optString("expert_reason", "")

      ScanResult(
        cropName = cropName,
        problemHindi = problem,
        confidenceHindi = confidence,
        possibleCausesRaw = causesList.joinToString("\n"),
        visibleSymptomsRaw = symptomsList.joinToString("\n"),
        recommendedActionsRaw = actionsList.joinToString("\n"),
        thingsToAvoidRaw = avoidList.joinToString("\n"),
        whenToRecheckHindi = recheck,
        expertRequired = expertReq,
        expertReasonHindi = expertReason,
        isDemoMode = false
      )
    } catch (e: Exception) {
      Log.e("GeminiPlantService", "Exception during diagnosis: ${e.message}", e)
      AgronomyKnowledgeBase.diagnose(
        cropName = cropName,
        symptomLocation = symptomLocation,
        plantAge = plantAge,
        hasInsects = hasInsects,
        flowerDropping = flowerDropping,
        leavesYellowing = leavesYellowing,
        freeFormText = freeFormProblem
      )
    }
  }
}
