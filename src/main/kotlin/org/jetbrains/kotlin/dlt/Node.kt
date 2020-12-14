package org.jetbrains.kotlin.dlt


data class Node (
        val nodeId: String,
        var transactions: MutableList<Transaction>
) {
    var subscription: Boolean = false
    var subscribedNode: String? = null

    var transactionsBeforeSub: MutableList<Transaction> = mutableListOf()

    fun addTransactions(subscribedTransactions: MutableList<Transaction>){
        transactionsBeforeSub = transactions
        for (trans in subscribedTransactions){
            if (!transactions.contains(trans)){
                transactions.add(trans)
            }
        }
    }

    fun deleteTransactions(unsubscribedTransactions: MutableList<Transaction>) {
//        for (trans in unsubscribedTransactions){
//            if (transactions.contains(trans)){
//                transactions.remove(trans)
//            }
//        }
        transactions = transactionsBeforeSub
    }

}