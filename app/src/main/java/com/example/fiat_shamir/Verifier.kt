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
    private lateinit var x: BigInteger
    private lateinit var v: List<Boolean>

    var t = 0
    private var tmpT = 0

    private val protocol = ProtocolHandler()

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

        start.setOnClickListener {
            startVerification()
        }

    }

    private fun handleReadMsg(msg: String) {
        if ("n:" in msg) {
            if (!this::n.isInitialized) {
                nReceived(msg)
            } else {
                showToast("Prover tries to send another N. We won't allow it!")
            }
            return
        }

        if ("pubKey:" in msg) {
            pubKeyReceived(msg)
            return
        }

        if ("x: " in msg) {
            xReceived(msg)
            return
        }

        if ("y: " in msg) {
            yReceived(msg)
            return
        }

        showToast(msg)
    }

    private fun nReceived(msg: String) {
        val pattern: Pattern = Pattern.compile("n: (.*)")
        val matcher: Matcher = pattern.matcher(msg)
        if (matcher.find()) {
            val g = matcher.group(1)
            n = g.toBigInteger()
            Log.e(TAG, "Verifier sent n: $n")
        }
    }

    private fun pubKeyReceived(msg: String) {
        val tmp = mutableListOf<BigInteger>()
        val p = Pattern.compile("-?\\d+")
        val m = p.matcher(msg)
        while (m.find()) {
            val t = m.group()
            val tInt = t.toBigInteger()
            tmp.add(tInt)
        }
        pubKey = tmp
        showToast("N and PubKey are obtained.")
    }

    private fun xReceived(msg: String) {
        val pattern: Pattern = Pattern.compile("x: (.*)")
        val matcher: Matcher = pattern.matcher(msg)
        if (matcher.find()) {
            val g = matcher.group(1)
            x = g.toBigInteger()
            Log.e(TAG, "Verifier sent x: $x")
        }

        v = protocol.generateVector(pubKey.size)
        Log.e(TAG, "vector: $v")

        btService.write("vector: $v")
    }

    private fun yReceived(msg: String) {
        var y = BigInteger.ONE
        val pattern: Pattern = Pattern.compile("y: (.*)")
        val matcher: Matcher = pattern.matcher(msg)
        if (matcher.find()) {
            val g = matcher.group(1)
            y = g.toBigInteger()
        }

        val result = protocol.verify(x, y, pubKey, n, v)

        if (result) {
            if (tmpT <= t) {
                tmpT += 1
                protocolStatusVer.text = ("t: $tmpT")
                btService.write("Start verification")
            } else {
                protocolStatusVer.text = "Verification succeeded"
                btService.write("Verification succeeded")
            }
        } else {
            protocolStatusVer.text = "Verification failed"
        }
    }

    private fun startVerification() {
        t = protocol.calcT(n)
        btService.write("Start verification")
        protocolStatusVer.text = "t: $tmpT"
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}