package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Crop(
  val id: String,
  val nameHindi: String,
  val emoji: String,
  val scientificName: String = "",
  val commonIssuesHindi: List<String> = emptyList()
)

val INITIAL_CROPS = listOf(
  Crop("wheat", "गेहूं", "🌾", "Triticum aestivum", listOf("पीला रतुआ (Yellow Rust)", "करनाल बंट", "माहू (एफिड्स)", "दीमक", "अनावृत कंडुआ")),
  Crop("paddy", "धान", "🌾", "Oryza sativa", listOf("शीथ ब्लाइट (झुलसा)", "तना छेदक (Stem Borer)", "भूरा फुदका (BPH)", "खैरा रोग (जिंक की कमी)", "ब्लास्ट रोग")),
  Crop("sugarcane", "गन्ना", "🎋", "Saccharum officinarum", listOf("लाल सड़न (Red Rot)", "कंसुआ / तना छेदक", "सफेद मक्खी / पायरीला", "चोटी छेदक (Top Borer)", "उकठा (Wilt)", "पोक्का बोइंग (Pokkah Boeng)")),
  Crop("mustard", "सरसों", "🌼", "Brassica juncea", listOf("सफेद रोली / रतुआ (White Rust)", "माहू / चेपा (Mustard Aphids)", "अल्टरनेरिया पत्ती धब्बा (Alternaria Blight)", "डाउनी मिल्ड्यू / तुलासिता", "तना गलन (Sclerotinia Stem Rot)")),
  Crop("tomato", "टमाटर", "🍅", "Solanum lycopersicum", listOf("फल छेदक", "पत्ती मरोड़ (लीफ़ कर्ल)", "झुलसा (ब्लाइट)", "फूल झड़ना")),
  Crop("chilli", "मिर्च", "🌶️", "Capsicum annuum", listOf("फूल झड़ना", "पत्ती मुड़ना (मरोड़िया)", "थ्रिप्स व माइट्स", "उकठा रोग")),
  Crop("brinjal", "बैंगन", "🍆", "Solanum melongena", listOf("तना व फल छेदक", "पत्तियां पीली होना", "बैक्टीरियल विल्ट", "फूट ड्रॉप")),
  Crop("cucumber", "खीरा", "🥒", "Cucumis sativus", listOf("पीले पत्ते (डाउनी मिल्ड्यू)", "फल टेढ़े होना", "चूर्णिल आसिता", "फूल गिरना")),
  Crop("okra", "भिंडी", "🫛", "Abelmoschus esculentus", listOf("पीला शिरा मोज़ेक (YVMV)", "फल छेदक इल्ली", "पत्तियों पर सफेद पाउडर")),
  Crop("bottle_gourd", "लौकी", "🎃", "Lagenaria siceraria", listOf("फल सड़ना", "मादा फूल न खिलना", "पत्तियों पर पीले धब्बे", "कीट")),
  Crop("bitter_gourd", "करेला", "🥒", "Momordica charantia", listOf("फल मक्खी डंक", "पत्ते पीले पड़ना", "विल्ट / सूखना")),
  Crop("cabbage", "गोभी", "🥦", "Brassica oleracea", listOf("हीरक पतंगा (DBM)", "काला सड़न रोग", "पत्ते कतरने वाली सुंडी")),
  Crop("potato", "आलू", "🥔", "Solanum tuberosum", listOf("पछेती झुलसा", "अगेती झुलसा", "पत्ते मुड़ना", "कंद सड़न")),
  Crop("other", "अन्य फसल", "🌿", "", listOf("कीट प्रकोप", "पोषक तत्व कमी", "रोग"))
)

data class SamplePhoto(
  val titleHindi: String,
  val cropName: String,
  val descriptionHindi: String,
  val drawableRes: Int? = null,
  val category: String = "general"
)

data class AiQuestion(
  val id: String,
  val questionHindi: String,
  val optionsHindi: List<String>
)

@Entity(tableName = "scan_results")
data class ScanResult(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropName: String,
  val problemHindi: String,
  val confidenceHindi: String, // "उच्च संभावना", "मध्यम संभावना", "कम संभावना"
  val possibleCausesRaw: String, // newline-separated
  val visibleSymptomsRaw: String, // newline-separated
  val recommendedActionsRaw: String, // newline-separated
  val thingsToAvoidRaw: String, // newline-separated
  val whenToRecheckHindi: String,
  val expertRequired: Boolean = false,
  val expertReasonHindi: String = "",
  val isDemoMode: Boolean = true,
  val imageUri: String? = null,
  val timestamp: Long = System.currentTimeMillis()
) {
  val possibleCauses: List<String> get() = possibleCausesRaw.split("\n").filter { it.isNotBlank() }
  val visibleSymptoms: List<String> get() = visibleSymptomsRaw.split("\n").filter { it.isNotBlank() }
  val recommendedActions: List<String> get() = recommendedActionsRaw.split("\n").filter { it.isNotBlank() }
  val thingsToAvoid: List<String> get() = thingsToAvoidRaw.split("\n").filter { it.isNotBlank() }
}

@Entity(tableName = "farmer_diary")
data class FarmerDiaryEntry(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropName: String,
  val actionTypeHindi: String, // "सिंचाई की", "खाद दी", "दवा/जैविक छिड़काव", "बीमारी देखी", "फूल आए", "फल बने"
  val notesHindi: String,
  val dateDisplay: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "my_crops")
data class MyCrop(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropNameHindi: String,
  val cropEmoji: String,
  val fieldOrPotName: String,
  val sowingDateString: String,
  val daysSinceSowing: Int,
  val notesHindi: String = "",
  val photoUri: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "expert_inquiries")
data class ExpertInquiry(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val farmerName: String,
  val cropName: String,
  val problemDescriptionHindi: String,
  val photoUri: String? = null,
  val hasVoiceNote: Boolean = false,
  val statusHindi: String = "जांच जारी (कृषि विशेषज्ञ को भेजा गया)",
  val expertAnswerHindi: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "feedback_records")
data class FeedbackRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val scanId: Long,
  val isHelpful: Boolean,
  val incorrectReasonHindi: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

data class WeatherInfo(
  val locationName: String = "उत्तर/मध्य भारत कृषि क्षेत्र",
  val temperature: String = "31°C",
  val humidity: String = "64%",
  val rainProbability: String = "20%",
  val conditionHindi: String = "धूप और आंशिक बादल",
  val conditionEmoji: String = "⛅",
  val farmerAlertHindi: String = "आज तेज बारिश की संभावना कम है। शाम को हल्की सिंचाई कर सकते हैं।"
)
