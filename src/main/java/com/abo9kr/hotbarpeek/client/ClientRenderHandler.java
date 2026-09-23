package com.abo9kr.hotbarpeek.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

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

            ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);
            if (heldItem.isEmpty()) continue;

            renderItemAbove(matrices, consumers, player, heldItem, cameraPos);
        }
    }

    private static void renderItemAbove(MatrixStack matrices, VertexConsumerProvider consumers,
                                         PlayerEntity player, ItemStack stack, Vec3d cameraPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        double x = player.getX() - cameraPos.x;
        double y = player.getY() + player.getHeight() + 0.6;
        double z = player.getZ() - cameraPos.z;

        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(client.gameRenderer.getCamera().getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);

        client.getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GUI,
                15728880,
                OverlayTexture.DEFAULT_UV,
                matrices,
                consumers,
                player.getWorld(),
                0
        );

        matrices.pop();
    }
}
