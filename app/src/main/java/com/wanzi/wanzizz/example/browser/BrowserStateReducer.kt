package com.wanzi.wanzizz.example.browser

internal object BrowserStateReducer {
    fun reduce(state: BrowserState, action: BrowserAction): BrowserState {
        return when (action) {
            is InitAction -> state
            is TabListAction -> TabListReducer.reduce(state, action)
        }
    }
}

internal object TabListReducer {
    fun reduce(state: BrowserState, action: TabListAction): BrowserState {
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