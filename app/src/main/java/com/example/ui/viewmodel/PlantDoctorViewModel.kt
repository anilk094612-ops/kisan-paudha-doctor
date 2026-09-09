package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PlantDoctorDatabase
import com.example.data.model.Crop
import com.example.data.model.ExpertInquiry
import com.example.data.model.FarmerDiaryEntry
import com.example.data.model.FeedbackRecord
import com.example.data.model.INITIAL_CROPS
import com.example.data.model.MyCrop
import com.example.data.model.ScanResult
import com.example.data.model.WeatherInfo
import com.example.data.repository.PlantDoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
  object Home : Screen()
  object PlantScan : Screen()
  object CropSelection : Screen()
  object AiQuestions : Screen()
  object DiagnosisResult : Screen()
  object ProblemHub : Screen()
  object MyCropScreen : Screen()
  object FarmerDiary : Screen()
  object DiseaseLibrary : Screen()
  object WeatherScreen : Screen()
  object ExpertHelp : Screen()
  object AdminPanel : Screen()
  object ScanHistory : Screen()
}

class PlantDoctorViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

  private val repository: PlantDoctorRepository

  private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val _selectedCrop = MutableStateFlow<Crop>(INITIAL_CROPS[0])
  val selectedCrop: StateFlow<Crop> = _selectedCrop.asStateFlow()

  private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
  val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

  private val _selectedImageUri = MutableStateFlow<String?>(null)
  val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

  // Follow-up questions state
  val plantAge = MutableStateFlow("1–2 महीने")
  val symptomLocation = MutableStateFlow("पत्तियों में")
  val hasInsects = MutableStateFlow("पता नहीं")
  val flowerDropping = MutableStateFlow("नहीं")
  val leavesYellowing = MutableStateFlow("हाँ")
  val freeFormProblemText = MutableStateFlow("")

  private val _isAnalyzing = MutableStateFlow(false)
  val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

  private val _currentDiagnosis = MutableStateFlow<ScanResult?>(null)
  val currentDiagnosis: StateFlow<ScanResult?> = _currentDiagnosis.asStateFlow()

  private val _feedbackSubmitted = MutableStateFlow(false)
  val feedbackSubmitted: StateFlow<Boolean> = _feedbackSubmitted.asStateFlow()

  private val _weatherInfo = MutableStateFlow(WeatherInfo())
  val weatherInfo: StateFlow<WeatherInfo> = _weatherInfo.asStateFlow()

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private var tts: TextToSpeech? = null
  private var ttsInitialized = false

  init {
    val database = PlantDoctorDatabase.getDatabase(application)
    repository = PlantDoctorRepository(database)
    tts = TextToSpeech(application, this)
    loadWeather()
  }

  // Reactive DB queries
  val allScans: StateFlow<List<ScanResult>> = repository.allScans
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recentScans: StateFlow<List<ScanResult>> = repository.recentScans
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val scanCount: StateFlow<Int> = repository.scanCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val allDiaryEntries: StateFlow<List<FarmerDiaryEntry>> = repository.allDiaryEntries
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val diaryCount: StateFlow<Int> = repository.diaryCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val allMyCrops: StateFlow<List<MyCrop>> = repository.allMyCrops
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cropCount: StateFlow<Int> = repository.cropCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val allInquiries: StateFlow<List<ExpertInquiry>> = repository.allInquiries
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inquiryCount: StateFlow<Int> = repository.inquiryCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val allFeedback: StateFlow<List<FeedbackRecord>> = repository.allFeedback
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun navigateTo(screen: Screen) {
    stopSpeaking()
    _currentScreen.value = screen
  }

  fun setCrop(crop: Crop) {
    _selectedCrop.value = crop
  }

  fun setCapturedPhoto(bitmap: Bitmap?, uri: String? = null) {
    _capturedBitmap.value = bitmap
    _selectedImageUri.value = uri
  }

  fun startNewScanFlow() {
    _capturedBitmap.value = null
    _selectedImageUri.value = null
    _feedbackSubmitted.value = false
    _currentDiagnosis.value = null
    freeFormProblemText.value = ""
    _currentScreen.value = Screen.PlantScan
  }

  fun onPhotoChosen() {
    _currentScreen.value = Screen.CropSelection
  }

  fun onCropChosen(crop: Crop) {
    _selectedCrop.value = crop
    _currentScreen.value = Screen.AiQuestions
  }

  fun startProblemSpecialFlow(problemCategory: String) {
    when (problemCategory) {
      "flower_drop" -> {
        flowerDropping.value = "हाँ"
        symptomLocation.value = "फूलों में"
        freeFormProblemText.value = "फूल लगकर गिर रहे हैं और फल नहीं बन रहा"
      }
      "yellow_leaves" -> {
        leavesYellowing.value = "हाँ"
        symptomLocation.value = "पत्तियों में"
        freeFormProblemText.value = "पत्ते पीले पड़ रहे हैं"
      }
      "insects" -> {
        hasInsects.value = "हाँ"
        symptomLocation.value = "पत्तियों में"
        freeFormProblemText.value = "पौधे पर कीड़े या इल्ली दिखाई दे रही है"
      }
      "fruit_issue" -> {
        flowerDropping.value = "नहीं"
        symptomLocation.value = "फल में"
        freeFormProblemText.value = "फूल तो आ रहे हैं लेकिन फल नहीं लग रहा"
      }
    }
    _currentScreen.value = Screen.CropSelection
  }

  fun analyzePlant() {
    viewModelScope.launch {
      _isAnalyzing.value = true
      _feedbackSubmitted.value = false
      try {
        val result = repository.diagnosePlant(
          cropName = _selectedCrop.value.nameHindi,
          bitmap = _capturedBitmap.value,
          symptomLocation = symptomLocation.value,
          plantAge = plantAge.value,
          hasInsects = hasInsects.value,
          flowerDropping = flowerDropping.value,
          leavesYellowing = leavesYellowing.value,
          freeFormProblem = freeFormProblemText.value
        )
        // Persist scan result automatically in history
        val insertedId = repository.saveScan(result)
        _currentDiagnosis.value = result.copy(id = insertedId)
        _currentScreen.value = Screen.DiagnosisResult
      } catch (e: Exception) {
        Log.e("PlantDoctorVM", "Analysis error", e)
      } finally {
        _isAnalyzing.value = false
      }
    }
  }

  fun submitFeedback(isHelpful: Boolean, reason: String = "") {
    val diagnosisId = _currentDiagnosis.value?.id ?: 0L
    viewModelScope.launch {
      repository.submitFeedback(
        FeedbackRecord(
          scanId = diagnosisId,
          isHelpful = isHelpful,
          incorrectReasonHindi = reason
        )
      )
      _feedbackSubmitted.value = true
    }
  }

  fun addDiaryEntry(actionType: String, notes: String, crop: String = _selectedCrop.value.nameHindi) {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("hi", "IN"))
    val dateStr = sdf.format(Date())
    viewModelScope.launch {
      repository.addDiaryEntry(
        FarmerDiaryEntry(
          cropName = crop,
          actionTypeHindi = actionType,
          notesHindi = notes,
          dateDisplay = dateStr
        )
      )
    }
  }

  fun deleteDiaryEntry(id: Long) {
    viewModelScope.launch {
      repository.deleteDiaryEntry(id)
    }
  }

  fun addMyCrop(name: String, emoji: String, plot: String, sowingDate: String, ageDays: Int, notes: String = "") {
    viewModelScope.launch {
      repository.addMyCrop(
        MyCrop(
          cropNameHindi = name,
          cropEmoji = emoji,
          fieldOrPotName = plot,
          sowingDateString = sowingDate,
          daysSinceSowing = ageDays,
          notesHindi = notes
        )
      )
    }
  }

  fun deleteMyCrop(id: Long) {
    viewModelScope.launch {
      repository.deleteMyCrop(id)
    }
  }

  fun submitExpertInquiry(farmerName: String, crop: String, problem: String, hasVoice: Boolean) {
    viewModelScope.launch {
      repository.submitExpertInquiry(
        ExpertInquiry(
          farmerName = farmerName.ifBlank { "किसान भाई" },
          cropName = crop,
          problemDescriptionHindi = problem,
          hasVoiceNote = hasVoice
        )
      )
    }
  }

  fun handleVoiceQuery(voiceText: String) {
    freeFormProblemText.value = voiceText
    // Auto-detect crop if mentioned in Hindi
    for (crop in INITIAL_CROPS) {
      if (voiceText.contains(crop.nameHindi)) {
        _selectedCrop.value = crop
        break
      }
    }
    if (voiceText.contains("फूल") || voiceText.contains("झड़") || voiceText.contains("गिर")) {
      flowerDropping.value = "हाँ"
      symptomLocation.value = "फूलों में"
    }
    if (voiceText.contains("पील")) {
      leavesYellowing.value = "हाँ"
      symptomLocation.value = "पत्तियों में"
    }
    if (voiceText.contains("कीड़") || voiceText.contains("इल्ली") || voiceText.contains("मक्खी")) {
      hasInsects.value = "हाँ"
    }
    // Proceed directly to questions or analysis
    _currentScreen.value = Screen.AiQuestions
  }

  fun loadWeather(lat: Double = 26.8467, lon: Double = 80.9462, locName: String = "उत्तर/मध्य भारत कृषि क्षेत्र") {
    viewModelScope.launch {
      val w = repository.fetchWeather(lat, lon, locName)
      _weatherInfo.value = w
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale("hi", "IN"))
      ttsInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    }
  }

  fun speakResultText() {
    val diag = _currentDiagnosis.value ?: return
    if (!ttsInitialized || tts == null) {
      return
    }

    if (_isSpeaking.value) {
      stopSpeaking()
      return
    }

    val speechScript = buildString {
      append("फसल ${diag.cropName}। ")
      append("संभावित समस्या: ${diag.problemHindi}। ")
      append("संभावित कारण: ")
      diag.possibleCauses.forEach { append("$it। ") }
      append("अभी क्या करें: ")
      diag.recommendedActions.forEach { append("$it। ") }
    }

    _isSpeaking.value = true
    tts?.speak(speechScript, TextToSpeech.QUEUE_FLUSH, null, "PlantDoctorResult")
  }

  fun stopSpeaking() {
    _isSpeaking.value = false
    tts?.stop()
  }

  override fun onCleared() {
    super.onCleared()
    tts?.stop()
    tts?.shutdown()
  }
}
