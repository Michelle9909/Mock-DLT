import org.jetbrains.kotlin.dlt.Ledger
import org.jetbrains.kotlin.dlt.Transaction
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFails

class LedgerTests {

    private val ledger: Ledger = Ledger()
    private val logger: Logger = LoggerFactory.getLogger(LedgerTests::class.java)

    @Test
    fun `A - post transaction `() {
        ledger.postTransaction("A", "1001", 100)
    }
    @Test
    fun `B - post transaction with non-existent node`() {
        ledger.postTransaction("D", "1001", 100)
    }

    @Test
    fun `C - get transaction with from node`() {
        ledger.postTransaction("A", "1001", 100)
        ledger.getTransactions("A")
    }
    @Test
    fun `D - delete transaction `() {
        ledger.postTransaction("A", "1001", 100)
        ledger.postTransaction("A", "1002", 100)
        ledger.postTransaction("A", "1003", 100)
        ledger.getTransactions("A")
        ledger.deleteTransaction("A", "1002")
        ledger.getTransactions("A")
    }

    @Test
    fun `E - Demo test case `() {
        ledger.postTransaction("A", "100", 100);

        ledger.getTransactions("A")

        ledger.subscribe("A", "B");

        ledger.postTransaction("B", "101",1000);
        assertFails { ledger.postTransaction("C", "101", 1000); } // rejected transaction

        ledger.postTransaction("C", "102", 1000);
        ledger.postTransaction("B", "103", 1000);
        ledger.postTransaction("C", "104", 1000);

        ledger.postTransaction("B", "105", 1000);

        ledger.getTransactions("B");
        ledger.getTransactions("A");
        ledger.deleteTransaction("B", "105")

        // Node A's transaction chain should return 3 transaction ids [103, 101, 100].
        ledger.getTransactions("A");

        // Node B's transaction chain should return 2 transaction ids [103, 101]
        ledger.getTransactions("B");

        // Node C's transaction chain should return 2 transaction ids [104, 102]
        ledger.getTransactions("C");

        // Node A unsubscribe to Node B
        ledger.unsubscribe("A", "B");

        // Node A's transaction chain should return [100], as it no longer subscribe to Node B
        ledger.getTransactions("A");

        ledger.subscribe("A", "C")
        ledger.subscribe("C", "A")
        ledger.getTransactions("A")
        ledger.getTransactions("C")

    }


}