package snaypertihcreator.thedisassember.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.blocksEntity.Tier2DisassemblerBlockEntity;

import java.util.Objects;

public class Tier2DisassemblerRenderer implements BlockEntityRenderer<Tier2DisassemblerBlockEntity> {

    public Tier2DisassemblerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(Tier2DisassemblerBlockEntity entity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack stack = entity.getRenderSaw();
        if (stack.isEmpty()) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();

        poseStack.translate(1.01, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        if (entity.getLevel() != null && entity.isActive()) {
            long gameTime = entity.getLevel().getGameTime();
            float rotation = (gameTime + partialTick) * 20.0f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rotation));
        }
        poseStack.scale(0.75f, 0.75f, 0.75f);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                getLightLevel(Objects.requireNonNull(entity.getLevel()), entity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.getLevel(), 0);
        poseStack.popPose();
    }

    private int getLightLevel(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos.west());
        int sLight = level.getBrightness(LightLayer.SKY, pos.west());
        return LightTexture.pack(bLight, sLight);
    }
}