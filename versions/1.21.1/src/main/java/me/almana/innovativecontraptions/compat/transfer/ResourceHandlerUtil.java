package me.almana.innovativecontraptions.compat.transfer;

import java.util.Objects;
import java.util.function.Predicate;
import me.almana.innovativecontraptions.compat.transfer.resource.Resource;
import me.almana.innovativecontraptions.compat.transfer.resource.ResourceStack;
import me.almana.innovativecontraptions.compat.transfer.transaction.Transaction;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import org.jetbrains.annotations.Nullable;

public final class ResourceHandlerUtil {
    public static <T extends Resource> int insertStacking(@Nullable ResourceHandler<T> handler, T resource, int amount, @Nullable TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (handler == null || amount == 0) return 0;
        try (Transaction tx = Transaction.open(transaction)) {
            int inserted = insertInto(handler, resource, amount, false, tx);
            if (inserted < amount) inserted += insertInto(handler, resource, amount - inserted, true, tx);
            tx.commit();
            return inserted;
        }
    }

    private static <T extends Resource> int insertInto(ResourceHandler<T> handler, T resource, int amount, boolean empty, TransactionContext tx) {
        int inserted = 0;
        for (int index = 0; index < handler.size(); index++) {
            if (handler.getResource(index).isEmpty() != empty) continue;
            inserted += handler.insert(index, resource, amount - inserted, tx);
            if (inserted == amount) break;
        }
        return inserted;
    }

    public static <T extends Resource> int move(@Nullable ResourceHandler<T> from, @Nullable ResourceHandler<T> to, Predicate<T> filter, int amount, @Nullable TransactionContext transaction) {
        Objects.requireNonNull(filter, "Filter may not be null");
        TransferPreconditions.checkNonNegative(amount);
        if (from == null || to == null || amount == 0) return 0;
        try (Transaction subTransaction = Transaction.open(transaction)) {
            int totalMoved = 0;
            for (int index = 0; index < from.size(); ++index) {
                T fromResource = from.getResource(index);
                if (fromResource.isEmpty() || !filter.test(fromResource)) continue;
                int maxExtracted;
                try (Transaction simulatedExtract = Transaction.open(subTransaction)) {
                    maxExtracted = from.extract(index, fromResource, amount - totalMoved, simulatedExtract);
                }
                if (maxExtracted == 0) continue;
                try (Transaction transferTransaction = Transaction.open(subTransaction)) {
                    int inserted = to.insert(fromResource, maxExtracted, transferTransaction);
                    if (inserted != from.extract(index, fromResource, inserted, transferTransaction)) continue;
                    totalMoved += inserted;
                    transferTransaction.commit();
                    if (totalMoved >= amount) break;
                }
            }
            subTransaction.commit();
            return totalMoved;
        } catch (Exception e) {
            throw crash(e, "Moving resources between resource handlers", from, to, filter, amount, transaction);
        }
    }

    @Nullable
    public static <T extends Resource> ResourceStack<T> moveFirst(@Nullable ResourceHandler<T> from, @Nullable ResourceHandler<T> to, Predicate<T> filter, int amount, @Nullable TransactionContext transaction) {
        Objects.requireNonNull(filter, "Filter may not be null");
        TransferPreconditions.checkNonNegative(amount);
        if (from == null || to == null || amount == 0) return null;
        try {
            int totalMoved = 0;
            T selectedResource = null;
            for (int index = 0; index < from.size(); ++index) {
                T fromResource = from.getResource(index);
                if (selectedResource == null && (fromResource.isEmpty() || !filter.test(fromResource))
                        || selectedResource != null && !selectedResource.equals(fromResource)) continue;
                int extracted;
                try (Transaction simulatedExtractTransaction = Transaction.open(transaction)) {
                    extracted = from.extract(index, fromResource, amount - totalMoved, simulatedExtractTransaction);
                }
                if (extracted == 0) continue;
                try (Transaction transferTransaction = Transaction.open(transaction)) {
                    int inserted = to.insert(fromResource, extracted, transferTransaction);
                    if (inserted == 0 || inserted != from.extract(index, fromResource, inserted, transferTransaction)) continue;
                    totalMoved += inserted;
                    transferTransaction.commit();
                    selectedResource = fromResource;
                    if (totalMoved >= amount) break;
                }
            }
            return totalMoved > 0 ? new ResourceStack<>(selectedResource, totalMoved) : null;
        } catch (Exception e) {
            throw crash(e, "Moving resources between storages", from, to, filter, amount, transaction);
        }
    }

    public static <T extends Resource> boolean contains(ResourceHandler<T> handler, T resource) {
        TransferPreconditions.checkNonEmpty(resource);
        for (int index = 0; index < handler.size(); index++) {
            if (resource.equals(handler.getResource(index))) return true;
        }
        return false;
    }

    @Nullable
    public static <T extends Resource> ResourceStack<T> extractFirst(@Nullable ResourceHandler<T> handler, Predicate<T> filter, int amount, @Nullable TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        if (handler == null || amount == 0) return null;
        T resource = findExtractableResource(handler, filter, transaction);
        if (resource == null) return null;
        try (var tx = Transaction.open(transaction)) {
            int extracted = handler.extract(resource, amount, tx);
            if (extracted == 0) return null;
            tx.commit();
            return new ResourceStack<>(resource, extracted);
        }
    }

    @Nullable
    public static <T extends Resource> T findExtractableResource(ResourceHandler<T> handler, Predicate<T> filter, @Nullable TransactionContext transaction) {
        try (Transaction temp = Transaction.open(transaction)) {
            for (int index = 0; index < handler.size(); index++) {
                T resource = handler.getResource(index);
                if (!resource.isEmpty() && filter.test(resource) && handler.extract(index, resource, handler.getAmountAsInt(index), temp) > 0) {
                    return resource;
                }
            }
            return null;
        }
    }

    private static ReportedException crash(Exception e, String title, Object from, Object to, Object filter, int amount, @Nullable TransactionContext transaction) {
        CrashReport report = CrashReport.forThrowable(e, title);
        report.addCategory("Move details")
                .setDetail("Input", from::toString)
                .setDetail("Output", to::toString)
                .setDetail("Filter", filter::toString)
                .setDetail("Amount", amount)
                .setDetail("Transaction", transaction);
        return new ReportedException(report);
    }

    private ResourceHandlerUtil() {}
}
