package com.example.fiat_shamir

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_verifier.*
import java.math.BigInteger
import java.util.regex.Matcher
import java.util.regex.Pattern


class Verifier : AppCompatActivity() {

    private lateinit var btService: MyBluetoothService
    private lateinit var n: BigInteger
    private lateinit var pubKey: List<BigInteger>

    private val mHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            MESSAGE_WRITE -> {
                Log.e(TAG, "msg sent")
            }
            MESSAGE_READ -> {
                val readBuf = msg.obj as ByteArray
                val readMessage = String(readBuf, 0, msg.arg1)
                handleReadMsg(readMessage)
            }

            MESSAGE_CONNECTION -> {
                connectionStatusVer.text = msg.obj.toString()

            }
        }
        false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifier)

        btService = MyBluetoothService(mHandler)
        val socket = MyBluetoothService.socketBT
        btService.manageMyConnectedSocket(socket)
        btService.write("Verifier is connected")

    }

    private fun handleReadMsg(msg: String) {
        if ("n:" in msg) {
            if (!this::n.isInitialized) {
                nReceived(msg)
            } else {
                showToast("Prover tries to send another N. We won't allow it!")
            }
        }

        if ("pubKey:" in msg) {
            pubKeyReceived(msg)
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun nReceived(msg: String) {
        val pattern: Pattern = Pattern.compile("n: (.*)")
        val matcher: Matcher = pattern.matcher(msg)
        if (matcher.find()) {
            var g = matcher.group(1)
            n = g.toBigInteger()
            Log.e(TAG, "Verifier sent n: $n")
        }
    }

    private fun pubKeyReceived(msg: String) {
        val tmp = mutableListOf<BigInteger>()
        val p = Pattern.compile("-?\\d+")
        val m = p.matcher(msg)
        while (m.find()) {
            var t = m.group()
            var t_int = t.toBigInteger()
            tmp.add(t_int)
        }
        pubKey = tmp
        showToast("N and PubKey are obtained.")
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}