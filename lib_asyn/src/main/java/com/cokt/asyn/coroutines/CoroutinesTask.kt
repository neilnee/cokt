package com.cokt.asyn.coroutines

import com.cokt.tool.CoktLog
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay

abstract class CoroutinesTask {

    fun execute() {
        CoktLog.debug("main thread is [${Thread.currentThread().id}]")
        async {
            CoktLog.debug("work thread is [${Thread.currentThread().id})]")
            delay(5000)
            runWorkThread()
        }
    }

    protected abstract suspend fun runWorkThread()

}