package rat.poison.utils

import com.sun.jna.Platform
import org.jire.kna.nativelib.windows.User32

fun keyState(virtualKeyCode: Int): Int = when {
	Platform.isWindows() || Platform.isWindowsCE() -> User32.GetKeyState(virtualKeyCode).toInt()
	else -> throw UnsupportedOperationException("Unsupported platform (osType=${Platform.getOSType()}")
}

fun keyPressed(virtualKeyCode: Int) = keyState(virtualKeyCode) < 0

fun keyReleased(virtualKeyCode: Int) = !keyPressed(virtualKeyCode)