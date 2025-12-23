package snaypertihcreator.thedisassembler.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import snaypertihcreator.thedisassembler.blocksEntity.Tier1DisassemblerBlockEntity;
import snaypertihcreator.thedisassembler.menus.Tier1DisassemblerMenu;

import java.util.function.Supplier;

/**
 * Это метод для бармолды
 */
public class PackSpined {
    public PackSpined() {}
    public PackSpined(FriendlyByteBuf ignoredBuf) {}
    public void toBytes(FriendlyByteBuf ignoredBuf) {}

    @SuppressWarnings("UnusedReturnValue")
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof Tier1DisassemblerMenu menu){
                if (menu.entity instanceof Tier1DisassemblerBlockEntity entity) entity.spined();
            }
        });
        return true;
    }
}
