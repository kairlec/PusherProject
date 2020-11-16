package com.kairlec.pusher.utils

import java.math.BigInteger
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PBKDF2Util {
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"
    private const val SALT_BYTE_SIZE = 32 / 2 //盐的长度
    private const val HASH_BIT_SIZE = 128 * 4 //生成密文的长度
    private const val PBKDF2_ITERATIONS = 1000 //迭代次数
    private val secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)

    /**
     * 对输入的密码进行验证
     * @param attemptedPassword:[String] 待验证密码
     * @param encryptedPassword:[String] 密文
     * @param salt:[String] 盐值
     * @return: [Boolean]
     */
    fun authenticate(attemptedPassword: String, encryptedPassword: String, salt: String): Boolean {
        // 用相同的盐值对用户输入的密码进行加密
        val encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt)
        // 把加密后的密文和原密文进行比较，相同则验证成功，否则失败
        return encryptedAttemptedPassword == encryptedPassword
    }

    /**
     * 生成密文
     * @param password: [String] 明文密码
     * @param salt: [String] 盐值
     * @return: [String]
     */
    fun getEncryptedPassword(password: String, salt: String): String {
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), fromHex(salt), PBKDF2_ITERATIONS, HASH_BIT_SIZE)
        return toHex(secretKeyFactory.generateSecret(spec).encoded)
    }

    /**
     * 通过加密的强随机数生成盐(最后转换为16进制)
     * @param: []
     * @return: [String]
     */
    fun generateSalt(): String {
        val random = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(SALT_BYTE_SIZE)
        random.nextBytes(salt)
        return toHex(salt)
    }

    /**
     * 十六进制字符串转二进制字符串
     * @param hex: [String]
     * @return: [ByteArray]
     */
    private fun fromHex(hex: String): ByteArray {
        val binary = ByteArray(hex.length / 2)
        for (i in binary.indices) {
            binary[i] = hex.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        }
        return binary
    }

    /**
     * 二进制字符串转十六进制字符串
     * @param array: [ByteArray]
     * @return: [String]
     */
    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) String.format("%0${paddingLength}d", 0) + hex else hex
    }
}