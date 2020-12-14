package org.jetbrains.kotlin.dlt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import java.util.*

data class Transaction (
    val transactionId: String,
    val amount: Long,
    val hash: String
) {
    val currentHash = calculateHash()
    val date: Long = Date().time

    private val logger: Logger = LoggerFactory.getLogger(Transaction::class.java)

    fun calculateHash(): String {
        val input = (transactionId + date + hash + amount).toByteArray()
        return DigestUtils.md5DigestAsHex(input)
    }

}