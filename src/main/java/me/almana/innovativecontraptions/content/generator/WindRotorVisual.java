package me.almana.innovativecontraptions.content.generator;

public interface WindRotorVisual {
    float speed();
    boolean hasRotor();
    default float rotorRadius() { return 1; }
}
