package org.jetbrains.kotlin.dlt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Service
@RestController
class Ledger {

    val nodes: List<Node> = listOf(Node("A", mutableListOf()), Node("B", mutableListOf()),
            Node("C", mutableListOf()))

    val allLedgerTransactions: MutableList<Transaction> = mutableListOf()
    private val logger: Logger = LoggerFactory.getLogger(Transaction::class.java)

    @GetMapping("/postTransaction")
    fun postTransaction(nodeId: String, transactionId: String, amount: Long){
        logger.info("Posted transaction --> transactionId: ${transactionId} | amount: ${amount} | node: ${nodeId}")
        if (isValidNode(nodeId) && !nodeContainsTransaction(nodeId, transactionId) && !transactionExistsOnLedger(transactionId)) {
            try {
                val currentNode = getNode(nodeId)
                currentNode.transactions.add(Transaction(transactionId, amount, currentNode.transactions.last().currentHash))
                allLedgerTransactions.add(Transaction(transactionId, amount, currentNode.transactions.last().currentHash))
            } catch (e: NoSuchElementException) {
                val currentNode = getNode(nodeId)
                currentNode.transactions.add(Transaction(transactionId, amount,"0"))
                allLedgerTransactions.add(Transaction(transactionId, amount, "0"))
            }
        } else {
            logger.error("Transaction is already present on the ledger!")
            throw error("Non-existent node/transaction already exists")
        }
    }

    fun deleteTransaction(nodeId: String, transactionId: String){
        var index: Int? = null
        if (isValidNode(nodeId) && nodeContainsTransaction(nodeId, transactionId)) {
            val currentNode = getNode(nodeId)
            for (trans in currentNode.transactions){
                if (trans.transactionId == transactionId){
                    index = currentNode.transactions.indexOf(trans)
                }
            }
            currentNode.transactions.removeAt(index!!)
            logger.info("Deleted transaction --> transactionId: ${transactionId} | node: ${nodeId}")
        }

    }

    fun getTransactions(inputNodeId: String): Set<Transaction> {
        var finalTransactionList: Set<Transaction>
        var inputNodeTransactionList: MutableList<Transaction>
        var subscriptionNodeTransactionList: MutableList<Transaction>
        var node: Node
        var subNode: Node

        logger.info("Transactions for node: ${inputNodeId}")

        if (isValidNode(inputNodeId)){
            node = getNode(inputNodeId)
            if (node.subscription){
                inputNodeTransactionList = node.transactions
                subNode = getNode(node.subscribedNode!!)
                subscriptionNodeTransactionList = subNode.transactions
                finalTransactionList = inputNodeTransactionList.union(subscriptionNodeTransactionList)
                logger.info("Transactions: ${finalTransactionList}")

                return finalTransactionList
            }
            logger.info("Transactions: ${getNode(inputNodeId).transactions}")
            return node.transactions.toSet()
        } else {
            throw error("Non-existent node")
        }
    }

    fun subscribe(initiatingNodeId: String, subscribeNodeId: String){
        if (isValidNode(initiatingNodeId) && isValidNode(subscribeNodeId) && !getNode(initiatingNodeId).subscription) {
            getNode(initiatingNodeId).subscribedNode = subscribeNodeId
            getNode(initiatingNodeId).subscription = true
            logger.info("Subscribed node: ${initiatingNodeId} to node: ${subscribeNodeId}")
        }
        else
            throw error("Invalid node/node already subscribed")
    }

    fun unsubscribe(initiatingNodeId: String, subscribeNodeId: String){
        if (isValidNode(initiatingNodeId) && isValidNode(subscribeNodeId) && getNode(initiatingNodeId).subscription) {
            getNode(initiatingNodeId).subscribedNode = null
            getNode(initiatingNodeId).subscription = false
            logger.info("Unsubscribed node: ${initiatingNodeId} to node: ${subscribeNodeId}")
        }
        else
            throw error("Invalid node/node already subscribed")
    }

    private fun isValidNode(nodeId: String): Boolean {
        val foundNode = nodes.find { it.nodeId == nodeId } ?: return false
        return true
    }

    private fun getNode(nodeId: String): Node{
        return nodes.first() { it.nodeId == nodeId }
    }

    private fun nodeContainsTransaction(inputNodeId: String, inputTransactionId: String): Boolean{
        getNode(inputNodeId).transactions.firstOrNull() { it.transactionId == inputTransactionId } ?: return false
        return true
    }

    private fun transactionExistsOnLedger(transactionId: String): Boolean {
        for (trans in allLedgerTransactions){
            if (trans.transactionId == transactionId){
                return true
            }
        }
        return false
    }
}