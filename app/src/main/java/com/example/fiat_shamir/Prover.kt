package com.example.fiat_shamir

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prover.*
import java.math.BigInteger

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
            showToast("Generated n: $n")

            privKey = protocol.generatePrivateKey()
            Log.e(TAG, "privKey: $privKey")


            pubKey = protocol.generatePublicKey(privKey)
            Log.e(TAG, "pubKey: $pubKey")

            btService.write("Prover is connected")

            sendN.setOnClickListener {
                btService.write("n: $n")
                Log.e(TAG, "n sent")
                btService.write("pubKey: $pubKey")
            }
        }
    }

    private fun handleMessage(msg: String) {
        if ("Start verification" == msg) {
            protocolStatusPr.text = "Started"
            val x = protocol.calcX()
            btService.write("x: $x")
        }
    }

    private fun getDevice(): BluetoothDevice {
        val position = intent.extras?.getInt("device")
        return btService.showPairedBT()!![position!!]
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}