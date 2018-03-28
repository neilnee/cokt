package com.cokt.activity

import android.os.Bundle
import com.cokt.R
import com.cokt.asyn.AbstractAsynTask
import com.cokt.asyn.AsynExecutor
import com.cokt.asyn.TaskResult
import com.cokt.tool.CoktLog
import kotlinx.android.synthetic.main.activity_asyn.*


class AsynActivity : BaseActivity() {
    companion object {
        private const val REQ_GET = 1
        private const val REQ_POST = 2

        private const val GITHUB_POST_URL = ""
        private const val GITHUB_GET_URL = "https://api.github.com/repos/neilnee/cokt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asyn)
        btn_req_get.setOnClickListener {
            AsynExecutor.defaultExecutor().execute(GithubTask(REQ_GET))
        }
    }

    private inner class GithubTask(val type: Int = REQ_GET) : AbstractAsynTask() {

        override fun runWorkThread() {
            when (type) {
                REQ_GET -> {
                    postResultMain(TaskResult(type).putResult(requestGet<CoktResp>(GITHUB_GET_URL)))
                }
                REQ_POST -> {

                }
            }
        }

        override fun handleResultMain(result: TaskResult) {
            when (result.type()) {
                REQ_GET -> {
                    CoktLog.debug("REQ_GET[${result.success()}]: ${result.data<CoktResp>()?.id} ${result.data<CoktResp>()?.name}")
                }
                REQ_POST -> {

                }
            }
        }

    }

    private data class CoktResp(
            var id: Int,
            var name: String)

}