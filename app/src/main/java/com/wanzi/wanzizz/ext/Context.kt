package com.wanzi.wanzizz.ext

import android.content.Context
import com.wanzi.wanzizz.WanziApplication
import com.wanzi.wanzizz.components.Components

val Context.application: WanziApplication
    get() = applicationContext as WanziApplication

val Context.components: Components
    get() = application.components