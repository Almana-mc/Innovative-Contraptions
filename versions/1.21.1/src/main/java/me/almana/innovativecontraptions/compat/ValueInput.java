package me.almana.innovativecontraptions.compat;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public final class ValueInput {
    private final CompoundTag tag;
    private final HolderLookup.Provider registries;

    public ValueInput(CompoundTag tag, HolderLookup.Provider registries) {
        this.tag = tag;
        this.registries = registries;
    }

    public int getIntOr(String key, int fallback) { return numeric(key) ? tag.getInt(key) : fallback; }
    public long getLongOr(String key, long fallback) { return numeric(key) ? tag.getLong(key) : fallback; }
    public Optional<Long> getLong(String key) { return numeric(key) ? Optional.of(tag.getLong(key)) : Optional.empty(); }
    public float getFloatOr(String key, float fallback) { return numeric(key) ? tag.getFloat(key) : fallback; }
    public double getDoubleOr(String key, double fallback) { return numeric(key) ? tag.getDouble(key) : fallback; }
    public boolean getBooleanOr(String key, boolean fallback) { return numeric(key) ? tag.getBoolean(key) : fallback; }
    public String getStringOr(String key, String fallback) { return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : fallback; }

    public <T> Optional<T> read(String key, Codec<T> codec) {
        Tag value = tag.get(key);
        return value == null ? Optional.empty() : codec.parse(registries.createSerializationContext(NbtOps.INSTANCE), value).resultOrPartial();
    }

    public ValueInput childOrEmpty(String key) { return new ValueInput(tag.getCompound(key), registries); }

    public CompoundTag tag() { return tag; }
    public HolderLookup.Provider registries() { return registries; }

    private boolean numeric(String key) { return tag.contains(key, Tag.TAG_ANY_NUMERIC); }
}
