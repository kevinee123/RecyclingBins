package com.asto.recyclingbins

import android.annotation.SuppressLint
import com.asto.recyclingbins.util.Crc8
import junit.framework.TestCase.assertTrue

import org.junit.Test


/**
 * assertEquals, 预期和实际相等，不相等则抛出异常和信息
 * assertNotEquals, 预期和实际不相等，相等则抛出异常和信息
 * assertNull，传入的为空，不为空抛出异常和信息
 * assertNotNull，传入非空，为空时抛出异常和信息
 * assertTrue，断言为真，如果为假(false)则抛出异常和信息
 * assertFalse，断言为假，如果为真(true)则抛出异常和信息
 * assertSame，引用同一对象，如果不是则抛出异常和信息
 * assertNotSame, 引用不同对象，如果是同一对象则抛出异常和信息
 */
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val data = "aa07016602000201c1dd"
        println(byteToHex(hexToByte(data)))
    }

    /**
     * hex转byte数组
     * @param hex
     * @return
     */
    fun hexToByte(hex: String): ByteArray {
        var m = 0
        var n = 0
        val byteLen = hex.length / 2 // 每两个字符描述一个字节
        val ret = ByteArray(byteLen)
        for (i in 0 until byteLen) {
            m = i * 2 + 1
            n = m + 1
//        val intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n))
//        ret[i] = java.lang.Byte.valueOf(intVal.toByte())
            ret[i] = Integer.parseInt(hex.substring(i * 2, m) + hex.substring(m, n),16).toByte()
        }
        return ret
    }


    /**
     * byte数组转hex
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun byteToHex(bytes: ByteArray): String {
        var strHex: String
        val sb = StringBuilder("")
        for (n in bytes.indices) {
            strHex = Integer.toHexString(bytes[n].toInt() and 0xff)
            sb.append(if (strHex.length == 1) "0$strHex" else strHex) // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim { it <= ' ' }.toLowerCase()
    }

}

