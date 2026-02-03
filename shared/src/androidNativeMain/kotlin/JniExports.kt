@file:OptIn(
    kotlin.experimental.ExperimentalNativeApi::class,
    ExperimentalForeignApi::class
)

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import platform.android.JNIEnvVar
import platform.android.jint
import platform.android.jobject
import platform.android.jstring

@CName("Java_com_ohyooo_demo_Shared_nativeGetStatus")
fun nativeGetStatus(env: CPointer<JNIEnvVar>?, thiz: jobject?): jint {
    return getStatus()
}

@CName("Java_com_ohyooo_demo_Shared_nativeGetContent")
fun nativeGetContent(env: CPointer<JNIEnvVar>?, thiz: jobject?): jstring? {
    if (env == null) {
        return null
    }

    val content = getContent()
    return memScoped {
        val utf = content.cstr.getPointer(this)
        val newStringUtf = env.pointed.pointed?.NewStringUTF
        newStringUtf?.invoke(env, utf)
    }
}
