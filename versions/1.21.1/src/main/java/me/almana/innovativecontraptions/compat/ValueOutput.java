package me.almana.innovativecontraptions.compat;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

public final class ValueOutput {
    private final CompoundTag tag;
    private final HolderLookup.Provider registries;

    public ValueOutput(CompoundTag tag, HolderLookup.Provider registries) {
        this.tag = tag;
        this.registries = registries;
    }

    public void putInt(String key, int value) { tag.putInt(key, value); }
    public void putLong(String key, long value) { tag.putLong(key, value); }
    public void putFloat(String key, float value) { tag.putFloat(key, value); }
    public void putDouble(String key, double value) { tag.putDouble(key, value); }
    public void putBoolean(String key, boolean value) { tag.putBoolean(key, value); }
    public void putString(String key, String value) { tag.putString(key, value); }

    public <T> void store(String key, Codec<T> codec, T value) {
        codec.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), value).resultOrPartial().ifPresent(encoded -> tag.put(key, encoded));
    }

    public ValueOutput child(String key) {
        CompoundTag child = new CompoundTag();
        tag.put(key, child);
        return new ValueOutput(child, registries);
    }

    public CompoundTag tag() { return tag; }
    public HolderLookup.Provider registries() { return registries; }
}
