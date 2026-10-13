package me.almana.innovativecontraptions.content.energy;

public interface EuSource {
    int sourceVoltage();

    int availableEu();

    int extractEu(int amount);

    default int packetsPerTick() { return 1; }
}
