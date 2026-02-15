@file:OptIn(
    kotlin.experimental.ExperimentalNativeApi::class,
    kotlinx.cinterop.ExperimentalForeignApi::class
)

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.get
import platform.android.JNINativeMethod

class JniMethodsBuilder {
    internal val defs = mutableListOf<NativeMethodDef>()

    fun int(name: String, fnPtr: COpaquePointer) {
        defs += NativeMethodDef(name, "()I", fnPtr)
    }

    fun string(name: String, fnPtr: COpaquePointer) {
        defs += NativeMethodDef(name, "()Ljava/lang/String;", fnPtr)
    }
}

data class NativeMethodDef(
    val name: String,
    val signature: String,
    val fnPtr: COpaquePointer
)

fun jni(block: JniMethodsBuilder.() -> Unit): List<NativeMethodDef> = JniMethodsBuilder().apply(block).defs

fun MemScope.buildJniNativeMethods(defs: List<NativeMethodDef>): CPointer<JNINativeMethod> {
    val arr = allocArray<JNINativeMethod>(defs.size)
    defs.forEachIndexed { i, d ->
        arr[i].name = d.name.cstr.getPointer(this)
        arr[i].signature = d.signature.cstr.getPointer(this)
        arr[i].fnPtr = d.fnPtr
    }
    return arr
}
