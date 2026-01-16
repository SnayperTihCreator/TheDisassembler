package snaypertihcreator.thedisassembler.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.blocks.DisassemblerBlock;
import snaypertihcreator.thedisassembler.blocksEntity.disassembler.DisassemblerBlockEntity;

import java.util.Objects;

public class DisassemblerSawRenderer<T extends DisassemblerBlockEntity> implements BlockEntityRenderer<T> {

    public DisassemblerSawRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(T entity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack stack = entity.getRenderSaw();
        if (stack.isEmpty()) return;

        BlockState state = entity.getBlockState();
        Direction facing = state.hasProperty(DisassemblerBlock.FACING) ? state.getValue(DisassemblerBlock.FACING) : Direction.NORTH;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        float f = facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-f));

        poseStack.translate(-0.505, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        boolean isWorking = state.hasProperty(DisassemblerBlock.WORKING) && state.getValue(DisassemblerBlock.WORKING);
        if (entity.getLevel() != null && isWorking) {
            float rotation = (entity.getLevel().getGameTime() + partialTick) * 20.0f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        }

        poseStack.scale(0.8f, 0.8f, 0.8f);
        Direction side = facing.getCounterClockWise();

        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                getLightLevel(Objects.requireNonNull(entity.getLevel()), entity.getBlockPos(), side),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.getLevel(), 0);

        poseStack.popPose();
    }

    // Поправленный метод света
    private int getLightLevel(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, Direction facing) {
        net.minecraft.core.BlockPos lightPos = pos.relative(facing);
        int bLight = level.getBrightness(LightLayer.BLOCK, lightPos);
        int sLight = level.getBrightness(LightLayer.SKY, lightPos);
        return LightTexture.pack(bLight, sLight);
    }
}