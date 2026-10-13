package me.almana.innovativecontraptions.content.rubber;

import net.minecraft.util.StringRepresentable;

public enum SapStage implements StringRepresentable {
    NONE("none"),
    EMPTY("empty"),
    READY("ready");

    private final String serializedName;

    SapStage(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
