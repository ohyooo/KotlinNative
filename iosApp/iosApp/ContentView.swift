import SwiftUI
import ui
import shared

private final class IOSSharedDataLoader: NSObject, SharedDataLoader {
    func loadStatus() -> Int32 {
        SharedBridge.shared.nativeGetStatus()
    }

    func loadContent() -> String {
        SharedBridge.shared.nativeGetContent()
    }
}

private struct SharedComposeViewController: UIViewControllerRepresentable {
    private let loader = IOSSharedDataLoader()

    func makeUIViewController(context: Context) -> UIViewController {
        SharedUIBridge.shared.makeViewController(loader: loader)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        SharedComposeViewController()
            .ignoresSafeArea()
    }
}
