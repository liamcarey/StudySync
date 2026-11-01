
package com.example.studysync

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Authentication models
data class AuthUser(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String
)

data class AuthResult(
    val token: String,
    val user: AuthUser
)

object StudySyncApi {

    // Android Emulator -> Mac localhost
    private const val BASE_URL = "http://127.0.0.1:3000"

    // Read HTTP response
    private fun readResponse(
        connection: HttpURLConnection
    ): String {

        val statusCode = connection.responseCode

        val stream = if (statusCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        return stream?.bufferedReader()?.use { reader ->
            reader.readText()
        } ?: ""
    }

    // Handle API errors
    private fun checkResponse(
        connection: HttpURLConnection,
        response: String,
        expectedStatus: Int? = null
    ) {
        val statusCode = connection.responseCode

        val success = if (expectedStatus != null) {
            statusCode == expectedStatus
        } else {
            statusCode in 200..299
        }

        if (!success) {
            val message = try {
                JSONObject(response).optString(
                    "error",
                    "Request failed"
                )
            } catch (e: Exception) {
                "HTTP error: $statusCode"
            }

            throw IllegalStateException(message)
        }
    }

    // Send a POST request containing JSON
    private fun post(
        endpoint: String,
        json: JSONObject
    ): Pair<Int, String> {

        val connection = URL("$BASE_URL$endpoint")
            .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )
            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            connection.outputStream.use { output ->
                output.write(
                    json.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val response = readResponse(connection)
            val statusCode = connection.responseCode

            checkResponse(connection, response)

            return Pair(statusCode, response)

        } finally {
            connection.disconnect()
        }
    }

    // =====================================
    // SIGN UP
    // =====================================

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {

        val json = JSONObject().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("email", email)
            put("password", password)
        }

        val (statusCode, _) = post(
            "/auth/signup",
            json
        )

        if (statusCode != 201) {
            throw IllegalStateException(
                "Account registration failed"
            )
        }
    }

    // =====================================
    // LOGIN
    // =====================================

    fun login(
        email: String,
        password: String
    ): AuthResult {

        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val (_, response) = post(
            "/auth/login",
            json
        )

        val result = JSONObject(response)
        val userJson = result.getJSONObject("user")

        val user = AuthUser(
            id = userJson.getInt("id"),
            firstName = userJson.getString("first_name"),
            lastName = userJson.getString("last_name"),
            email = userJson.getString("email")
        )

        return AuthResult(
            token = result.getString("token"),
            user = user
        )
    }

    // =====================================
    // GET STUDY GROUPS
    // =====================================


    fun getGroups(): List<StudyGroup> {

        val connection = URL("$BASE_URL/groups")
            .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val response = readResponse(connection)

            checkResponse(connection, response)

            val jsonArray = JSONArray(response)
            val groups = mutableListOf<StudyGroup>()

            for (i in 0 until jsonArray.length()) {

                val json = jsonArray.getJSONObject(i)

                val group = StudyGroup(
                    id = json.optInt("id"),
                    course = json.getString("course"),
                    title = json.getString("title"),
                    location = json.getString("location"),
                    time = json.getString("time"),
                    members = json.getInt("members"),
                    latitude = if (json.isNull("latitude")) null
                    else json.getDouble("latitude"),
                    longitude = if (json.isNull("longitude")) null
                    else json.getDouble("longitude")
                )

                groups.add(group)
            }

            return groups

        } finally {
            connection.disconnect()
        }
    }


    // =====================================
    // CREATE STUDY GROUP
    // =====================================

    fun createGroup(
        group: StudyGroup
    ): StudyGroup {

        val json = JSONObject().apply {
            put("course", group.course)
            put("title", group.title)
            put("location", group.location)
            put("time", group.time)
            put("latitude", group.latitude)
            put("longitude", group.longitude)
        }

        val (statusCode, response) = post(
            "/groups",
            json
        )

        if (statusCode != 201) {
            throw IllegalStateException(
                "Failed to create study group"
            )
        }

        val result = JSONObject(response)

        return StudyGroup(
            id = result.getInt("id"),
            course = result.getString("course"),
            title = result.getString("title"),
            location = result.getString("location"),
            time = result.getString("time"),
            members = result.getInt("members"),
            latitude = result.optDouble("latitude").takeUnless {
                it.isNaN()
            },
            longitude = result.optDouble("longitude").takeUnless {
                it.isNaN()
            }
        )
    }
}
