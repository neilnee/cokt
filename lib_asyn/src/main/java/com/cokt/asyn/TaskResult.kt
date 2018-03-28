package com.cokt.asyn

@Suppress("unused", "MemberVisibilityCanBePrivate")
class TaskResult(private var type: Int, private var code: Int = TASK_CODE_SUCCESS) {
    companion object {
        const val TASK_CODE_MIN = 500
        const val TASK_CODE_SUCCESS = TASK_CODE_MIN - 1
        const val TASK_CODE_FAIL = TASK_CODE_SUCCESS - 1
    }

    @PublishedApi
    internal var result: Any? = null

    fun type(): Int = type

    fun success(): Boolean = code == TASK_CODE_SUCCESS

    fun putResult(r: Any?): TaskResult {
        result = r
        return this
    }

    inline fun <reified T> data(): T? {
        return if (result is T) {
            result as T
        } else {
            null
        }
    }

}