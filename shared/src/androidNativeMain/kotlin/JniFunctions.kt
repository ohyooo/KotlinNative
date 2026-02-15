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

fun nativeGetStatus(env: CPointer<JNIEnvVar>?, thiz: jobject?): jint = getStatus()

fun nativeGetContent(env: CPointer<JNIEnvVar>?, thiz: jobject?): jstring? {
    if (env == null) return null
    val content = getContent()
    return memScoped {
        env.pointed.pointed?.NewStringUTF?.invoke(env, content.cstr.getPointer(this))
    }
}
