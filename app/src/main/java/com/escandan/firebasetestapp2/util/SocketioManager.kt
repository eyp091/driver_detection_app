package com.escandan.firebasetestapp2.util

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketioManager(private val messageCallback: (String) -> Unit) {
    private lateinit var socket: Socket

    fun connectToServer() {
        try {
            socket = IO.socket("http://10.0.2.2:3000")
            socket.connect()

            socket.on(Socket.EVENT_CONNECT) {
                if (socket.connected()) {
                    Log.d("socket.io", "Connected to server.")
                }
                else {
                    Log.d("socket.io", "Connection failed.")
                }

                socket.emit("status", JSONObject().apply {
                    put("message", "Ready.")
                })

            }

            //sunucudan gelen mesajları dinle
            socket.on("status") {args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    val message = data.optString("message", "unknown message!")
                    Log.d("socket.io", "Message from the socket.io server: $message")
                    messageCallback(message) //callback ile mesaj gönderme
                }
                else {
                    Log.d("socket.io", "args is empty!")
                }
            }


            socket.on(Socket.EVENT_DISCONNECT) {
                socket.emit("status", JSONObject().apply {
                    put("message", "closed")
                })
                Log.d("socket.io","socket.io server disconnected!")
                messageCallback("disconnected")
            }

        }
        catch (e: URISyntaxException) {
            Log.d("socket.io", "Connection error: ${e.message}")
        }
    }

    fun disconnected() {
        socket.emit("status", JSONObject().apply {
            put("message", "closed")
        })

        socket.disconnect()
    }
}