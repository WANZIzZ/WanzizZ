package com.wanzi.wanzizz

import android.app.Application
import com.wanzi.wanzizz.components.Components

class WanziApplication : Application() {

    val components by lazy { Components(this) }
}