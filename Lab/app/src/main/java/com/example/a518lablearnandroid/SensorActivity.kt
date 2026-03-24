package com.example.a518lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.a518lablearnandroid.ui.theme._518LabLearnAndroidTheme
import kotlin.math.sqrt

class SensorActivity : ComponentActivity() {

    // viewModels() สร้าง ViewModel แบบ scoped กับ Activity นี้
    private val sensorViewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _518LabLearnAndroidTheme {
                SensorScreen(viewModel = sensorViewModel)
            }
        }
    }
}

// ── Composable หน้าจอหลัก ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(viewModel: SensorViewModel) {
    val context = LocalContext.current

    // ── collectAsState() แปลง StateFlow ➜ Compose State ────────────────────
    // ทันที StateFlow อัปเดต → Compose recompose และวาดตัวเลขใหม่อัตโนมัติ
    val accel by viewModel.accelerometerData.collectAsState()
    val location by viewModel.locationData.collectAsState()
    val isTracking by viewModel.isLocationTracking.collectAsState()

    // ── Permission Launcher สำหรับ GPS ─────────────────────────────────────
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startLocationTracking()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensor & Location", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Section 1: Accelerometer ─────────────────────────────────────
            SectionCard(
                title = "📱 Accelerometer",
                subtitle = "ค่า X / Y / Z แบบ Real-time (m/s²)"
            ) {
                // แสดงแถบกราฟิก + ตัวเลขสำหรับแต่ละแกน
                SensorAxisRow(label = "X", value = accel.x, color = MaterialTheme.colorScheme.primary)
                SensorAxisRow(label = "Y", value = accel.y, color = MaterialTheme.colorScheme.secondary)
                SensorAxisRow(label = "Z", value = accel.z, color = MaterialTheme.colorScheme.tertiary)

                Spacer(modifier = Modifier.height(8.dp))

                // คำนวณขนาดแรงรวม (magnitude)
                val magnitude = sqrt(accel.x * accel.x + accel.y * accel.y + accel.z * accel.z)
                Text(
                    text = "แรงรวม (|G|): ${"%.3f".format(magnitude)} m/s²",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Section 2: GPS / Location ────────────────────────────────────
            SectionCard(
                title = "📍 GPS Location",
                subtitle = "พิกัดตำแหน่ง Real-time"
            ) {
                if (isTracking) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Latitude
                        CoordinateBox(
                            label = "Latitude",
                            value = "%.6f".format(location.latitude),
                            modifier = Modifier.weight(1f)
                        )
                        // Longitude
                        CoordinateBox(
                            label = "Longitude",
                            value = "%.6f".format(location.longitude),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ความแม่นยำ: ±${"%.1f".format(location.accuracy)} เมตร",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "กด \"เริ่มติดตาม GPS\" เพื่อรับพิกัดตำแหน่ง",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ปุ่มสลับ Start / Stop GPS
                if (!isTracking) {
                    Button(
                        onClick = {
                            val permission = Manifest.permission.ACCESS_FINE_LOCATION
                            if (ContextCompat.checkSelfPermission(context, permission)
                                == PackageManager.PERMISSION_GRANTED
                            ) {
                                viewModel.startLocationTracking()
                            } else {
                                locationPermissionLauncher.launch(permission)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🛰️  เริ่มติดตาม GPS")
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.stopLocationTracking() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⏹  หยุดติดตาม GPS")
                    }
                }
            }

            // ── Note: Architecture Explanation ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "🏗️ Architecture: Hardware → SensorViewModel (StateFlow) → collectAsState() → UI",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

// ── Reusable Components ────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SensorAxisRow(label: String, value: Float, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Progress bar แสดงขนาดค่า (range ±20 m/s²)
        val normalized = ((value + 20f) / 40f).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(normalized)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.5f), color)
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        // ตัวเลขค่าจริง
        Text(
            text = "%+.3f".format(value),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun CoordinateBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
