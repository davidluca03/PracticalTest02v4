package ro.pub.cs.systems.eim.colocviu2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest02v4 : AppCompatActivity() {
    var serverThread: ServerThread? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v4_main)

        val urlText = findViewById<EditText>(R.id.url_text)
        val portText = findViewById<EditText>(R.id.port_text)
        val reqButton = findViewById<Button>(R.id.request_button)
        val btnConnect = findViewById<Button>(R.id.start_button)
        val resultTv = findViewById<TextView>(R.id.result)

        btnConnect.setOnClickListener {
            val port = portText.text.toString().toInt()
            serverThread = ServerThread(port)
            serverThread?.start()
            Toast.makeText(this, "Server started on $port", Toast.LENGTH_SHORT).show()
        }

        reqButton.setOnClickListener {
            val urlLink = urlText.text.toString()

            Thread {
                try {
                    val socket = java.net.Socket("localhost", portText.text.toString().toInt())
                    val writer = socket.getOutputStream().bufferedWriter()
                    writer.write("$urlLink\n")
                    writer.flush()

                    val response = socket.getInputStream().bufferedReader().readLine()
                    runOnUiThread { resultTv.text = response }
                    socket.close()
                } catch (e: Exception) { e.printStackTrace() }
            }.start()
        }
    }

    override fun onDestroy() {
        serverThread?.stopServer()
        super.onDestroy()
    }
}