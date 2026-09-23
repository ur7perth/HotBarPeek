package com.abo9kr.hotbarpeek.client;

import net.fabricmc.api.ClientModInitializer;

public class HotbarPeekClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRenderHandler.register();
    }
}
