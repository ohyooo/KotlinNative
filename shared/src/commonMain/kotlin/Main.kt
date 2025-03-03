import kotlinx.io.files.Path
import kotlinx.io.files.sink
import kotlinx.io.files.source

import kotlinx.io.files.SystemFileSystem

fun main() {
    val currentDir = Path(".") // 当前目录
    val files = SystemFileSystem.list(currentDir) // 列出目录内容

    files.forEach { println(it.name) } // 输出文件名
}

