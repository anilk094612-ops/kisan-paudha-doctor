package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.PlantDoctorDatabase
import com.example.data.model.ExpertInquiry
import com.example.data.model.FarmerDiaryEntry
import com.example.data.model.FeedbackRecord
import com.example.data.model.MyCrop
import com.example.data.model.ScanResult
import com.example.data.model.WeatherInfo
import com.example.data.service.GeminiPlantService
import com.example.data.service.WeatherService
import kotlinx.coroutines.flow.Flow

class PlantDoctorRepository(
  private val database: PlantDoctorDatabase,
  private val geminiService: GeminiPlantService = GeminiPlantService(),
  private val weatherService: WeatherService = WeatherService()
) {

  val allScans: Flow<List<ScanResult>> = database.scanDao().getAllScans()
  val recentScans: Flow<List<ScanResult>> = database.scanDao().getRecentScans()
  val scanCount: Flow<Int> = database.scanDao().getScanCount()

  val allDiaryEntries: Flow<List<FarmerDiaryEntry>> = database.diaryDao().getAllEntries()
  val diaryCount: Flow<Int> = database.diaryDao().getDiaryCount()

  val allMyCrops: Flow<List<MyCrop>> = database.myCropDao().getAllCrops()
  val cropCount: Flow<Int> = database.myCropDao().getCropCount()

  val allInquiries: Flow<List<ExpertInquiry>> = database.expertDao().getAllInquiries()
  val inquiryCount: Flow<Int> = database.expertDao().getInquiryCount()

  val allFeedback: Flow<List<FeedbackRecord>> = database.feedbackDao().getAllFeedback()
  val feedbackCount: Flow<Int> = database.feedbackDao().getFeedbackCount()

  suspend fun diagnosePlant(
    cropName: String,
    bitmap: Bitmap?,
    symptomLocation: String,
    plantAge: String,
    hasInsects: String,
    flowerDropping: String,
    leavesYellowing: String,
    freeFormProblem: String = ""
  ): ScanResult {
    return geminiService.diagnosePlant(
      cropName = cropName,
      bitmap = bitmap,
      symptomLocation = symptomLocation,
      plantAge = plantAge,
      hasInsects = hasInsects,
      flowerDropping = flowerDropping,
      leavesYellowing = leavesYellowing,
      freeFormProblem = freeFormProblem
    )
  }

  suspend fun saveScan(scan: ScanResult): Long {
    return database.scanDao().insertScan(scan)
  }

  suspend fun addDiaryEntry(entry: FarmerDiaryEntry): Long {
    return database.diaryDao().insertEntry(entry)
  }

  suspend fun deleteDiaryEntry(id: Long) {
    database.diaryDao().deleteEntry(id)
  }

  suspend fun addMyCrop(crop: MyCrop): Long {
    return database.myCropDao().insertCrop(crop)
  }

  suspend fun deleteMyCrop(id: Long) {
    database.myCropDao().deleteCrop(id)
  }

  suspend fun submitExpertInquiry(inquiry: ExpertInquiry): Long {
    return database.expertDao().insertInquiry(inquiry)
  }

  suspend fun submitFeedback(feedback: FeedbackRecord): Long {
    return database.feedbackDao().insertFeedback(feedback)
  }

  suspend fun fetchWeather(lat: Double = 26.8467, lon: Double = 80.9462, locName: String = "उत्तर/मध्य भारत कृषि क्षेत्र"): WeatherInfo {
    return weatherService.fetchWeather(lat, lon, locName)
  }
}
