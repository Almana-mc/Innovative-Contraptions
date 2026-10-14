package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, InnovativeContraptions.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH = SOUNDS.register("item.wrench.use",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "item.wrench.use")));

    private ICSounds() {}
}
