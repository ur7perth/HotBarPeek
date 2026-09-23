package com.abo9kr.hotbarpeek.client;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHotbarCache {

    public static class HotbarSlot {
        public final List<ItemStack> items;
        public final int selectedSlot;
        public final long receivedAt;

        public HotbarSlot(List<ItemStack> items, int selectedSlot, long receivedAt) {
            this.items = items;
            this.selectedSlot = selectedSlot;
            this.receivedAt = receivedAt;
        }
    }

    // How long cached data stays valid if no new packet arrives (ms)
    private static final long STALE_AFTER_MS = 5000;

    private static final Map<UUID, HotbarSlot> CACHE = new ConcurrentHashMap<>();

    public static void update(UUID playerId, int selectedSlot, List<ItemStack> items) {
        CACHE.put(playerId, new HotbarSlot(items, selectedSlot, System.currentTimeMillis()));
    }

    public static HotbarSlot get(UUID playerId) {
        HotbarSlot slot = CACHE.get(playerId);
        if (slot == null) return null;
        if (System.currentTimeMillis() - slot.receivedAt > STALE_AFTER_MS) {
            CACHE.remove(playerId);
            return null;
        }
        return slot;
    }

    public static void clear() {
        CACHE.clear();
    }
}
