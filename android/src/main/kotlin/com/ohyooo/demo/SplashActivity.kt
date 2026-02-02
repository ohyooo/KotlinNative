package com.ohyooo.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

class SplashActivity : ComponentActivity() {
    private external fun nativeGetStatus(): Int

    private external fun nativeGetContent(): String

    companion object {
        init {
            System.loadLibrary("shared")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val s = nativeGetStatus()
        val c = nativeGetContent()


        setContent {
//            val status by rememberSaveable { mutableStateOf("${s}") }
            val content by rememberSaveable { mutableStateOf(c) }
//            Log.d("SplashActivity", "native status=$status, content=${content.take(200)}")

            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
//                Text(text = status, fontSize = 18.sp, color = Color.Black)
                Text(text = content, fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}
