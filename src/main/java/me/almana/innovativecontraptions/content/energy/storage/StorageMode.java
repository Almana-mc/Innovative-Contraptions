package me.almana.innovativecontraptions.content.energy.storage;

public final class StorageMode {
    public static boolean signal(int mode, int energy, int capacity, int packet) {
        return switch (mode) {
            case 1 -> energy >= capacity - 20 * packet;
            case 2 -> energy > packet && energy < capacity - packet;
            case 3 -> energy < capacity - packet;
            case 4 -> energy < packet;
            default -> false;
        };
    }

    public static boolean emit(int mode, int energy, int capacity, int packet, boolean powered) {
        return switch (mode) {
            case 5 -> !powered;
            case 6 -> !powered || energy > capacity - 20 * packet;
            default -> true;
        };
    }

    private StorageMode() {}
}
