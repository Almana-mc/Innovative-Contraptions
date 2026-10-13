package me.almana.innovativecontraptions.compat.transfer.transaction;

public final class RootCommitJournal extends SnapshotJournal<Void> {
    private final Runnable rootCommitCallback;

    public RootCommitJournal(Runnable rootCommitCallback) {
        this.rootCommitCallback = rootCommitCallback;
    }

    @Override
    protected Void createSnapshot() {
        return null;
    }

    @Override
    protected void revertToSnapshot(Void snapshot) {}

    @Override
    protected void onRootCommit(Void originalState) {
        rootCommitCallback.run();
    }
}
