@file:OptIn(
    kotlin.experimental.ExperimentalNativeApi::class,
    kotlinx.cinterop.ExperimentalForeignApi::class
)

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.value
import platform.android.JNIEnvVar
import platform.android.JNI_ERR
import platform.android.JNI_OK
import platform.android.JNI_VERSION_1_6
import platform.android.JavaVMVar
import platform.android.jint

private val methodsDef = jni {
    int("nativeGetStatus", staticCFunction(::nativeGetStatus).reinterpret())
    string("nativeGetContent", staticCFunction(::nativeGetContent).reinterpret())
}

@CName("JNI_OnLoad")
fun JNI_OnLoad(vm: CPointer<JavaVMVar>?, reserved: COpaquePointer?): jint {
    if (vm == null) return JNI_ERR

    memScoped {
        val envPtr = alloc<CPointerVar<JNIEnvVar>>()
        val getEnv = vm.pointed.pointed?.GetEnv ?: return JNI_ERR
        if (getEnv(vm, envPtr.ptr.reinterpret(), JNI_VERSION_1_6) != JNI_OK) return JNI_ERR

        val env = envPtr.value ?: return JNI_ERR
        val fns = env.pointed.pointed ?: return JNI_ERR

        // default package's object Shared => "Shared"
        val clazz = fns.FindClass?.invoke(env, "Shared".cstr.ptr) ?: return JNI_ERR

        val methods = buildJniNativeMethods(methodsDef)
        val reg = fns.RegisterNatives?.invoke(env, clazz, methods, methodsDef.size) ?: return JNI_ERR
        if (reg != JNI_OK) return JNI_ERR
    }

    return JNI_VERSION_1_6
}
