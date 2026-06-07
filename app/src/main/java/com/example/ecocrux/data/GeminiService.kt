package com.example.ecocrux.data

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class TimelineItemData(
    val type: String, // "start", "charge", "food", "arrive"
    val title: String,
    val subtitle: String?,
    val time: String
)

object GeminiService {
    // IMPORTANT: Replace with your actual Gemini API key from Google AI Studio
    private const val API_KEY = "YOUR_GEMINI_API_KEY_HERE"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    suspend fun planTrip(from: String, to: String, vehicle: String): List<TimelineItemData> = withContext(Dispatchers.IO) {
        if (API_KEY == "YOUR_GEMINI_API_KEY_HERE" || API_KEY.isBlank()) {
            return@withContext listOf(
                TimelineItemData("start", "Start — $from (100% battery)", null, "8:00 AM"),
                TimelineItemData("charge", "API Key Missing", "Please add Gemini API Key in GeminiService.kt", "9:00 AM"),
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

            val response = generativeModel.generateContent(prompt)
            val text = response.text ?: return@withContext emptyList()

            text.lines()
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
        } catch (e: Exception) {
            e.printStackTrace()
            listOf(
                TimelineItemData("start", "Start — $from", null, "8:00 AM"),
                TimelineItemData("charge", "Error generating plan", e.message, "---"),
                TimelineItemData("arrive", "Arrive — $to", null, "---")
            )
        }
    }
}
