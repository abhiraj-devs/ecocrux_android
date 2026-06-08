package com.example.ecocrux.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class TimelineItemData(
    val type: String, // "start", "charge", "food", "arrive"
    val title: String,
    val subtitle: String?,
    val time: String
)

object GeminiService {
    private val API_KEY = com.example.ecocrux.BuildConfig.GEMINI_API_KEY

    suspend fun planTrip(from: String, to: String, vehicle: String): List<TimelineItemData> = withContext(Dispatchers.IO) {
        if (API_KEY == "YOUR_GEMINI_API_KEY_HERE" || API_KEY.isBlank()) {
            return@withContext listOf(
                TimelineItemData("start", "Start — $from (100% battery)", null, "8:00 AM"),
                TimelineItemData("charge", "API Key Missing", "Please add Gemini API Key in local.properties", "9:00 AM"),
                TimelineItemData("arrive", "Arrive — $to", null, "12:00 PM")
            )
        }

        try {
            val prompt = """
                Plan an EV road trip in India from $from to $to using a $vehicle.
                Assume departure at 8:00 AM with 100% battery.
                Provide a realistic timeline including charging stops, food breaks, and arrival.
                
                Format the response STRICTLY as a list of items, one per line.
                Use the following format for each line:
                [TYPE] | [TITLE] | [SUBTITLE] | [TIME]
                
                Rules:
                - [TYPE] must be one of: start, charge, food, arrive
                - [TITLE] must be short and clear
                - [SUBTITLE] can be empty (just use " ") or contain details like "45 min · +40% · ₹95"
                - [TIME] must be in AM/PM format
                
                Example output:
                start | Start — Kochi (100% battery) |   | 8:00 AM
                charge | Charge — Zeon Charging, Coimbatore | 45 min · +50% · ₹400 | 11:30 AM
                food | Lunch — A2B Restaurant | Rated 4.2 · ₹₹ | 1:00 PM
                arrive | Arrive — Bangalore (20% battery) |   | 6:00 PM
            """.trimIndent()

            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = JSONObject().apply {
                put("contents", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(jsonBody.toString()) }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = InputStreamReader(connection.inputStream).readText()
                val responseObj = JSONObject(response)
                val candidates = responseObj.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")

                if (text != null) {
                    return@withContext text.lines()
                        .filter { it.contains("|") }
                        .map { line ->
                            val parts = line.split("|").map { it.trim() }
                            if (parts.size >= 4) {
                                TimelineItemData(
                                    type = parts[0].lowercase(),
                                    title = parts[1],
                                    subtitle = parts[2].takeIf { it.isNotBlank() },
                                    time = parts[3]
                                )
                            } else null
                        }
                        .filterNotNull()
                }
            }
            
            return@withContext emptyList()
        } catch (e: Throwable) {
            e.printStackTrace()
            listOf(
                TimelineItemData("start", "Start — $from", null, "8:00 AM"),
                TimelineItemData("charge", "Error generating plan", e.message, "---"),
                TimelineItemData("arrive", "Arrive — $to", null, "---")
            )
        }
    }
}
