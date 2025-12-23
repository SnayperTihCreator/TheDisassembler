package snaypertihcreator.thedisassembler.client.renderers;

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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.blocks.DisassemblerBlock;
import snaypertihcreator.thedisassembler.blocksEntity.DisassemblerBlockEntity;

import java.util.Objects;

// Обрати внимание: T extends DisassemblerBlockEntity
public class DisassemblerSawRenderer<T extends DisassemblerBlockEntity> implements BlockEntityRenderer<T> {

    public DisassemblerSawRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(T entity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack stack = entity.getRenderSaw();
        if (stack.isEmpty()) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();

        poseStack.translate(1.01, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        BlockState state = entity.getBlockState();
        boolean isWorking = state.hasProperty(DisassemblerBlock.WORKING) && state.getValue(DisassemblerBlock.WORKING);

        if (entity.getLevel() != null && isWorking) {
            long gameTime = entity.getLevel().getGameTime();
            // Скорость вращения
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