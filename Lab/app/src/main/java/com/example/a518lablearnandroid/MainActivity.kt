package com.example.a518lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a518lablearnandroid.ui.theme._518LabLearnAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // Log.i("Lifecycle", "MainActivity")
        enableEdgeToEdge()
        setContent {
            RBGCardView(
                onNextActivity = {
                    startActivity(Intent(this, MenuActivity::class.java))
                }
            )

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _518LabLearnAndroidTheme {
        Greeting("Android")
    }
}
@Composable
fun RBGCardView (onNextActivity: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray)
        .padding(32.dp)){
        //hp
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Color.White)

        ){
            Text(
                text = "hp",
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .fillMaxWidth(fraction = 0.5F)
                    .background(color =Color.Red)
                    .padding(8.dp)
            )

        }
        Image(
            painter = painterResource(id=R.drawable.profile),
            contentDescription = "Profile",
            contentScale = ContentScale.Crop, // เพิ่มบรรทัดนี้ เพื่อให้รูปขยายเต็มพื้นที่ 200dp
            modifier = Modifier.run {
                size(200.dp)
                    .clip(RoundedCornerShape(50.dp)) // แล้วค่อยตัดขอบมน
                    .align(Alignment.CenterHorizontally)
                    .padding(top=16.dp)
                    .clickable {
                        onNextActivity.invoke()
                    }
            }


        )

        //status

        var str by remember { mutableStateOf(8) }
        var Agi by remember { mutableStateOf(10) }
        var Int by remember { mutableStateOf(15) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Button(onClick = {
                    str = str+1
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.baseline_arrow_upward_24),
                        contentDescription = "str_up",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(text="Str", fontSize = 32.sp)
                Text(text = str.toString(),fontSize = 32.sp)
                Button(onClick = {
                    str = str-1
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.outline_arrow_downward_24),
                        contentDescription = "str_down",
                        modifier = Modifier.size(20.dp)
                    )
                }
                //Text(text="-", fontSize = 32.sp,
                //   modifier = Modifier
                //       .clickable {
                //       str =str-1
                //   })
            }

            Column {
                Button(onClick = {
                    Agi = Agi+1
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.baseline_arrow_upward_24),
                        contentDescription = "agi_up",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "Agi",fontSize = 32.sp)
                Text(text=Agi.toString(),fontSize = 32.sp)
                Button(onClick = {
                    Agi = Agi-1
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.outline_arrow_downward_24),
                        contentDescription = "agi_down",
                        modifier = Modifier.size(20.dp)
                    )
                }

            }
            Column {
                Button(onClick = {
                    Int = Int+1
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.baseline_arrow_upward_24),
                        contentDescription = "int_up",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text="Int",fontSize = 32.sp)
                Text(text=Int.toString(),fontSize = 32.sp)
                Button(onClick = {
                    Int = Int-1
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.outline_arrow_downward_24),
                        contentDescription = "int_down",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

        }
    }
}
//เช็คชื่อ cp213 24/2
//ดช็คชื่อ  31/3
