package com.cokt.asyn

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor


class DeliveryMain {
    private var poster: Executor? = null

    init {
        val handler = Handler(Looper.getMainLooper())
        poster = Executor { command -> handler.post(command) }
    }

    fun postEvent(task: AbstractAsynTask, event: TaskEvent) {
        poster?.execute(InnerEventRunnable(task, event))
    }

    private inner class InnerEventRunnable internal constructor(
            private val task: AbstractAsynTask, private val event: TaskEvent) : Runnable {

        override fun run() {
            task.onEvent(event)
        }
    }

}