package com.cokt.asyn.coroutines

import com.cokt.tool.CoktLog
import kotlinx.coroutines.experimental.runBlocking

abstract class CoroutinesTask {

    fun execute() = runBlocking {
        CoktLog.debug("start execute in [${Thread.currentThread().id}]")
        CoktLog.debug("end execute in [${Thread.currentThread().id}]")
    }

    protected abstract suspend fun runWorkThread(): Int

}