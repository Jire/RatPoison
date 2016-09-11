/*
 * Charlatan is a premium CS:GO cheat ran on the JVM.
 * Copyright (C) 2016 Thomas Nappo, Jonathan Beaudoin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.charlatano.scripts

import com.charlatano.*
import com.charlatano.game.CSGO.clientDLL
import com.charlatano.game.CSGO.engineDLL
import com.charlatano.game.ClientState
import com.charlatano.game.angle
import com.charlatano.game.entity.*
import com.charlatano.game.offsets.ClientOffsets.dwLocalPlayer
import com.charlatano.game.offsets.EngineOffsets.dwClientState
import com.charlatano.overlay.Overlay
import com.charlatano.utils.*
import org.jire.arrowhead.keyPressed
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom.current as tlr

private var target: Player = -1L

private const val SMOOTHING_MIN = 30F
private const val SMOOTHING_MAX = 22F

fun forceAim() = every(FORCE_AIM_SMOOTHING) {
	val pressed = keyPressed(FORCE_AIM_KEY) {
		val me: Player = clientDLL.uint(dwLocalPlayer)
		if (me <= 0x200 || me.dead()) return@keyPressed

		var currentTarget = target
		if (currentTarget == -1L) {
			currentTarget = me.target()
			if (currentTarget == -1L) return@keyPressed
			target = currentTarget
			return@keyPressed
		}

		if (target.dead()
				|| target.dormant()
				|| !target.spotted()
				|| target.team() == me.team()) {
			target = -1L
			return@keyPressed
		}

		val bonePosition = Vector(target.bone(0xC), target.bone(0x1C), target.bone(0x2C))
		/*if (tlr().nextInt(20) == 4) {
			var rand1 = tlr().nextFloat() * tlr().nextInt(20)
			var rand2 = tlr().nextFloat() * tlr().nextInt(20)
			var rand3 = tlr().nextFloat() * tlr().nextInt(10)
			if (tlr().nextBoolean()) rand1 = -rand1
			if (tlr().nextBoolean()) rand2 = -rand2
			if (tlr().nextBoolean()) rand3 = -rand3
			bonePosition.x += rand1
			bonePosition.y += rand2
			bonePosition.z += rand3
		}*/
		compensateVelocity(me, target, bonePosition, SMOOTHING_MAX)

		val dest = calculateAngle(me, bonePosition)

		val clientState: ClientState = engineDLL.uint(dwClientState)
		var currentAngle = clientState.angle() // This randomly gets weird

		currentAngle.normalize()

		dest.finalize(currentAngle, SMOOTHING_MAX)

		currentAngle = clientState.angle()
		val delta = Vector(currentAngle.y - dest.y, currentAngle.x - dest.x, 0F)

		val dx = Math.round(delta.x / (InGameSensitivity * InGamePitch))
		val dy = Math.round(-delta.y / (InGameSensitivity * InGameYaw))

		Overlay {
			val mx = SCREEN_SIZE.width / 2
			val my = SCREEN_SIZE.height / 2
			color = Color.RED
			fillOval(mx + dx - 5, my + dy - 5, 10, 10)
			drawOval(mx + dx - 6, my + dy - 6, 12, 12)
		}
		User32.mouse_event(User32.MOUSEEVENTF_MOVE, dx, dy, null, null)
	}
	if (!pressed) target = -1L
}