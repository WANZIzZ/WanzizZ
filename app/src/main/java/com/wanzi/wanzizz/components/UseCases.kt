package com.wanzi.wanzizz.components

import android.content.Context
import com.wanzi.wanzizz.example.browser.BrowserStore

class UseCases(
    private val context: Context,
    private val store: BrowserStore,
) {

    val tabsUseCases by lazy { TabsUseCases(store) }
}