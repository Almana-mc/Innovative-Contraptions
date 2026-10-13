package me.almana.innovativecontraptions.compat.transfer.resource;

public record ResourceStack<T extends Resource>(T resource, int amount) {
    @Override
    public String toString() {
        return amount + "x " + resource;
    }
}
