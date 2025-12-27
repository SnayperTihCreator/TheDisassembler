package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CoalExtractorBlock extends ExtractorBlock{
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public CoalExtractorBlock(TierExtractor tier) {
        super(tier);
        registerDefaultState(stateDefinition.any()
                .setValue(OPEN, false)
        );
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }
}
