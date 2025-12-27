package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class T2ExtractorBlock extends ExtractorBlock{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public T2ExtractorBlock() {
        super(TierExtractor.COAL);
        registerDefaultState(stateDefinition.any()
                .setValue(OPEN, false).setValue(LIT, false)
        );
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, LIT);
    }
}
