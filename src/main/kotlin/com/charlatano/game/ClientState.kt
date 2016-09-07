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

package com.charlatano.game

import com.charlatano.game.CSGO.csgoEXE
import com.charlatano.game.offsets.EngineOffsets.dwViewAngles
import com.charlatano.utils.Angle
import com.charlatano.utils.Vector
import org.jire.arrowhead.get

typealias ClientState = Long

internal fun ClientState.angle(): Angle = Vector<Float>(csgoEXE[this + dwViewAngles], csgoEXE[this + dwViewAngles + 4], csgoEXE[this + dwViewAngles + 8])