package me.almana.innovativecontraptions.compat.transfer.transaction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import org.jetbrains.annotations.Nullable;

final class TransactionManager {
    private static final ThreadLocal<TransactionManager> MANAGERS = ThreadLocal.withInitial(TransactionManager::new);
    final Thread thread = Thread.currentThread();
    final List<Transaction> stack = new ArrayList<>();
    int currentDepth = -1;
    final Queue<SnapshotJournal<?>> rootCommitQueue = new ArrayDeque<>();
    boolean processingRootCommitQueue = false;

    static TransactionManager getManagerForThread() {
        return MANAGERS.get();
    }

    Transaction open(@Nullable TransactionContext parent) {
        if (parent != null) {
            Transaction parentImpl = (Transaction) parent;
            validateCurrentTransaction(parentImpl);
            parentImpl.validateOpen();
        } else if (currentDepth >= 0) {
            throw new IllegalStateException("A root transaction is already active on this thread " + thread + ".");
        }
        Transaction current;
        if (stack.size() == ++currentDepth) {
            current = new Transaction(this, currentDepth);
            stack.add(current);
        } else {
            current = stack.get(currentDepth);
        }
        current.open = true;
        return current;
    }

    Transaction getOpenTransaction(int depth) {
        validateCurrentThread();
        if (depth < 0) throw new IndexOutOfBoundsException("Depth may not be negative.");
        if (depth > currentDepth) throw new IndexOutOfBoundsException("There is no open transaction for depth `" + depth + "`");
        Transaction transaction = stack.get(depth);
        transaction.validateOpen();
        return transaction;
    }

    void validateCurrentThread() {
        if (Thread.currentThread() != thread) {
            throw new IllegalStateException("Attempted to access transaction state from thread %s, but this transaction is only valid on thread %s."
                    .formatted(Thread.currentThread().getName(), thread.getName()));
        }
    }

    void validateCurrentTransaction(Transaction transaction) {
        validateCurrentThread();
        if (currentDepth != -1 && stack.get(currentDepth) == transaction) return;
        throw new IllegalStateException("Transaction function was called on a transaction with depth `%d`, but the current transaction has depth `%d`."
                .formatted(transaction.depth(), currentDepth));
    }

    @Nullable
    RuntimeException processRootCommitQueue(@Nullable RuntimeException closeException) {
        // Nested callbacks join outer processing
        if (processingRootCommitQueue) return closeException;
        processingRootCommitQueue = true;
        while (!rootCommitQueue.isEmpty()) {
            SnapshotJournal<?> journal = rootCommitQueue.remove();
            try {
                journal.callOnRootCommit();
            } catch (Exception exception) {
                if (closeException == null) {
                    closeException = new RuntimeException("Encountered an exception while invoking a journal's onRootCommit method.", exception);
                } else {
                    closeException.addSuppressed(exception);
                }
            }
        }
        processingRootCommitQueue = false;
        return closeException;
    }

    private TransactionManager() {}
}
