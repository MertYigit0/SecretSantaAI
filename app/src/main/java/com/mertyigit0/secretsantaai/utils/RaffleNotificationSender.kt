package com.mertyigit0.secretsantaai.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.data.model.Group
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class RaffleNotificationSender : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    fun sendNotificationToUsers(groupId: String, groupName: String) {
        val db = FirebaseFirestore.getInstance()
        val groupRef = db.collection("groups").document(groupId)

        groupRef.get().addOnSuccessListener { document ->
            val group = document.toObject(Group::class.java)
            val creatorId = group?.createdBy
            val tokens = group?.users?.filter { it.userId != creatorId }
                ?.mapNotNull { it.fcmToken } ?: return@addOnSuccessListener

            tokens.forEach { token ->
                sendPushNotification(token, groupName)
            }
        }
    }

    private fun sendPushNotification(token: String, groupName: String) {
        Executors.newSingleThreadExecutor().execute {
            val accessToken = "" // OAuth 2.0 Token
            val url = URL("https://fcm.googleapis.com/v1/projects/YOUR_PROJECT_ID/messages:send")

            val message = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", token)
                    put("notification", JSONObject().apply {
                        put("title", "Çekiliş Sonucu")
                        put("body", "$groupName grubunun çekilişi tamamlandı!")
                    })
                })
            }

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            connection.outputStream.use { os ->
                os.write(message.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                println("Bildirim başarıyla gönderildi!")
            } else {
                println("Bildirim gönderme hatası: $responseCode")
            }
        }
    }

}
