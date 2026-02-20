package com.ohyooo.demo

import Shared
import SharedApp
import SharedDataLoader
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            SharedApp(
                loader = object : SharedDataLoader {
                    override fun loadStatus(): Int = Shared.nativeGetStatus()

                    override fun loadContent(): String = Shared.nativeGetContent()
                },
            )
        }
    }
}
