package com.example.fiat_shamir

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prover.*
import java.math.BigInteger
import java.util.regex.Pattern

class Prover : AppCompatActivity() {
    private lateinit var btService: MyBluetoothService
    private lateinit var pubKey: List<BigInteger>
    private lateinit var privKey: List<BigInteger>

    private val protocol = ProtocolHandler()
    var n = BigInteger("0")

    private val mHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            MESSAGE_WRITE -> {
                Log.e(TAG, "msg sent")
            }
            MESSAGE_READ -> {
                val readBuf = msg.obj as ByteArray
                val readMessage = String(readBuf, 0, msg.arg1)
                handleMessage(readMessage)
            }

            MESSAGE_CONNECTION -> {
                connectionStatusAuth.text = msg.obj.toString()
            }
        }
        false
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prover)

        btService = MyBluetoothService(mHandler)

        val device = getDevice()

        Log.e(TAG, device.name)
        btService.connectToDevice(device)



        generateKeys.setOnClickListener {
            n = protocol.generateN()

            privKey = protocol.generatePrivateKey()
            Log.e(TAG, "privKey: $privKey")


            pubKey = protocol.generatePublicKey(privKey)
            Log.e(TAG, "pubKey: $pubKey")

            showToast("Keys generated")

            sendN.setOnClickListener {
                btService.write("n: $n")
                Log.e(TAG, "n sent")
                btService.write("pubKey: $pubKey")
            }
        }
    }

    private fun handleMessage(msg: String) {
        if ("t: " in msg) {
            protocolStatusPr.text = msg
            val x = protocol.calcX()
            btService.write("x: $x")
        }
        if ("vector: " in msg) {
            vectorReceived(msg)
        }
        if ("succeeded" in msg) {
            protocolStatusPr.text = msg
        }
    }

    private fun vectorReceived(msg: String) {
        val v = mutableListOf<Boolean>()
        val p = Pattern.compile("(true|false)")
        val m = p.matcher(msg)
        while (m.find()) {
            val t = m.group()
            val tBool = t!!.toBoolean()
            v.add(tBool)
        }

        val y = protocol.calcY(privKey, v)
        Log.e(TAG, "vector received: $v")
        btService.write("y: $y")

    }

    private fun getDevice(): BluetoothDevice {
        val position = intent.extras?.getInt("device")
        return btService.showPairedBT()!![position!!]
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}