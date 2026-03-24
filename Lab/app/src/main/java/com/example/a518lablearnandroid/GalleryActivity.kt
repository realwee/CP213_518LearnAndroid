package com.example.a518lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.a518lablearnandroid.ui.theme._518LabLearnAndroidTheme

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _518LabLearnAndroidTheme {
                GalleryScreen()
            }
        }
    }
}

@Composable
fun GalleryScreen() {
    val context = LocalContext.current

    // State เก็บ Uri ของรูปที่เลือกได้
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // State เก็บข้อความสถานะ
    var statusMessage by remember { mutableStateOf("ยังไม่ได้เลือกรูปภาพ") }

    // กำหนด permission ที่ต้องการตาม API level
    // Android 13+ (API 33+) ใช้ READ_MEDIA_IMAGES
    // ต่ำกว่านั้นใช้ READ_EXTERNAL_STORAGE
    val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // ── Launcher 2: เปิดแกลเลอรีและรับ Uri กลับมา ──────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            statusMessage = "เลือกรูปภาพสำเร็จ!"
        } else {
            statusMessage = "ยกเลิกการเลือกรูปภาพ"
        }
    }

    // ── Launcher 1: ขอ Permission จากผู้ใช้ ─────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // ได้รับอนุญาตแล้ว → เปิดแกลเลอรีทันที
            galleryLauncher.launch("image/*")
            statusMessage = "ได้รับสิทธิ์แล้ว กำลังเปิดแกลเลอรี..."
        } else {
            statusMessage = "ถูกปฏิเสธสิทธิ์การเข้าถึงไฟล์"
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = "Gallery & Permission",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── กรอบแสดงรูปภาพ ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    // ใช้ Coil AsyncImage แสดงรูปจาก Uri
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "รูปภาพที่เลือก",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder เมื่อยังไม่มีรูป
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🖼️",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "รูปภาพจะแสดงที่นี่",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ── ข้อความสถานะ ─────────────────────────────────────────────────
            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            // ── ปุ่มเลือกรูปภาพ ──────────────────────────────────────────────
            Button(
                onClick = {
                    // ตรวจสอบสิทธิ์ก่อน
                    val permissionStatus = ContextCompat.checkSelfPermission(context, requiredPermission)
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        // ✅ มีสิทธิ์แล้ว → เปิดแกลเลอรีเลย
                        galleryLauncher.launch("image/*")
                    } else {
                        // ❌ ยังไม่มีสิทธิ์ → ขอ Permission
                        permissionLauncher.launch(requiredPermission)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "📂  เลือกรูปภาพ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ── ปุ่มล้างรูป ───────────────────────────────────────────────────
            if (selectedImageUri != null) {
                OutlinedButton(
                    onClick = {
                        selectedImageUri = null
                        statusMessage = "ล้างรูปภาพแล้ว"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🗑️  ล้างรูปภาพ",
                        fontSize = 16.sp
                    )
                }
            }

            // ── หมายเหตุ API Level ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "📌 Permission ที่ใช้: ${
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            "READ_MEDIA_IMAGES (Android 13+)"
                        else
                            "READ_EXTERNAL_STORAGE (Android ≤12)"
                    }",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
