package com.abo9kr.hotbarpeek.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record HotbarSyncPayload(UUID playerId, int selectedSlot, List<ItemStack> hotbarItems)
        implements CustomPayload {

    public static final CustomPayload.Id<HotbarSyncPayload> ID =
            new CustomPayload.Id<>(Identifier.of("hotbarpeek", "hotbar_sync"));

    public static final PacketCodec<RegistryByteBuf, HotbarSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.UUID, HotbarSyncPayload::playerId,
            PacketCodecs.VAR_INT, HotbarSyncPayload::selectedSlot,
            PacketCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_PACKET_CODEC), HotbarSyncPayload::hotbarItems,
            HotbarSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
