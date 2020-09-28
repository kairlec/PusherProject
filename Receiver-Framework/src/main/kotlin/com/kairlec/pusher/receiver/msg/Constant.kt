package com.kairlec.pusher.receiver.msg

import org.w3c.dom.Element

operator fun Element?.get(name: String): String? {
    return this?.getElementsByTagName(name)?.item(0)?.textContent
}

