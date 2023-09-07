package com.wanzi.wanzizz.state.ext

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.wanzi.wanzizz.state.Action
import com.wanzi.wanzizz.state.State
import com.wanzi.wanzizz.state.Store
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@MainThread
fun <S : State, A : Action> Store<S, A>.flowScoped(
    owner: LifecycleOwner? = null,
    block: suspend (Flow<S>) -> Unit,
): CoroutineScope {
    return MainScope().apply {
        launch {
            block(flow(owner))
        }
    }
}

fun <S : State, A : Action> Store<S, A>.flow(owner: LifecycleOwner? = null): Flow<S> {
    var destroyed = owner?.lifecycle?.currentState == Lifecycle.State.DESTROYED
    val ownerDestroyedObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            destroyed = true
        }
    }
    owner?.lifecycle?.addObserver(ownerDestroyedObserver)
    Log.d("Wanzi123", "StoreExtensionsKt flow destroyed:$destroyed")
    return channelFlow {
        Log.d("Wanzi123", "StoreExtensionsKt flow channelFlow destroyed:$destroyed")
        if (destroyed) {
            return@channelFlow
        }

        owner?.lifecycle?.removeObserver(ownerDestroyedObserver)

        val subscription = observeManually { state ->
            runBlocking {
                try {
                    Log.d("Wanzi123", "StoreExtensionsKt flow observeManually send:$state")
                    send(state)
                } catch (e: CancellationException) {
                }
            }
        }

        if (owner == null) {
            subscription.resume()
        } else {
            subscription.binding = SubscriptionLifecycleBinding(owner, subscription).apply {
                owner.lifecycle.addObserver(this)
            }
        }

        awaitClose {
            Log.d("Wanzi123", "StoreExtensionsKt flow awaitClose")
            subscription.unsubscribe()
        }
    }.buffer(Channel.CONFLATED)
}

private class SubscriptionLifecycleBinding<S : State, A : Action>(
    private val owner: LifecycleOwner,
    private val subscription: Store.Subscription<S, A>,
) : DefaultLifecycleObserver, Store.Subscription.Binding {
    override fun onStart(owner: LifecycleOwner) {
        Log.d("Wanzi123", "SubscriptionLifecycleBinding onStart")
        subscription.resume()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("Wanzi123", "SubscriptionLifecycleBinding onStop")
        subscription.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("Wanzi123", "SubscriptionLifecycleBinding onDestroy")
        subscription.unsubscribe()
    }

    override fun unbind() {
        Log.d("Wanzi123", "SubscriptionLifecycleBinding unbind")
        owner.lifecycle.removeObserver(this)
    }
}