package com.wanzi.wanzizz.state.utils

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(private val prefix: String) : ThreadFactory {

    private val backingFactory = Executors.defaultThreadFactory()
    private val threadNumber = AtomicInteger(1)

    override fun newThread(r: Runnable?): Thread {
        return backingFactory.newThread(r).apply {
            val threadNumber = threadNumber.getAndIncrement()
            name = "$prefix-thread-$threadNumber"
        }
    }
}