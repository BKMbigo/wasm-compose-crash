import androidx.compose.material3.Text
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        var bar: Int? = remember { null }

        Text("Good Morning")
    }
}

class Bar
