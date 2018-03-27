package com.cokt.asyn

@Suppress("unused", "MemberVisibilityCanBePrivate")
class TaskEvent(var eventType: Int) {
    companion object {
        const val TASK_EVENT_TYPE_MIN = 500
        const val TASK_EVENT_TYPE_SUCCESS = TASK_EVENT_TYPE_MIN - 1
        const val TASK_EVENT_TYPE_FAIL = TASK_EVENT_TYPE_SUCCESS - 1
    }

    /** 业务自定义的事件代码  */
    var eventCode: Int = 0
    /** 业务自定义的事件信息  */
    var eventMsg: String? = null
}