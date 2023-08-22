package com.wanzi.wanzizz.state.internal

import com.wanzi.wanzizz.state.Action
import com.wanzi.wanzizz.state.Middleware
import com.wanzi.wanzizz.state.MiddlewareContext
import com.wanzi.wanzizz.state.Reducer
import com.wanzi.wanzizz.state.State
import com.wanzi.wanzizz.state.Store

internal class ReducerChainBuilder<S : State, A : Action>(
    private val storeThreadFactory: StoreThreadFactory,
    private val reducer: Reducer<S, A>,
    private val middleware: List<Middleware<S, A>>,
) {
    private var chain: ((A) -> Unit)? = null

    fun get(store: Store<S, A>): (A) -> Unit {
        chain?.let { return it }

        return build(store).also {
            chain = it
        }
    }

    private fun build(store: Store<S, A>): (A) -> Unit {
        val context = object : MiddlewareContext<S, A> {
            override val state: S
                get() = store.state

            override fun dispatch(action: A) {
                get(store).invoke(action)
            }

            override val store: Store<S, A>
                get() = store
        }

        var chain: (A) -> Unit = { action ->
            val state = reducer(store.state, action)
            store.transitionTo(state)
        }

        val threadCheck: Middleware<S, A> = { _, next, action ->
            storeThreadFactory.assertOnThread()
            next(action)
        }

        (middleware.reversed() + threadCheck).forEach { middleware ->
            val next = chain
            chain = { action -> middleware(context, next, action) }
        }

        return chain
    }
}