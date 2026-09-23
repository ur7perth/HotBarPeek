package com.abo9kr.hotbarpeek;

import com.abo9kr.hotbarpeek.network.HotbarSyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class HotbarPeek implements ModInitializer {
    public static final String MOD_ID = "hotbarpeek";
    private static int tickCounter = 0;

    // Only sync to players within this range (in blocks)
    private static final double SYNC_RANGE = 64.0;
    private static final int SYNC_INTERVAL_TICKS = 4;

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(HotbarSyncPayload.ID, HotbarSyncPayload.CODEC);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % SYNC_INTERVAL_TICKS != 0) return;

            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

            for (ServerPlayerEntity source : players) {
                List<ItemStack> hotbar = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    hotbar.add(source.getInventory().getStack(i));
                }

                HotbarSyncPayload payload = new HotbarSyncPayload(
                        source.getUuid(),
                        source.getInventory().selectedSlot,
                        hotbar
                );

                for (ServerPlayerEntity receiver : players) {
                    if (receiver == source) continue;
                    if (receiver.squaredDistanceTo(source) <= SYNC_RANGE * SYNC_RANGE) {
                        ServerPlayNetworking.send(receiver, payload);
                    }
                }
            }
        });
    }
}
