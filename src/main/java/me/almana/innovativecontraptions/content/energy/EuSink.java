package me.almana.innovativecontraptions.content.energy;

public interface EuSink {
    int receiveEu(int voltage, int amount);

    default int maxInputVoltage() { return 32; }

    default int demandedEu() { return Integer.MAX_VALUE; }

    default boolean isEuSink() {
        return true;
    }
}
