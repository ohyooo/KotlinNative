import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SharedApp(
    loader: SharedDataLoader,
) {
    var buttonText by rememberSaveable { mutableStateOf("Load") }
    var status by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Status", fontSize = 14.sp)
            Text(
                text = status.ifBlank { "-" },
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Content", fontSize = 14.sp)
            Text(
                text = content.ifBlank { "-" },
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    scope.launch {
                        buttonText = "Loading"
                        withContext(Dispatchers.Default) {
                            status = ""
                            content = ""
                        }
                        val loadedStatus = withContext(Dispatchers.Default) {
                            loader.loadStatus().toString()
                        }
                        val loadedContent = withContext(Dispatchers.Default) {
                            loader.loadContent()
                        }
                        status = loadedStatus
                        content = loadedContent
                        buttonText = "Load"
                    }
                },
            ) {
                Text(text = buttonText)
            }
        }
    }
}

interface SharedDataLoader {
    fun loadStatus(): Int

    fun loadContent(): String
}
