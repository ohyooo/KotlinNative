import SwiftUI
import shared

private struct SharedComposeViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SharedUIBridge.shared.makeViewController()
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
