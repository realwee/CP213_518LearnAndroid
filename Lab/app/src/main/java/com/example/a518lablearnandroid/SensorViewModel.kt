package com.example.a518lablearnandroid

import android.annotation.SuppressLint
import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ── Data class รวมค่าจาก Accelerometer ─────────────────────────────────────
data class AccelerometerData(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
)

// ── Data class รวมพิกัด GPS ──────────────────────────────────────────────────
data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Float = 0f
)

/**
 * SensorViewModel
 *
 * รับผิดชอบดูแล:
 * 1. การลงทะเบียน/ยกเลิก SensorEventListener กับ Accelerometer
 * 2. การลงทะเบียน/ยกเลิก LocationCallback กับ FusedLocationProviderClient
 * 3. เปิดเผยข้อมูลผ่าน StateFlow ให้ Compose UI subscribe
 */
class SensorViewModel(application: Application) : AndroidViewModel(application) {

    // ── Accelerometer Setup ──────────────────────────────────────────────────
    private val sensorManager: SensorManager =
        application.getSystemService(SensorManager::class.java)

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // MutableStateFlow (private) — ViewModel อัปเดตเอง
    private val _accelerometerData = MutableStateFlow(AccelerometerData())

    // StateFlow (public) — Compose อ่านได้อย่างเดียว
    val accelerometerData: StateFlow<AccelerometerData> = _accelerometerData.asStateFlow()

    // ── GPS / Location Setup ─────────────────────────────────────────────────
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _locationData = MutableStateFlow(LocationData())
    val locationData: StateFlow<LocationData> = _locationData.asStateFlow()

    // State ว่า GPS กำลังทำงานอยู่หรือเปล่า
    private val _isLocationTracking = MutableStateFlow(false)
    val isLocationTracking: StateFlow<Boolean> = _isLocationTracking.asStateFlow()

    // ── SensorEventListener implementation ──────────────────────────────────
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                // อัปเดตค่าใน StateFlow → Compose จะ recompose อัตโนมัติ
                _accelerometerData.value = AccelerometerData(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2]
                )
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // ไม่จำเป็นต้องทำอะไรในที่นี้
        }
    }

    // ── LocationCallback implementation ──────────────────────────────────────
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location: Location = result.lastLocation ?: return
            _locationData.value = LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy
            )
        }
    }

    // ── เริ่มรับค่า Accelerometer (ลงทะเบียน Listener) ──────────────────────
    init {
        startAccelerometer()
    }

    private fun startAccelerometer() {
        accelerometer?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_UI // อัปเดตความเร็วพอสมควร ไม่ทำให้ UI กระตุก
            )
        }
    }

    // ── เริ่มรับพิกัด GPS ─────────────────────────────────────────────────────
    @SuppressLint("MissingPermission") // Permission ถูกตรวจแล้วใน UI Layer
    fun startLocationTracking() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L // อัปเดตทุก 2 วินาที
        ).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
        _isLocationTracking.value = true
    }

    // ── หยุดรับพิกัด GPS ─────────────────────────────────────────────────────
    fun stopLocationTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _isLocationTracking.value = false
    }

    // ── ล้างทรัพยากรเมื่อ ViewModel ถูก destroy ───────────────────────────────
    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(sensorEventListener)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
