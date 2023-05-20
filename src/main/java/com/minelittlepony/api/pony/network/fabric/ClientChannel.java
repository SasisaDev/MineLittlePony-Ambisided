package com.minelittlepony.api.pony.network.fabric;

import com.minelittlepony.client.MineLittlePonyClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.network.MsgPonyData;

@Environment(EnvType.CLIENT)
public class ClientChannel {

    private static boolean registered;

    public static void bootstrap() {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
           registered = false;
           MineLittlePonyClient.logger.info("Resetting registered flag");
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            MineLittlePonyClient.logger.info("Sending consent packet to " + handler.getPlayer().getName().getString());

            sender.sendPacket(ChannelConstants.REQUEST_PONY_DATA, PacketByteBufs.empty());
        });

        ClientPlayNetworking.registerGlobalReceiver(ChannelConstants.REQUEST_PONY_DATA, (client, handler, ignored, sender) -> {
            if (client.player != null) {
                IPony pony = IPony.getManager().getPony(client.player);
                registered = true;
                MineLittlePonyClient.logger.info("Server has just consented");

                sender.sendPacket(ChannelConstants.CLIENT_PONY_DATA, new MsgPonyData(pony.metadata(), pony.defaulted()).toBuffer(PacketByteBufs.create()));
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(ChannelConstants.CLIENT_PONY_DATA, (server, player, ignore, buffer, ignore2) -> {
            MsgPonyData packet = new MsgPonyData(buffer);
            server.execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, packet, packet.isNoSkin(), EnvType.SERVER);
            });
        });
    }

    public static void broadcastPonyData(MsgPonyData packet) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Client packet send called by the server");
        }

        MineLittlePonyClient.logger.info("Sending pony skin data over");

        ClientPlayNetworking.send(ChannelConstants.CLIENT_PONY_DATA, packet.toBuffer(PacketByteBufs.create()));
    }
}