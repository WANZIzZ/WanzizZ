package com.wanzi.wanzizz.state

import android.os.Handler
import android.os.Looper
import com.wanzi.wanzizz.state.internal.ReducerChainBuilder
import com.wanzi.wanzizz.state.internal.StoreThreadFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

open class Store<S : State, A : Action>(
    initialState: S,
    reducer: Reducer<S, A>,
    middleware: List<Middleware<S, A>> = emptyList(),
    threadNamePrefix: String? = null,
) {
    private val threadFactory = StoreThreadFactory(threadNamePrefix)
    private val dispatcher = Executors.newSingleThreadExecutor(threadFactory).asCoroutineDispatcher()
    private val reducerChainBuilder = ReducerChainBuilder(threadFactory, reducer, middleware)
    private val scope = CoroutineScope(dispatcher)

    internal val subscriptions = Collections.newSetFromMap(ConcurrentHashMap<Subscription<S, A>, Boolean>())
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Handler(Looper.getMainLooper()).postAtFrontOfQueue {
            throw StoreException("Exception while reducing state", throwable)
        }

        scope.cancel()
    }

    private val dispatcherWithExceptionHandler = dispatcher + exceptionHandler

    @Volatile
    private var currentState = initialState

    val state: S
        get() = currentState

    @Synchronized
    fun observeManually(observer: Observer<S>): Subscription<S, A> {
        val subscription = Subscription(observer, store = this)
        subscriptions.add(subscription)

        return subscription
    }

    fun dispatch(action: A) = scope.launch(dispatcherWithExceptionHandler) {
        synchronized(this@Store) {
            reducerChainBuilder.get(this@Store).invoke(action)
        }
    }

    internal fun transitionTo(state: S) {
        if (state == currentState) {
            return
        }

        currentState = state
        subscriptions.forEach { subscription -> subscription.dispatch(state) }
    }

    private fun removeSubscription(subscription: Subscription<S, A>) {
        subscriptions.remove(subscription)
    }

    class Subscription<S : State, A : Action> internal constructor(
        internal val observer: Observer<S>,
        store: Store<S, A>,
    ) {
        private val storeReference = WeakReference(store)
        internal var binding: Binding? = null
        private var active = false

        @Synchronized
        fun resume() {
            active = true

            storeReference.get()?.state?.let(observer)
        }

        @Synchronized
        fun pause() {
            active = false
        }

        @Synchronized
        internal fun dispatch(state: S) {
            if (active) {
                observer.invoke(state)
            }
        }

        @Synchronized
        fun unsubscribe() {
            active = false

            storeReference.get()?.removeSubscription(this)
            storeReference.clear()

            binding?.unbind()
        }

        interface Binding {
            fun unbind()
        }
    }
}

class StoreException(msg: String, val e: Throwable? = null) : Exception(msg, e)