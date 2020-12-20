package rat.poison.scripts.visuals

import rat.poison.curSettings
import rat.poison.ui.uiUpdate
import rat.poison.utils.every
import rat.poison.utils.generalUtil.strToBool
import rat.poison.utils.keyPressed

fun espToggle() = every(50) {
    if (keyPressed(curSettings["VISUALS_TOGGLE_KEY"].toInt())) {
        curSettings["ENABLE_ESP"] = !curSettings["ENABLE_ESP"].strToBool()
        if (!curSettings["ENABLE_ESP"].strToBool()) {
            disableAllEsp()
        }

        Thread.sleep(100)

        uiUpdate()
    }
}