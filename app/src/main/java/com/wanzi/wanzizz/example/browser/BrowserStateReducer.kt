package com.wanzi.wanzizz.example.browser

import android.util.Log

internal object BrowserStateReducer {
    fun reduce(state: BrowserState, action: BrowserAction): BrowserState {
        Log.d("Wanzi123", "BrowserStateReducer reduce action:$action")
        return when (action) {
            is InitAction -> state
            is TabListAction -> TabListReducer.reduce(state, action)
        }
    }
}

internal object TabListReducer {
    fun reduce(state: BrowserState, action: TabListAction): BrowserState {
        Log.d("Wanzi123", "TabListReducer reduce action:$action thread:${Thread.currentThread().name}")
        return when (action) {
            is TabListAction.AddTabAction -> {
                val updatedTabList = state.tabs + action.tab
                state.copy(
                    tabs = updatedTabList
                )
            }

            is TabListAction.SelectTabAction -> {
                state.copy(
                    selectedTabId = action.tabId
                )
            }
        }
    }
}