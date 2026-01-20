package ro.pub.cs.systems.eim.colocviu2

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class ServerThread(private val port: Int) : Thread() {
    private var serverSocket: ServerSocket? = null
    private val dataCache = ConcurrentHashMap<String, String>()

    override fun run() {
        try {
            serverSocket = ServerSocket(port)
            while (!isInterrupted) {
                val socket = serverSocket?.accept()
                if (socket != null) CommunicationThread(socket).start()
                else android.util.Log.d("PracticalTest02", "socket is null!!")
            }
        } catch (e: Exception) { Log.e("Server", e.message ?: "Error") }
    }

    fun stopServer() {
        interrupt()
        serverSocket?.close()
    }

    inner class CommunicationThread(private val socket: Socket) : Thread() {
        override fun run() {
            try {
                val reader = socket.getInputStream().bufferedReader()
                val writer = socket.getOutputStream().bufferedWriter()

                val urlLink = reader.readLine()
                android.util.Log.d("PracticalTest02", urlLink + "LINK!!")

                var info = dataCache[urlLink]
                val server = org.apache.http.impl.client.DefaultHttpClient()

                if (info == null) {
                    val httpGet = org.apache.http.client.methods.HttpGet(urlLink)
                    val response = server.execute(httpGet).toString()
                    info = response
                    dataCache[urlLink] = info
                }
                android.util.Log.d("PracticalTest02", info)
                writer.write(info + "\n")
                writer.flush()
                socket.close()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}