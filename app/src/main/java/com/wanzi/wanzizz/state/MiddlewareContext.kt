package com.wanzi.wanzizz.state

typealias Middleware<S, A> = (context: MiddlewareContext<S, A>, next: (A) -> Unit, action: A) -> Unit

interface MiddlewareContext<S : State, A : Action> {

    val state: S

    fun dispatch(action: A)

    val store: Store<S, A>
}