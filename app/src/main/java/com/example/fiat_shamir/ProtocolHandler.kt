package com.example.fiat_shamir

import android.util.Log
import java.math.BigInteger
import java.util.*
import kotlin.math.ln

const val bits = 20
const val cert = 0


class ProtocolHandler {
    private val rand = Random()
    private lateinit var k: BigInteger
    private lateinit var n: BigInteger
    private lateinit var p: BigInteger
    private lateinit var q: BigInteger
    private lateinit var r: BigInteger


    fun generateN(): BigInteger {
        val m = BigInteger("4")
        val r = BigInteger("3")
        p = BigInteger(bits, cert, rand)

        while (p.mod(m).compareTo(r) != 0) {
            p = BigInteger(bits, cert, rand)
        }
        Log.e(TAG, "found p: $p")

        q = BigInteger(bits, cert, rand)

        while (q.mod(m).compareTo(r) != 0) {
            q = BigInteger(bits, cert, rand)
        }
        Log.e(TAG, "found q: $q")

        n = p.multiply(q)
        k = ln(n.toDouble()).toBigDecimal().toBigInteger()
        return n
    }


    //TODO
    fun generatePublicKey(): List<BigInteger> {
        return emptyList()
    }

    //TODO
    fun generatePrivateKey(publicKey: List<BigInteger>): List<BigInteger> {
        return emptyList()
    }


    //TODO
    fun calcT(): BigInteger {
        return BigInteger.ONE
    }

    //TODO
    //Musi zadeklarować lateinit R
    fun calcX(): BigInteger {
        return BigInteger.ONE
    }

    //TODO
    fun generateVector(): List<Boolean> {
        return emptyList()
    }

    //TODO
    fun calcY(privKey: List<BigInteger>): BigInteger {
        return BigInteger.ONE
    }

    //TODO
    fun verify(x: BigInteger, y: BigInteger, publicKey: List<BigInteger>, n: BigInteger): Boolean {
        return false
    }
}