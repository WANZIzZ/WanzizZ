package com.wanzi.wanzizz.components

import android.content.Context
import com.wanzi.wanzizz.example.browser.BrowserStore

class Core(private val context: Context) {

    val store by lazy { BrowserStore() }
}