package com.minelittlepony.api.pony.network.fabric;

import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.server.MineLittlePonyServer;
import com.minelittlepony.server.pony.ServerPonyData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

@Environment(EnvType.SERVER)
public class ServerChannel {

    private static boolean registered;

    public static void bootstrap() {
        registered = true;

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            MineLittlePonyServer.logger.info("Sending consent packet to " + handler.getPlayer().getName().getString());

            sender.sendPacket(ChannelConstants.REQUEST_PONY_DATA, PacketByteBufs.empty());
        });

        ServerPlayNetworking.registerGlobalReceiver(ChannelConstants.CLIENT_PONY_DATA, (server, player, ignore, buffer, ignore2) -> {
            ServerPonyData packet = new ServerPonyData(buffer);
            server.execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, packet, false, EnvType.SERVER);
            });
        });
    }
}