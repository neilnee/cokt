package com.cokt.tool

import com.google.gson.Gson
import com.google.gson.JsonObject

object Json {

    inline fun <reified T> toObject(jsonStr: String?): T? {
        return try {
            Gson().fromJson(jsonStr, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    inline fun <reified T> toObject(jsonObj: JsonObject?): T? {
        return try {
            Gson().fromJson(jsonObj, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun toJson(obj: Any?): String {
        return try {
            Gson().toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}