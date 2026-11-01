package com.example.studysync

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object StudySyncApi {

    private const val BASE_URL = "http://10.0.2.2:3000"

    // Retrieve all study groups from PostgreSQL
    fun getGroups(): List<StudyGroup> {

        val connection = URL("$BASE_URL/groups")
            .openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode !in 200..299) {
                throw Exception(
                    "HTTP error: ${connection.responseCode}"
                )
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(response)
            val groups = mutableListOf<StudyGroup>()

            for (i in 0 until jsonArray.length()) {

                val json = jsonArray.getJSONObject(i)

                groups.add(
                    StudyGroup(
                        course = json.getString("course"),
                        title = json.getString("title"),
                        location = json.getString("location"),
                        time = json.getString("time"),
                        members = json.getInt("members")
                    )
                )
            }

            groups

        } finally {
            connection.disconnect()
        }
    }

    // Create a new study group in PostgreSQL
    fun createGroup(group: StudyGroup): StudyGroup {

        val connection = URL("$BASE_URL/groups")
            .openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )
            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            // Convert the study group into JSON
            val json = JSONObject().apply {
                put("course", group.course)
                put("title", group.title)
                put("location", group.location)
                put("time", group.time)
            }

            // Send the JSON to Express
            connection.outputStream.use {
                it.write(
                    json.toString().toByteArray(Charsets.UTF_8)
                )
            }

            // Check whether the group was created
            if (connection.responseCode != 201) {
                throw Exception(
                    "Failed to create group: HTTP ${connection.responseCode}"
                )
            }

            // Read the newly created group
            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val result = JSONObject(response)

            StudyGroup(
                course = result.getString("course"),
                title = result.getString("title"),
                location = result.getString("location"),
                time = result.getString("time"),
                members = result.getInt("members")
            )

        } finally {
            connection.disconnect()
        }
    }
}
