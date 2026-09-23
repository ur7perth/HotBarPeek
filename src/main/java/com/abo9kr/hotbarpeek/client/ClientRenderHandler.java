package com.abo9kr.hotbarpeek.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ClientRenderHandler {

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(ClientRenderHandler::onRenderEntities);
    }

    private static void onRenderEntities(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) return;

        Vec3d cameraPos = context.camera().getPos();

        for (PlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;

            ClientHotbarCache.HotbarSlot slot = ClientHotbarCache.get(player.getUuid());
            if (slot == null) continue;

            renderHotbarAbove(matrices, consumers, player, slot, cameraPos);
        }
    }

    private static void renderHotbarAbove(MatrixStack matrices, VertexConsumerProvider consumers,
                                           PlayerEntity player, ClientHotbarCache.HotbarSlot slot,
                                           Vec3d cameraPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        double x = player.getX() - cameraPos.x;
        double y = player.getY() + player.getHeight() + 0.6;
        double z = player.getZ() - cameraPos.z;

        List<ItemStack> items = slot.items;
        float totalWidth = items.size() * 18f;
        float startX = -totalWidth / 2f;

        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(client.gameRenderer.getCamera().getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);

        ItemRenderer itemRenderer = client.getItemRenderer();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            matrices.push();
            matrices.translate(startX + i * 18f, 0, 0);
            if (i == slot.selectedSlot) {
                matrices.scale(1.15f, 1.15f, 1.15f);
            }

            itemRenderer.renderItem(
                    stack,
                    ModelTransformationMode.GUI,
                    15728880, // full brightness
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    consumers,
                    player.getWorld(),
                    0
            );

            matrices.pop();
        }

        matrices.pop();
    }
}
