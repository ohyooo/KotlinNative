import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

object SharedUIBridge {
    fun makeViewController(loader: SharedDataLoader): UIViewController = ComposeUIViewController {
        SharedApp(loader)
    }
}
