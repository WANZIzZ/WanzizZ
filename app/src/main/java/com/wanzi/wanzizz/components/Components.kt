package com.wanzi.wanzizz.components

import android.content.Context

class Components(private val context: Context) {

    val core by lazy { Core(context) }
}