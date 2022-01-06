/******************************************************************************
 * Copyright (C) 2022 by nekohasekai <contact-git@sekai.icu>                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                       *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                            *
 ******************************************************************************/

package io.nekohasekai.sagernet.utils

import io.nekohasekai.sagernet.ktx.app
import java.io.File

object RootCAList {

    fun extract() {

        val sha256Sum = app.assets.open("root_ca_certs/sha256sum").use {
            it.bufferedReader().readText()
        }
        val sha256SumFile = File(app.filesDir, "root_ca_certs.pem.sha256sum")
        if (sha256SumFile.exists() && sha256SumFile.readText() == sha256Sum) {
            return
        }
        val certFileList = app.assets.list("root_ca_certs") as Array<String>?
        if (certFileList.isNullOrEmpty()) {
            error("empty root_ca_certs dir")
        }
        val certList = mutableListOf<String>()
        for (certFile in certFileList) {
            app.assets.open("root_ca_certs/$certFile").use {
                certList.add(it.bufferedReader().readText())
            }
        }
        File(app.filesDir, "root_ca_certs.pem").writeText(certList.joinToString("\n"))
        File(app.filesDir, "root_ca_certs.pem.sha256sum").writeText(sha256Sum)

    }

}