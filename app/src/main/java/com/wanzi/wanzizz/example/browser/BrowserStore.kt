package com.wanzi.wanzizz.example.browser

import com.wanzi.wanzizz.state.Middleware
import com.wanzi.wanzizz.state.Store

class BrowserStore(
    initialState: BrowserState = BrowserState(),
    middleware: List<Middleware<BrowserState, BrowserAction>> = emptyList()
) : Store<BrowserState, BrowserAction>(
    initialState = initialState,
    reducer = BrowserStateReducer::reduce,
    middleware = middleware,
    threadNamePrefix = "BrowserStore",
) {
    init {
        dispatch(InitAction)
    }
}