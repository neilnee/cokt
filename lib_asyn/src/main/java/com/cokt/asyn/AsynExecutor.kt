package com.cokt.asyn

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class AsynExecutor private constructor(corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long) {

    companion object {
        private const val DEFAULT_POOL_SIZE_CORE = 10
        private const val DEFAULT_POOL_SIZE_MAX = 20
        private const val DEFAULT_POOL_KEEP_ALIVE = 2L
        @Volatile
        private var DEFAULT_EXECUTOR: AsynExecutor? = null

        fun defaultExecutor(): AsynExecutor {
            if (DEFAULT_EXECUTOR == null) {
                synchronized(AsynExecutor::class) {
                    if (DEFAULT_EXECUTOR == null) {
                        DEFAULT_EXECUTOR = AsynExecutor(DEFAULT_POOL_SIZE_CORE, DEFAULT_POOL_SIZE_MAX, DEFAULT_POOL_KEEP_ALIVE)
                    }
                }
            }
            return DEFAULT_EXECUTOR!!
        }

        fun shutdownAll() {
            synchronized(AsynExecutor::class) {
                if (DEFAULT_EXECUTOR != null) {
                    DEFAULT_EXECUTOR?.shutdown()
                    DEFAULT_EXECUTOR = null
                }
            }
        }
    }

    private val waitingQueue = PriorityBlockingQueue<Runnable>()
    private val workingQueue = ArrayList<AbstractAsynTask>()
    private var threadPool: InnerThreadPool? = null
    private val deliveryMain = DeliveryMain()

    init {
        threadPool = InnerThreadPool(corePoolSize, maximumPoolSize, keepAliveTime)
    }

    fun execute(task: AbstractAsynTask) {
        task.setDelivery(deliveryMain)
        threadPool?.execute(task)
    }
    
    fun cancle(task: AbstractAsynTask) {
        waitingQueue.remove(task)
    }

    fun shutdown() {
        if (!threadPool?.isShutdown!!) {
            threadPool?.shutdown()
        }
    }

    private inner class InnerThreadPool internal constructor(
            corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long) :
            ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, waitingQueue) {

        override fun beforeExecute(t: Thread, r: Runnable) {
            synchronized(workingQueue) {
                if (r is AbstractAsynTask) {
                    workingQueue.add(r)
                }
            }
        }

        override fun afterExecute(r: Runnable, t: Throwable?) {
            synchronized(workingQueue) {
                if (r is AbstractAsynTask) {
                    workingQueue.remove(r)
                }
            }
        }

    }

}