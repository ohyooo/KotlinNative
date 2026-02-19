import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

object SharedUIBridge {
    fun makeViewController(): UIViewController = ComposeUIViewController {
        SharedApp()
    }
}
