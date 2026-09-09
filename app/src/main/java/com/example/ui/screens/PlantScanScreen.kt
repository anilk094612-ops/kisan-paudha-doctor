package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.File
import coil.compose.AsyncImage
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GeometricBg
import com.example.ui.theme.GeometricBorder
import com.example.ui.theme.GeometricSurface
import com.example.ui.theme.GeometricText
import com.example.ui.theme.GeometricTextMuted
import com.example.ui.theme.GeometricTextSubtle
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.SageContainer
import com.example.ui.theme.WarningRed
import com.example.ui.theme.WarningRedLight
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantScanScreen(viewModel: PlantDoctorViewModel) {
  val context = LocalContext.current
  val capturedBitmap by viewModel.capturedBitmap.collectAsStateWithLifecycle()
  val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()

  var selectedSampleName by remember { mutableStateOf<String?>(null) }
  val hasPhoto = capturedBitmap != null || selectedImageUri != null || selectedSampleName != null

  // Camera Permission State
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    hasCameraPermission = isGranted
    if (!isGranted) {
      Toast.makeText(context, "कैमरा अनुमति नहीं मिली। आप गैलरी से फोटो चुन सकते हैं।", Toast.LENGTH_SHORT).show()
    }
  }

  // Automatically request camera permission on screen entry if not granted
  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  // CameraX Controllers
  var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
  var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
  var isFlashOn by remember { mutableStateOf(false) }
  var isCapturing by remember { mutableStateOf(false) }
  var isAnalyzing by remember { mutableStateOf(false) }
  var cameraLoadError by remember { mutableStateOf<String?>(null) }
  var cameraRebindKey by remember { mutableIntStateOf(0) }
  var showShutterFlash by remember { mutableStateOf(false) }

  val imageCapture = remember {
    ImageCapture.Builder()
      .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
      .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
      .build()
  }

  // Normal Phone Camera (जो फोन में होता है - Default Android Camera App)
  var tempCameraPhotoUri by remember { mutableStateOf<Uri?>(null) }
  var currentCapturedFile by remember { mutableStateOf<File?>(null) }

  // Fallback Bitmap Preview capture via Phone's Default Camera App
  val normalCameraPreviewLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    isCapturing = false
    if (bitmap != null) {
      viewModel.setCapturedPhoto(bitmap, null)
      selectedSampleName = null
      cameraLoadError = null
      Toast.makeText(context, "✅ फोटो सफलतापूर्वक खींच ली गई!", Toast.LENGTH_SHORT).show()
    } else {
      cameraRebindKey++
    }
  }

  // High-Resolution capture with FileProvider via Phone's Default Camera App
  val normalCameraCaptureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture()
  ) { isSuccess: Boolean ->
    isCapturing = false
    val file = currentCapturedFile
    val hasFile = file != null && file.exists() && file.length() > 0
    if (isSuccess || hasFile) {
      try {
        val bitmap = if (hasFile) {
          BitmapFactory.decodeFile(file!!.absolutePath)
        } else if (tempCameraPhotoUri != null) {
          context.contentResolver.openInputStream(tempCameraPhotoUri!!)?.use {
            BitmapFactory.decodeStream(it)
          }
        } else null

        if (bitmap != null) {
          viewModel.setCapturedPhoto(bitmap, tempCameraPhotoUri?.toString() ?: file?.toURI()?.toString())
          selectedSampleName = null
          cameraLoadError = null
          Toast.makeText(context, "✅ फोन के कैमरे से फोटो सफलतापूर्वक ले ली गई!", Toast.LENGTH_SHORT).show()
        } else {
          normalCameraPreviewLauncher.launch(null)
        }
      } catch (e: Exception) {
        normalCameraPreviewLauncher.launch(null)
      }
    } else {
      // Fallback to preview capture if file capture was not completed
      try {
        normalCameraPreviewLauncher.launch(null)
      } catch (_: Exception) {
        cameraRebindKey++
      }
    }
  }

  fun launchNormalPhoneCamera() {
    try {
      // Unbind CameraX temporarily so hardware camera is released for external camera
      val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
      if (cameraProviderFuture.isDone) {
        cameraProviderFuture.get().unbindAll()
      }
    } catch (_: Exception) {}

    try {
      val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: File(context.cacheDir, "camera_photos")
      picturesDir.mkdirs()
      val photoFile = File.createTempFile("plant_${System.currentTimeMillis()}", ".jpg", picturesDir)
      currentCapturedFile = photoFile
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
      )
      tempCameraPhotoUri = uri
      normalCameraCaptureLauncher.launch(uri)
    } catch (e: Exception) {
      try {
        normalCameraPreviewLauncher.launch(null)
      } catch (e2: Exception) {
        Toast.makeText(context, "कैमरा खोलने में असमर्थ: ${e2.localizedMessage}", Toast.LENGTH_SHORT).show()
      }
    }
  }

  // System Fallback Intent Launcher (for backup or emulators without hardware camera)
  val systemCameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      viewModel.setCapturedPhoto(bitmap, null)
      selectedSampleName = null
    }
  }

  // Gallery Pick Visual Media Launcher (Android Modern Photo Picker)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        viewModel.setCapturedPhoto(bitmap, uri.toString())
        selectedSampleName = null
        Toast.makeText(context, "गैलरी से फोटो सफलतापूर्वक चुनी गई", Toast.LENGTH_SHORT).show()
      } catch (e: Exception) {
        viewModel.setCapturedPhoto(null, uri.toString())
        selectedSampleName = null
      }
    }
  }

  // Gallery Picker (Legacy GetContent fallback)
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        viewModel.setCapturedPhoto(bitmap, uri.toString())
        selectedSampleName = null
        Toast.makeText(context, "गैलरी से फोटो सफलतापूर्वक चुनी गई", Toast.LENGTH_SHORT).show()
      } catch (e: Exception) {
        viewModel.setCapturedPhoto(null, uri.toString())
        selectedSampleName = null
      }
    }
  }

  fun openGalleryPicker() {
    try {
      photoPickerLauncher.launch(
        androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
      )
    } catch (e: Exception) {
      // Fallback to standard GetContent for older API levels
      galleryLauncher.launch("image/*")
    }
  }

  // Shutter action (Screen Capture with CameraX + graceful fallback)
  fun takePhotoWithCameraX() {
    if (isCapturing) return
    isCapturing = true
    showShutterFlash = true

    try {
      val executor = ContextCompat.getMainExecutor(context)
      imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
          override fun onCaptureSuccess(imageProxy: ImageProxy) {
            try {
              val bitmap = imageProxy.toBitmap()
              val rotationDegrees = imageProxy.imageInfo.rotationDegrees
              val rotatedBitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
              } else {
                bitmap
              }
              viewModel.setCapturedPhoto(rotatedBitmap, null)
              selectedSampleName = null
              cameraLoadError = null
              Toast.makeText(context, "✅ फोटो सफलतापूर्वक खींच ली गई!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
              Toast.makeText(context, "फोटो सुरक्षित की जा रही है...", Toast.LENGTH_SHORT).show()
              launchNormalPhoneCamera()
            } finally {
              imageProxy.close()
              isCapturing = false
              showShutterFlash = false
            }
          }

          override fun onError(exception: ImageCaptureException) {
            isCapturing = false
            showShutterFlash = false
            cameraLoadError = exception.localizedMessage
            Toast.makeText(context, "फोन का कैमरा ऐप खोला जा रहा है...", Toast.LENGTH_SHORT).show()
            launchNormalPhoneCamera()
          }
        }
      )
    } catch (e: Exception) {
      isCapturing = false
      showShutterFlash = false
      Toast.makeText(context, "फोन का कैमरा ऐप खोला जा रहा है...", Toast.LENGTH_SHORT).show()
      launchNormalPhoneCamera()
    }
  }

  Scaffold(
    topBar = {
      // Geometric Balance Top Navigation Header
      Surface(
        color = GeometricSurface,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GeometricBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "वापस जाएं",
              tint = ForestGreen,
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "पौधा कैमरा स्कैन",
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Text(
              text = "CameraX AI लाइव स्कैनर",
              fontSize = 12.sp,
              color = GeometricTextMuted
            )
          }

          if (hasPhoto) {
            // Reset photo button
            IconButton(
              onClick = {
                viewModel.setCapturedPhoto(null, null)
                selectedSampleName = null
              },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "रीसेट करें",
                tint = OliveGreen
              )
            }
          }
        }
      }
    },
    containerColor = GeometricBg
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // 1. In-App Camera Viewfinder / Preview Frame
      item {
        Card(
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (hasPhoto) GeometricSurface else Color(0xFF141A14)
          ),
          border = BorderStroke(
            width = if (hasPhoto) 3.dp else 2.dp,
            color = if (hasPhoto) OliveGreen else SageContainer
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
        ) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            when {
              // Captured Bitmap Display
              capturedBitmap != null -> {
                Image(
                  bitmap = capturedBitmap!!.asImageBitmap(),
                  contentDescription = "खींची गई फोटो",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
              // Gallery Image Display
              selectedImageUri != null -> {
                AsyncImage(
                  model = selectedImageUri,
                  contentDescription = "गैलरी से चुनी गई फोटो",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
              // Sample Photo Selected Display
              selectedSampleName != null -> {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8F2E4)),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = when (selectedSampleName) {
                      "मिर्च फूल झड़ना" -> "🌶️"
                      "टमाटर झुलसा" -> "🍅"
                      else -> "🍆"
                    },
                    fontSize = 72.sp
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "नमूना: $selectedSampleName",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                  )
                  Text(
                    text = "AI विश्लेषण हेतु तैयार",
                    fontSize = 13.sp,
                    color = GeometricTextMuted
                  )
                }
              }
              // Live CameraX Feed when Permission Granted
              hasCameraPermission -> {
                Box(modifier = Modifier.fillMaxSize()) {
                  key(lensFacing, cameraRebindKey) {
                    CameraXLivePreview(
                      modifier = Modifier.fillMaxSize(),
                      lensFacing = lensFacing,
                      imageCapture = imageCapture,
                      onCameraBound = { camera ->
                        cameraControl = camera.cameraControl
                      },
                      onError = { error ->
                        cameraLoadError = error
                      }
                    )
                  }

                  // Viewfinder corners (Geometric Reticle Overlay)
                  ViewfinderCornerOverlay()

                  // Top Live Camera Controls Overlay
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp)
                      .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // Live Status Badge
                    Surface(
                      color = Color.Black.copy(alpha = 0.55f),
                      shape = RoundedCornerShape(12.dp)
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Box(
                          modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "कैमरा लाइव",
                          color = Color.White,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }

                    // Torch & Lens Switch Controls
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      // Torch toggle
                      Box(
                        modifier = Modifier
                          .size(36.dp)
                          .clip(CircleShape)
                          .background(Color.Black.copy(alpha = 0.55f))
                          .clickable {
                            isFlashOn = !isFlashOn
                            cameraControl?.enableTorch(isFlashOn)
                          },
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                          contentDescription = "टॉर्च चालू/बंद",
                          tint = if (isFlashOn) HarvestAmber else Color.White,
                          modifier = Modifier.size(20.dp)
                        )
                      }

                      // Lens Switch toggle
                      Box(
                        modifier = Modifier
                          .size(36.dp)
                          .clip(CircleShape)
                          .background(Color.Black.copy(alpha = 0.55f))
                          .clickable {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                              CameraSelector.LENS_FACING_FRONT
                            } else {
                              CameraSelector.LENS_FACING_BACK
                            }
                          },
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = Icons.Default.Cameraswitch,
                          contentDescription = "कैमरा बदलें",
                          tint = Color.White,
                          modifier = Modifier.size(20.dp)
                        )
                      }
                    }
                  }

                  // Center Guidance or Error
                  if (cameraLoadError == null) {
                    Column(
                      modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(
                        text = "बीमार पत्ते या फसल को बीच में रखें",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center
                      )
                    }
                  } else {
                    Surface(
                      color = Color.Black.copy(alpha = 0.75f),
                      shape = RoundedCornerShape(16.dp),
                      modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                    ) {
                      Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                        Text(
                          text = "⚠️ स्क्रीन कैमरा व्यस्त है",
                          color = HarvestAmber,
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = "कृपया नीचे 'फोन का सामान्य कैमरा' बटन दबाएं",
                          color = Color.White,
                          fontSize = 12.sp,
                          textAlign = TextAlign.Center
                        )
                      }
                    }
                  }

                  // Live Shutter Control Bar inside the Viewfinder
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .align(Alignment.BottomCenter)
                      .background(
                        Brush.verticalGradient(
                          listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                      )
                      .padding(horizontal = 24.dp, vertical = 12.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      // Gallery shortcut button
                      Surface(
                        onClick = { openGalleryPicker() },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(46.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "गैलरी",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                          )
                        }
                      }

                      // Main Large Shutter Button (Classic Camera Shutter Ring)
                      Box(
                        modifier = Modifier
                          .size(74.dp)
                          .clip(CircleShape)
                          .background(Color.White.copy(alpha = 0.35f))
                          .clickable(enabled = !isCapturing) {
                            takePhotoWithCameraX()
                          }
                          .padding(5.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Box(
                          modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White),
                          contentAlignment = Alignment.Center
                        ) {
                          if (isCapturing) {
                            CircularProgressIndicator(
                              color = ForestGreen,
                              modifier = Modifier.size(28.dp),
                              strokeWidth = 3.dp
                            )
                          } else {
                            Icon(
                              imageVector = Icons.Default.CameraAlt,
                              contentDescription = "फोटो खींचे",
                              tint = ForestGreen,
                              modifier = Modifier.size(34.dp)
                            )
                          }
                        }
                      }

                      // Phone Native Camera Shortcut Button
                      Surface(
                        onClick = { launchNormalPhoneCamera() },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(46.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "फोन कैमरा",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                          )
                        }
                      }
                    }
                  }

                  // Shutter Flash Visual Feedback
                  if (showShutterFlash) {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.85f))
                    )
                  }
                }
              }
              // Permission Not Granted State
              else -> {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E281D))
                    .padding(20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(62.dp)
                      .clip(CircleShape)
                      .background(SageContainer.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(32.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "कैमरा अनुमति आवश्यक है",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "खेत में पौधे की लाइव फोटो लेने के लिए अनुमति दें",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(14.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                      colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                      shape = RoundedCornerShape(14.dp),
                      modifier = Modifier.testTag("btn_request_permission")
                    ) {
                      Text("📷 अनुमति दें", fontWeight = FontWeight.Bold)
                    }
                    Button(
                      onClick = { launchNormalPhoneCamera() },
                      colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                      shape = RoundedCornerShape(14.dp),
                      modifier = Modifier.testTag("btn_normal_camera_direct")
                    ) {
                      Text("📱 फोन का सामान्य कैमरा", fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            // Photo Ready Confirmation Banner
            if (hasPhoto) {
              Surface(
                color = OliveGreen,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .padding(top = 12.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "फोटो तैयार है • स्पष्ट छवि",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // 2. Large Action Buttons (बड़े बटन - Primary Capture, Phone Camera & Gallery)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Large Primary Capture Button
          Button(
            onClick = {
              if (hasPhoto) {
                // Reset photo so farmer can scan again
                viewModel.setCapturedPhoto(null, null)
                selectedSampleName = null
                cameraLoadError = null
                cameraRebindKey++
              } else if (hasCameraPermission) {
                takePhotoWithCameraX()
              } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
              }
            },
            enabled = !isCapturing,
            modifier = Modifier
              .fillMaxWidth()
              .height(66.dp)
              .testTag("btn_take_photo"),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (hasPhoto) HarvestAmber else ForestGreen
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              if (isCapturing) {
                CircularProgressIndicator(
                  color = Color.White,
                  modifier = Modifier.size(26.dp),
                  strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                  text = "फोटो खींची जा रही है...",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              } else {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (hasPhoto) Icons.Default.Refresh else Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                  Text(
                    text = if (hasPhoto) {
                      "🔄 दूसरी नई फोटो खींचें"
                    } else if (hasCameraPermission) {
                      "📷 अभी फोटो खींचें (Take Photo)"
                    } else {
                      "📷 कैमरा चालू करें व फोटो लें"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Text(
                    text = if (hasPhoto) "पिछली फोटो हटाकर नई तस्वीर लें" else "स्क्रीन पर दिख रहे पौधे की फोटो लें",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.90f)
                  )
                }
              }
            }
          }

          // Dedicated Button: Phone's Normal Camera App (जो फोन में होता है)
          Card(
            onClick = {
              if (hasPhoto) {
                viewModel.setCapturedPhoto(null, null)
                selectedSampleName = null
              }
              launchNormalPhoneCamera()
            },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = GeometricSurface),
            border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp)
              .testTag("btn_normal_camera")
          ) {
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "फोन का कैमरा ऐप",
                tint = ForestGreen,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "📱 फोन का सामान्य कैमरा ऐप खोलें",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
              )
            }
          }

          // Large Gallery Button (58.dp Height)
          Card(
            onClick = { openGalleryPicker() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = GeometricSurface),
            border = BorderStroke(1.5.dp, Color(0xFFBCC6B8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp)
              .testTag("gallery_pick_button")
          ) {
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "गैलरी से चुनें",
                tint = ForestGreen,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "🖼️ फोन गैलरी से फोटो चुनें (Gallery)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
              )
            }
          }
        }
      }

      // 3. Clear Hindi Instructions Card (स्पष्ट हिंदी निर्देश - 4 आसान कदम)
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = GeometricSurface),
          border = BorderStroke(1.dp, GeometricBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(SageContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "💡", fontSize = 20.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "सही फोटो लेने के 4 जरूरी नियम",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreen
                )
                Text(
                  text = "सटीक AI रोग पहचान के लिए पालन करें",
                  fontSize = 12.sp,
                  color = GeometricTextMuted
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Clear Step Instructions
            InstructionStepRow(
              number = "1",
              icon = Icons.Default.Straighten,
              title = "15-20 सेमी की दूरी रखें",
              subtitle = "कैमरा न ज्यादा दूर हो, न ज्यादा पास"
            )
            Spacer(modifier = Modifier.height(10.dp))
            InstructionStepRow(
              number = "2",
              icon = Icons.Default.LightMode,
              title = "साफ और अच्छी रोशनी में लें",
              subtitle = "धूप या दिन के उजाले में फोटो सबसे सही आती है (कम रोशनी में टॉर्च चालू करें)"
            )
            Spacer(modifier = Modifier.height(10.dp))
            InstructionStepRow(
              number = "3",
              icon = Icons.Default.TrackChanges,
              title = "रोग वाला हिस्सा फ्रेम के बीच रखें",
              subtitle = "पत्ते के धब्बे, कीड़े या सूखे भाग पर फोकस करें"
            )
            Spacer(modifier = Modifier.height(10.dp))
            InstructionStepRow(
              number = "4",
              icon = Icons.Default.CheckCircle,
              title = "हाथ स्थिर रखें (धुंधली न हो)",
              subtitle = "साफ दिखने पर ही AI सही इलाज बता पाएगा"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dos and Don'ts comparison badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                color = SageContainer.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, OliveGreen.copy(alpha = 0.3f)),
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "✅", fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "पास से साफ पत्ता",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                  )
                }
              }

              Surface(
                color = WarningRedLight,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, WarningRed.copy(alpha = 0.2f)),
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "❌", fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "बहुत दूर या अंधेरा",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningRed
                  )
                }
              }
            }
          }
        }
      }

      // 4. Quick Sample Photos for Instant Village / Offline Testing
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = GeometricSurface),
          border = BorderStroke(1.dp, GeometricBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "या तुरंत जांच के लिए नमूना चुनें:",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = ForestGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              SamplePhotoCardGeometric(
                title = "मिर्च फूल झड़ना",
                emoji = "🌶️",
                modifier = Modifier.weight(1f),
                isSelected = selectedSampleName == "मिर्च फूल झड़ना",
                onClick = {
                  selectedSampleName = "मिर्च फूल झड़ना"
                  viewModel.flowerDropping.value = "हाँ"
                  viewModel.symptomLocation.value = "फूलों में"
                }
              )
              SamplePhotoCardGeometric(
                title = "टमाटर झुलसा",
                emoji = "🍅",
                modifier = Modifier.weight(1f),
                isSelected = selectedSampleName == "टमाटर झुलसा",
                onClick = {
                  selectedSampleName = "टमाटर झुलसा"
                  viewModel.leavesYellowing.value = "हाँ"
                  viewModel.symptomLocation.value = "पत्तियों में"
                }
              )
              SamplePhotoCardGeometric(
                title = "बैंगन इल्ली",
                emoji = "🍆",
                modifier = Modifier.weight(1f),
                isSelected = selectedSampleName == "बैंगन इल्ली",
                onClick = {
                  selectedSampleName = "बैंगन इल्ली"
                  viewModel.hasInsects.value = "हाँ"
                  viewModel.symptomLocation.value = "फल में"
                }
              )
            }
          }
        }
      }

      // 5. Large Proceed Button (आगे बढ़ें - फसल चुनें)
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Button(
          onClick = { viewModel.onPhotoChosen() },
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .testTag("btn_proceed_to_crop"),
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (hasPhoto) HarvestAmber else OliveGreen
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (hasPhoto) "आगे बढ़ें (फसल चुनें) ▶" else "फसल चुनकर आगे बढ़ें ▶",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
fun CameraXLivePreview(
  modifier: Modifier = Modifier,
  lensFacing: Int,
  imageCapture: ImageCapture,
  onCameraBound: (Camera) -> Unit,
  onError: (String) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  AndroidView(
    modifier = modifier,
    factory = { ctx: Context ->
      val previewView = PreviewView(ctx).apply {
        scaleType = PreviewView.ScaleType.FILL_CENTER
      }

      val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
      cameraProviderFuture.addListener({
        try {
          val cameraProvider = cameraProviderFuture.get()
          val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
          }

          val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

          cameraProvider.unbindAll()
          val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
          )
          onCameraBound(camera)
        } catch (exc: Exception) {
          onError(exc.localizedMessage ?: "कैमरा लोड नहीं हुआ")
        }
      }, ContextCompat.getMainExecutor(ctx))

      previewView
    }
  )
}

@Composable
fun InstructionStepRow(
  number: String,
  icon: ImageVector,
  title: String,
  subtitle: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(SageContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = ForestGreen,
        modifier = Modifier.size(20.dp)
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111410)
      )
      Text(
        text = subtitle,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF262F23)
      )
    }
  }
}

@Composable
fun ViewfinderCornerOverlay() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(18.dp)
  ) {
    // Top-Left Corner Bracket
    Box(
      modifier = Modifier
        .align(Alignment.TopStart)
        .size(32.dp)
        .border(
          width = 3.dp,
          color = SageContainer,
          shape = RoundedCornerShape(topStart = 8.dp)
        )
    )
    // Top-Right Corner Bracket
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(32.dp)
        .border(
          width = 3.dp,
          color = SageContainer,
          shape = RoundedCornerShape(topEnd = 8.dp)
        )
    )
    // Bottom-Left Corner Bracket
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .size(32.dp)
        .border(
          width = 3.dp,
          color = SageContainer,
          shape = RoundedCornerShape(bottomStart = 8.dp)
        )
    )
    // Bottom-Right Corner Bracket
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .size(32.dp)
        .border(
          width = 3.dp,
          color = SageContainer,
          shape = RoundedCornerShape(bottomEnd = 8.dp)
        )
    )
  }
}

@Composable
fun SamplePhotoCardGeometric(
  title: String,
  emoji: String,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) ForestGreen else Color(0xFFF1F6F0)
    ),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.5.dp,
      color = if (isSelected) ForestGreen else Color(0xFFBCC6B8)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
    modifier = modifier.height(78.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(text = emoji, fontSize = 26.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) Color.White else Color(0xFF111410),
        textAlign = TextAlign.Center,
        maxLines = 1
      )
    }
  }
}
