package com.wanzi.wanzizz.example.browser

import com.wanzi.wanzizz.state.State

data class BrowserState(
    val tabs: List<String> = emptyList(),
    val selectedTabId: String? = null
) : State
