@file:Suppress(
    "INVISIBLE_MEMBER",
    "INVISIBLE_REFERENCE",
    "EXPOSED_PARAMETER_TYPE",
)

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.ComposeWindow
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.w3c.dom.HTMLCanvasElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        var job: Job? = remember { null }
        Text("Good Morning")
    }
}

fun CanvasBasedWindow(
    title: String = "JetpackNativeWindow",
    canvasElementId: String = "defaultCanvasElementId",
    requestResize: (suspend () -> IntSize)? = null,
    content: @Composable () -> Unit = { }
) {

    val resize: suspend () -> IntSize = if (requestResize != null) {
        requestResize
    } else {
        val channel = Channel<IntSize>(capacity = Channel.CONFLATED)

        window.addEventListener("resize", { _ ->
            val w = document.documentElement?.clientWidth ?: 0
            val h = document.documentElement?.clientHeight ?: 0
            channel.trySend(IntSize(w, h))
        })

        suspend {
            channel.receive()
        }
    }

    if (requestResize == null) {
        (document.getElementById(canvasElementId) as? HTMLCanvasElement)?.let {
            it.width = document.documentElement?.clientWidth ?: 0
            it.height = document.documentElement?.clientHeight ?: 0
        }
    }

    try {
        ComposeWindow(canvasId = canvasElementId).apply {
            val composeWindow = this
            setContent {
                content()
                LaunchedEffect(Unit) {
                    while (isActive) {
                        val newSize = resize()
                        composeWindow.resize(newSize)
                        delay(100) // throttle
                    }
                }
            }
        }
    } catch (e: Exception) {
        print("Exception encountered is $e and ${e.suppressedExceptions.size}")
    }
}
