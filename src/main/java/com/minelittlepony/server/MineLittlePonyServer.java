package com.minelittlepony.server;

import com.minelittlepony.api.pony.network.fabric.PonyDataCallback;
import com.minelittlepony.api.pony.network.fabric.ServerChannel;
import com.minelittlepony.client.MineLittlePonyClient;
import com.minelittlepony.server.pony.ServerPonyManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineLittlePonyServer implements DedicatedServerModInitializer {
    private static MineLittlePonyClient instance;

    private static ServerPonyManager ponyManager;

    public static final Logger logger = LogManager.getLogger("MineLittlePony");
    @Override
    public void onInitializeServer() {
        ServerChannel.bootstrap();

        ponyManager = new ServerPonyManager();

        PonyDataCallback.EVENT.register((sender, data, noSkin, env) -> {
            ponyManager.Assign(sender, data);
        });
    }
}
