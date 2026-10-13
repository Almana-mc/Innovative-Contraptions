package me.almana.innovativecontraptions.compat.transfer.transaction;

public sealed interface TransactionContext permits Transaction {
    int depth();
}
