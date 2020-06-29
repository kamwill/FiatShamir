package com.example.fiat_shamir

import android.util.Log
import java.math.BigInteger
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.properties.Delegates

const val bits = 128
const val cert = 0


class ProtocolHandler {
    private val rand = Random()
    private var k by Delegates.notNull<Int>()
    private lateinit var n: BigInteger
    private lateinit var p: BigInteger
    private lateinit var q: BigInteger
    private lateinit var r: BigInteger

    companion object {
        fun regexFind(pat: String, s: String): String {
            val pattern: Pattern = Pattern.compile(pat)
            val matcher: Matcher = pattern.matcher(s)
            return if (matcher.find()) {
                val g = matcher.group(1)
                g!!
            } else {
                ""
            }
        }
    }


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
        k = n.bitLength().toBigInteger().bitLength()
        k = (k..2*k).random()
        Log.e(TAG, "k: $k")
        return n
    }


    //DONE
    fun generatePublicKey(privateKey: List<BigInteger>): List<BigInteger> {
        val list = mutableListOf<BigInteger>()
        for (sj in privateKey) {
            var temp = sj.modPow(BigInteger("2"), n)
            temp = temp.modInverse(n)
            if (rand.nextBoolean()) {
                temp = temp.negate().mod(n)
            }
            list.add(temp)
        }
        return list
    }

    //DONE
    fun generatePrivateKey(): List<BigInteger> {
        val list = mutableListOf<BigInteger>()
        for (i in 1..k) {
            var temp = BigInteger(k, rand)
            while ((temp.compareTo(p) == 0) || (temp.compareTo(q) == 0) || (temp.compareTo(
                    BigInteger.ZERO
                ) == 0)
            ) {
                temp = BigInteger(k, rand)
            }
            list.add(temp)
        }
        return list
    }


    //DONE
    fun calcT(n: BigInteger): Int {
        val t = n.bitLength()
        return (t..2*t).random()
    }

    //DONE
    //Musi zadeklarować lateinit R
    fun calcX(): BigInteger {
        r = BigInteger(k, rand)
        while (r.compareTo(BigInteger.ZERO) == 0) {
            r = BigInteger(k, rand)
        }
        var X = r.modPow(BigInteger("2"), n)
        if (rand.nextBoolean()) {
            X = X.negate().mod(n)
        }
        return X
    }

    //DONE
    fun generateVector(k: Int): List<Boolean> {
        Log.e(TAG, "k: $k")
        val vector = mutableListOf<Boolean>()
        for (i in 1..k) {
            vector.add(Random().nextBoolean())
        }
        return vector
    }

    //DONE
    fun calcY(privKey: List<BigInteger>, vector: List<Boolean>): BigInteger {
        var y = r
        for ((index, bool) in vector.withIndex()) {
            if (bool) {
                y = y.multiply(privKey[index]).mod(n)
            }
        }
        return y
    }

    //DONE
    fun verify(x: BigInteger, y: BigInteger, publicKey: List<BigInteger>, n: BigInteger, vector: List<Boolean>): Boolean {
        var temp = y.modPow(BigInteger("2"), n)
        for ((index, bool) in vector.withIndex()) {
            if (bool) {
                temp = temp.multiply(publicKey[index]).mod(n)
            }
        }
        return (x == temp) || (x == temp.negate().mod(n))
    }
}
