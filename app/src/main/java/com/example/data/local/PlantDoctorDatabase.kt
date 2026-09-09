package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ExpertInquiry
import com.example.data.model.FarmerDiaryEntry
import com.example.data.model.FeedbackRecord
import com.example.data.model.MyCrop
import com.example.data.model.ScanResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
  @Query("SELECT * FROM scan_results ORDER BY timestamp DESC")
  fun getAllScans(): Flow<List<ScanResult>>

  @Query("SELECT * FROM scan_results WHERE id = :id LIMIT 1")
  suspend fun getScanById(id: Long): ScanResult?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScan(scan: ScanResult): Long

  @Query("SELECT COUNT(*) FROM scan_results")
  fun getScanCount(): Flow<Int>

  @Query("SELECT * FROM scan_results ORDER BY timestamp DESC LIMIT 5")
  fun getRecentScans(): Flow<List<ScanResult>>
}

@Dao
interface DiaryDao {
  @Query("SELECT * FROM farmer_diary ORDER BY timestamp DESC")
  fun getAllEntries(): Flow<List<FarmerDiaryEntry>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEntry(entry: FarmerDiaryEntry): Long

  @Query("DELETE FROM farmer_diary WHERE id = :id")
  suspend fun deleteEntry(id: Long)

  @Query("SELECT COUNT(*) FROM farmer_diary")
  fun getDiaryCount(): Flow<Int>
}

@Dao
interface MyCropDao {
  @Query("SELECT * FROM my_crops ORDER BY timestamp DESC")
  fun getAllCrops(): Flow<List<MyCrop>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrop(crop: MyCrop): Long

  @Query("DELETE FROM my_crops WHERE id = :id")
  suspend fun deleteCrop(id: Long)

  @Query("SELECT COUNT(*) FROM my_crops")
  fun getCropCount(): Flow<Int>
}

@Dao
interface ExpertDao {
  @Query("SELECT * FROM expert_inquiries ORDER BY timestamp DESC")
  fun getAllInquiries(): Flow<List<ExpertInquiry>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInquiry(inquiry: ExpertInquiry): Long

  @Query("SELECT COUNT(*) FROM expert_inquiries")
  fun getInquiryCount(): Flow<Int>
}

@Dao
interface FeedbackDao {
  @Query("SELECT * FROM feedback_records ORDER BY timestamp DESC")
  fun getAllFeedback(): Flow<List<FeedbackRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFeedback(record: FeedbackRecord): Long

  @Query("SELECT COUNT(*) FROM feedback_records")
  fun getFeedbackCount(): Flow<Int>
}

@Database(
  entities = [
    ScanResult::class,
    FarmerDiaryEntry::class,
    MyCrop::class,
    ExpertInquiry::class,
    FeedbackRecord::class
  ],
  version = 1,
  exportSchema = false
)
abstract class PlantDoctorDatabase : RoomDatabase() {
  abstract fun scanDao(): ScanDao
  abstract fun diaryDao(): DiaryDao
  abstract fun myCropDao(): MyCropDao
  abstract fun expertDao(): ExpertDao
  abstract fun feedbackDao(): FeedbackDao

  companion object {
    @Volatile
    private var INSTANCE: PlantDoctorDatabase? = null

    fun getDatabase(context: Context): PlantDoctorDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PlantDoctorDatabase::class.java,
          "kisan_plant_doctor.db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
