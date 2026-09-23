package com.abo9kr.hotbarpeek.client;

import com.abo9kr.hotbarpeek.network.HotbarSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class HotbarPeekClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(HotbarSyncPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        ClientHotbarCache.update(payload.playerId(), payload.selectedSlot(), payload.hotbarItems())
                )
        );

        ClientRenderHandler.register();
    }
}
