package com.minelittlepony.server.pony;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import net.minecraft.util.Identifier;

public class ServerPony implements IPony {

    IPonyData metadata;

    public ServerPony(IPonyData data)
    {
        metadata = data;
    }

    @Override
    public boolean defaulted() {
        return false;
    }

    @Override
    public boolean hasMetadata() {
        return true;
    }

    @Override
    public Identifier texture() {
        return null;
    }

    @Override
    public IPonyData metadata() {
        return metadata;
    }
}
