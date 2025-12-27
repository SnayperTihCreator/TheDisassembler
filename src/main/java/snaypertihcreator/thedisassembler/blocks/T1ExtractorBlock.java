package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class T1ExtractorBlock extends ExtractorBlock{
    public static final BooleanProperty SOURCE_HEAT = BooleanProperty.create("source_het");

    public T1ExtractorBlock(){
        super(TierExtractor.PRIMITIVE);

        registerDefaultState(stateDefinition.any().setValue(SOURCE_HEAT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SOURCE_HEAT);
    }
}
