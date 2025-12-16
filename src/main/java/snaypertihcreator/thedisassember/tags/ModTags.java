package snaypertihcreator.thedisassember.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import snaypertihcreator.thedisassember.TheDisassemberMod;

public class ModTags {

    public static TagKey<Block> registerBlock(String name){
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, name));
    }

    public static TagKey<Item> registerItem(String name){
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, name));
    }

    public static final TagKey<Item> SAWS = registerItem("saws");
    public static final TagKey<Item> TEETHS = registerItem("teeths");
    public static final TagKey<Item> BLADES = registerItem("blades");
}
