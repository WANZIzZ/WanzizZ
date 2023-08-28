package com.wanzi.wanzizz.components

import com.wanzi.wanzizz.example.browser.BrowserStore
import com.wanzi.wanzizz.example.browser.TabListAction

class TabsUseCases(
    store: BrowserStore
) {

    class SelectTabUseCase(
        private val store: BrowserStore
    ) {
        operator fun invoke(tabId: String) {
            store.dispatch(TabListAction.SelectTabAction(tabId))
        }
    }

    class AddTabUseCase(
        private val store: BrowserStore
    ) {
        operator fun invoke(tab: String) {
            store.dispatch(TabListAction.AddTabAction(tab))
        }
    }

    val selectTab by lazy { SelectTabUseCase(store) }
    val addTab by lazy { AddTabUseCase(store) }
}