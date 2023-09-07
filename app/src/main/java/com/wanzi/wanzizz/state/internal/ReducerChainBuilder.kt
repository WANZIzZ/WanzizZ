package com.wanzi.wanzizz.state.internal

import android.util.Log
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
        Log.d("Wanzi123", "ReducerChainBuilder get chain:$chain store:$store")
        chain?.let {
            return it
        }

        return build(store).also {
            chain = it
        }
    }

    private fun build(store: Store<S, A>): (A) -> Unit {
        Log.d("Wanzi123", "ReducerChainBuilder build")
        val context = object : MiddlewareContext<S, A> {
            override val state: S
                get() = store.state

            override fun dispatch(action: A) {
                Log.d("Wanzi123", "ReducerChainBuilder build MiddlewareContext dispatch")
                get(store).invoke(action)
            }

            override val store: Store<S, A>
                get() = store
        }

        var chain: (A) -> Unit = { action ->
            Log.d("Wanzi123", "ReducerChainBuilder block1")
            val state = reducer(store.state, action)
            store.transitionTo(state)
        }

        val threadCheck: Middleware<S, A> = { _, next, action ->
            Log.d("Wanzi123", "ReducerChainBuilder checkThread And next")
            storeThreadFactory.assertOnThread()
            next(action)
        }

//        (middleware.reversed() + threadCheck).forEach { middleware ->
//            Log.d("Wanzi123", "ReducerChainBuilder forEach")
//            val next = chain
//            chain = { action -> middleware(context, next, action) }
//        }

        return chain
    }
}