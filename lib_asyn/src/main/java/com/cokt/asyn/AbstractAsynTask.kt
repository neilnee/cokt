package com.cokt.asyn

import android.text.TextUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URLEncoder


abstract class AbstractAsynTask : Runnable, Comparable<AbstractAsynTask> {
    companion object {
        @PublishedApi
        internal val JSON_TYPE = MediaType.parse("application/json; charset=utf-8")
        @PublishedApi
        internal val URLENCODED_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
    }

    private var delivery: DeliveryMain? = null
    private var httpClient: OkHttpClient? = null
    private var httpCode: Int = 0

    override fun run() {
        runWorkThread()
    }

    override fun compareTo(other: AbstractAsynTask): Int {
        return 0
    }

    fun setDelivery(delivery: DeliveryMain) {
        this.delivery = delivery
    }

    fun onEvent(event: TaskEvent) {
        handleEventMainThread(event)
    }

    protected abstract fun runWorkThread()

    protected abstract fun handleEventMainThread(event: TaskEvent)

    protected fun postEventMainThread(event: TaskEvent) {
        delivery?.postEvent(this, event)
    }

    protected fun <T> sendPost(url: String, req: T?) {
        doRequestPost(url, RequestBody.create(JSON_TYPE, Json.toJson(req)))
    }

    protected inline fun <reified T, K> requestPost(url: String, req: K?): T? {
        return Json.toObject<T>(doRequestPost(url, RequestBody.create(JSON_TYPE, Json.toJson(req))))
    }

    protected inline fun <reified T> requestPost(url: String, params: Map<String, String>): T? {
        return Json.toObject<T>(doRequestPost(url, RequestBody.create(URLENCODED_TYPE, buildQuery(params,"utf-8"))))
    }

    protected inline fun <reified T> requestGet(url: String): T? {
        return Json.toObject<T>(doRequestGet(url))
    }

    protected fun success(): Boolean {
        return httpCode == HttpURLConnection.HTTP_OK
    }

    @PublishedApi
    internal fun doRequestGet(url: String): String {
        if (url.isEmpty()) return ""
        checkHttpClient()
        val builder = Request.Builder()
                .url(url)
                .get()
        try {
            val response = httpClient?.newCall(builder.build())?.execute()
            httpCode = response?.code() ?: HttpURLConnection.HTTP_BAD_REQUEST
            val retStr = response?.body()?.string() ?: ""
            if (response != null && response.isSuccessful) {
                return retStr
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    @PublishedApi
    internal fun doRequestPost(url: String, req: RequestBody): String {
        if (url.isEmpty()) return ""
        checkHttpClient()
        val builder = Request.Builder()
                .url(url)
                .post(req)
        try {
            val response = httpClient?.newCall(builder.build())?.execute()
            httpCode = response?.code() ?: HttpURLConnection.HTTP_BAD_REQUEST
            val retStr = response?.body()?.string() ?: ""
            if (response != null && response.isSuccessful) {
                return retStr
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    @PublishedApi
    internal fun buildQuery(params: Map<String, String>?, charset: String?): String {
        if (params == null || charset == null) {
            return ""
        }
        val query = StringBuilder()
        val entries = params.entries
        var hasParam = false
        for ((name, value) in entries) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value) ||
                    name.trim { it <= ' ' }.isEmpty() || value.trim { it <= ' ' }.isEmpty()) {
                continue
            }
            if (hasParam) {
                query.append("&")
            } else {
                hasParam = true
            }
            query.append(name).append("=").append(URLEncoder.encode(value, charset))
        }
        return query.toString()
    }

    private fun checkHttpClient() {
        if (httpClient == null) {
            synchronized(OkHttpClient::class) {
                if (httpClient == null) {
                    httpClient = OkHttpClient()
                }
            }
        }
    }

}