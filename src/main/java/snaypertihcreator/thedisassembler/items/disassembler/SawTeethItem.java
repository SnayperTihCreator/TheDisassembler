package snaypertihcreator.thedisassembler.items.disassembler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.utils.WordGender;

import java.util.List;

// Класс компонент - зубье пилы
public class SawTeethItem extends Item implements IMaterialComponent{
    private final SawMaterial material;

    public SawTeethItem(SawMaterial material) {
        super(new Properties());
        this.material = material;
    }

    @Override
    public SawMaterial getMaterial() {return material;}
    @Override
    public WordGender getGender() {return WordGender.PLURAL;}
    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {return getMaterialName();}

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level p_41422_, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.thedisassembler.teeth.description"));
        tooltip.add(Component.translatable("tooltip.thedisassembler.luckMod", Math.round(material.getLuckModifier()*100)));
    }
}
