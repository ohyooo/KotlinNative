package com.ohyooo.demo


object Shared {
    init {
        System.loadLibrary("shared")
    }

    external fun nativeGetStatus(): Int

    external fun nativeGetContent(): String
}
