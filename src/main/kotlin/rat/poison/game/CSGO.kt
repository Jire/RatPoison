package rat.poison.game

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import org.jire.kna.attach.Attach
import org.jire.kna.attach.AttachedModule
import org.jire.kna.attach.AttachedProcess
import org.jire.kna.attach.windows.WindowsAttachAccess
import rat.poison.dbg
import rat.poison.game.hooks.constructEntities
import rat.poison.game.hooks.updateCursorEnable
import rat.poison.game.netvars.NetVars
import rat.poison.settings.CLIENT_MODULE_NAME
import rat.poison.settings.ENGINE_MODULE_NAME
import rat.poison.settings.PROCESS_ACCESS_FLAGS
import rat.poison.settings.PROCESS_NAME
import rat.poison.utils.every
import rat.poison.utils.inBackground
import rat.poison.utils.natives.CUser32
import rat.poison.utils.retry
import kotlin.system.exitProcess

object CSGO {
	const val ENTITY_SIZE = 16
	const val GLOW_OBJECT_SIZE = 56
	
	lateinit var csgoEXE: AttachedProcess
		private set
	
	lateinit var clientDLL: AttachedModule
		private set
	lateinit var engineDLL: AttachedModule
		private set
	
	var gameHeight: Int = 0
		private set
	
	var gameX: Int = 0
		private set
	
	var gameWidth: Int = 0
		private set
	
	var gameY: Int = 0
		private set
	
	fun initialize() {
		if (dbg) println("[DEBUG] Searching for and initializing CSGO")
		
		retry(128) {
			csgoEXE = Attach.byName(PROCESS_NAME, WindowsAttachAccess(PROCESS_ACCESS_FLAGS))!!
		}
		
		retry(128) {
			val modules = csgoEXE.modules()
			engineDLL = modules.byName(ENGINE_MODULE_NAME)!!
			clientDLL = modules.byName(CLIENT_MODULE_NAME)!!
		}
		
		val rect = WinDef.RECT()
		val hwd = CUser32.FindWindowA(null, "Counter-Strike: Global Offensive")
		
		//Get initially
		if (!CUser32.GetClientRect(hwd, rect)) exitProcess(2)
		gameWidth = rect.right - rect.left
		gameHeight = rect.bottom - rect.top
		
		if (!CUser32.GetWindowRect(hwd, rect)) exitProcess(3)
		gameX = rect.left + (((rect.right - rect.left) - gameWidth) / 2)
		gameY = rect.top + ((rect.bottom - rect.top) - gameHeight)
		
		every(1000) {
			if (!CUser32.GetClientRect(hwd, rect)) exitProcess(2)
			gameWidth = rect.right - rect.left
			gameHeight = rect.bottom - rect.top
			
			if (!CUser32.GetWindowRect(hwd, rect)) exitProcess(3)
			gameX = rect.left + (((rect.right - rect.left) - gameWidth) / 2)
			gameY = rect.top + ((rect.bottom - rect.top) - gameHeight)
		}
		
		every(1000, continuous = true) {
			inBackground = Pointer.nativeValue(hwd.pointer) != CUser32.GetForegroundWindow()
		}
		
		NetVars.load()
		
		constructEntities()
		updateCursorEnable()
		
		if (dbg) println("[DEBUG] CSGO initialized")
	}
}