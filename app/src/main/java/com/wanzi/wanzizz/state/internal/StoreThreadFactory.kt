package com.wanzi.wanzizz.state.internal

import com.wanzi.wanzizz.state.utils.NamedThreadFactory
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

internal class StoreThreadFactory(threadNamePrefix: String?) : ThreadFactory {

    @Volatile
    private var thread: Thread? = null

    private val actualFactory = if (threadNamePrefix != null) {
        NamedThreadFactory(threadNamePrefix)
    } else {
        Executors.defaultThreadFactory()
    }

    override fun newThread(r: Runnable?): Thread {
        return actualFactory.newThread(r).also {
            thread = it
        }
    }

    fun assertOnThread() {
        val currentThread = Thread.currentThread()
        val currentThreadId = currentThread.id
        val expectedThreadId = thread?.id

        if (currentThreadId == expectedThreadId) {
            return
        }

        throw IllegalThreadStateException(
            "Expected `store` thread, but running on thread `${currentThread.name}`. Leaked MiddlewareContext?",
        )
    }
}