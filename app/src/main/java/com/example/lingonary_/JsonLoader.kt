package com.example.lingonary_

import android.content.Context
import org.json.JSONArray
import java.io.IOException
fun loadTranscriptFromJson(context: Context, fileName: String): List<WordTimestamp> {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }
    val list = mutableListOf<WordTimestamp>()
    val jsonArray = JSONArray(jsonString)
    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
            WordTimestamp(
                id = obj.getInt("id"),
                word = obj.getString("w"),
                startTime = obj.getLong("s"),
                endTime = obj.getLong("e"),
                definition = obj.getString("d")
            )
        )
    }
    return list
}