package com.wanzi.wanzizz.state.ext

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

    return channelFlow {
        if (destroyed) {
            return@channelFlow
        }

        owner?.lifecycle?.removeObserver(ownerDestroyedObserver)

        val subscription = observeManually { state ->
            runBlocking {
                try {
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
            subscription.unsubscribe()
        }
    }.buffer(Channel.CONFLATED)
}

private class SubscriptionLifecycleBinding<S : State, A : Action>(
    private val owner: LifecycleOwner,
    private val subscription: Store.Subscription<S, A>,
) : DefaultLifecycleObserver, Store.Subscription.Binding {
    override fun onStart(owner: LifecycleOwner) {
        subscription.resume()
    }

    override fun onStop(owner: LifecycleOwner) {
        subscription.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscription.unsubscribe()
    }

    override fun unbind() {
        owner.lifecycle.removeObserver(this)
    }
}