package com.example.fiat_shamir

import android.util.Log
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ln
import kotlin.properties.Delegates

const val bits = 20
const val cert = 0


class ProtocolHandler {
    private val rand = Random()
    private var k by Delegates.notNull<Int>()
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
        //k = ln(n.toDouble()).toBigDecimal().toBigInteger()
        k = n.bitLength().toBigInteger().bitLength()
        return n
    }


    //TODO
    fun generatePublicKey(privateKey: List<BigInteger>): List<BigInteger> {
        val arr = ArrayList<BigInteger>(k)
        for ((j, sj) in privateKey.withIndex()) {
            var temp = sj.modPow(BigInteger("2"), n)
            temp = temp.modInverse(n)
            if (rand.nextBoolean()) {
                temp = temp.negate().mod(n)
            }
            arr[j] = temp
        }
        return arr.toList()
    }

    //TODO
    fun generatePrivateKey(): List<BigInteger> {
        val arr = ArrayList<BigInteger>(k)
        for (i in 1 until k) {
            var temp = BigInteger(k, rand)
            while ( (temp.compareTo(p) == 0) || (temp.compareTo(q) == 0) || (temp.compareTo(BigInteger("0")) == 0)) {
                temp = BigInteger(k, rand)
            }
            arr[i] = temp
        }
        return arr.toList()
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
