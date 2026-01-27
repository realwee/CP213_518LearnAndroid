package com.example.a518lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.a518lablearnandroid.ui.theme._518LabLearnAndroidTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           _518LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android2",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
           }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    //var inputText by remember { mutableLongStateOf("" ) }
    //Column {
          //  Text(
           //     text = "Hello $name! say = ..." + inputText,
           //     modifier = modifier
         //   )
            //TextField(
             //   value = inputText ,
             //   onValueChange = {
            //        inputText = it
            //    }
           // TextField(
          //      value = inputText ,
           //     onValueChange = {}
           // )

    }



//}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    _518LabLearnAndroidTheme {
        Greeting2("Android")
    }
}