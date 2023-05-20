package com.minelittlepony.client.render;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePonyClient;
import com.minelittlepony.client.util.render.RenderLayerUtil;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class LevitatingItemRenderer {
    @Deprecated
    private static int COLOR;
    @Deprecated
    public static boolean isEnabled() {
        return false;
    }
    @Deprecated
    public static RenderLayer getRenderLayer(Identifier texture) {
        return MagicGlow.getColoured(texture, COLOR);
    }

    private VertexConsumerProvider getProvider(IPony pony, VertexConsumerProvider renderContext) {
        final int color = pony.metadata().getGlowColor();
        return layer -> {
            return renderContext.getBuffer(MagicGlow.getColoured(RenderLayerUtil.getTexture(layer).orElse(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE), color));
        };
    }

    /**
     * Renders an item in first person optionally with a magical overlay.
     */
    public void renderItemInFirstPerson(ItemRenderer itemRenderer, @Nullable LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack matrix, VertexConsumerProvider renderContext, @Nullable World world, int lightUv, int posLong) {

        if (entity instanceof PlayerEntity && (mode.isFirstPerson() || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND)) {

            IPony pony = IPony.getManager().getPony((PlayerEntity)entity);

            COLOR = pony.metadata().getGlowColor();

            matrix.push();

            boolean doMagic = MineLittlePonyClient.getInstance().getConfig().fpsmagic.get() && pony.hasMagic();

            if (doMagic && mode.isFirstPerson()) {
                setupPerspective(itemRenderer, entity, stack, left, matrix);
            }

            itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);

            if (doMagic) {
                VertexConsumerProvider interceptedContext = getProvider(pony, renderContext);

                matrix.scale(1.1F, 1.1F, 1.1F);
                matrix.translate(0.015F, 0.01F, 0.01F);

                itemRenderer.renderItem(entity, stack, mode, left, matrix, interceptedContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
                matrix.translate(-0.03F, -0.02F, -0.02F);
                itemRenderer.renderItem(entity, stack, mode, left, matrix, interceptedContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
            }

            matrix.pop();
        } else {
            itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
        }
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(ItemRenderer renderer, LivingEntity entity, ItemStack item, boolean left, MatrixStack stack) {
        UseAction action = item.getUseAction();

        boolean doNormal = entity.getItemUseTime() <= 0 || action == UseAction.NONE || (action == UseAction.CROSSBOW && CrossbowItem.isCharged(item));

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = left ? 1 : -1;
            float ticks = entity.age * sign;

            float floatAmount = -(float)Math.sin(ticks / 9F) / 40F;
            float driftAmount = -(float)Math.cos(ticks / 6F) / 40F;

            boolean handHeldTool =
                       action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK;

            float distanceChange = handHeldTool ? -0.3F : -0.6F;

            stack.translate(
                    driftAmount - floatAmount / 4F + distanceChange / 1.5F * sign,
                    floatAmount,
                    distanceChange);

            if (!handHeldTool) { // bows have to point forwards
                stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(sign * -60 + floatAmount));
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(sign * 30 + driftAmount));
            }
        }
    }
}
