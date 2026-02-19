@file:OptIn(
    kotlin.experimental.ExperimentalNativeApi::class,
    kotlinx.cinterop.ExperimentalForeignApi::class
)

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import platform.android.JNIEnvVar
import platform.android.jint
import platform.android.jobject
import platform.android.jstring

internal fun nativeGetStatus(env: CPointer<JNIEnvVar>?, thiz: jobject?): jint = SharedBridge.nativeGetStatus()

internal fun nativeGetContent(env: CPointer<JNIEnvVar>?, thiz: jobject?): jstring? {
    if (env == null) return null
    val content = SharedBridge.nativeGetContent()
    return memScoped {
        env.pointed.pointed?.NewStringUTF?.invoke(env, content.cstr.getPointer(this))
    }
}
