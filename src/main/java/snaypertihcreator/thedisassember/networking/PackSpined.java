package snaypertihcreator.thedisassember.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import snaypertihcreator.thedisassember.menus.Tier1DisassemblerMenu;

import java.util.function.Supplier;

public class PackSpined {
    public PackSpined() {}
    public PackSpined(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof Tier1DisassemblerMenu menu) menu.entity.spined();
        });
        return true;
    }
}
