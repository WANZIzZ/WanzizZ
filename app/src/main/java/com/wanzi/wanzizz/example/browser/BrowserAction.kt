package com.wanzi.wanzizz.example.browser

import com.wanzi.wanzizz.state.Action

sealed class BrowserAction : Action

object InitAction : BrowserAction()

sealed class TabListAction : BrowserAction() {
    data class AddTabAction(val tab: String) : TabListAction()
    data class SelectTabAction(val tabId: String) : TabListAction()
}