package com.minelittlepony.server.pony;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.IPonyManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ServerPonyManager implements IPonyManager {

    private Map<UUID, IPony> PlayerPonies = new HashMap<>();

    public ServerPonyManager() {
        Instance.instance = this;
    }

    public void Assign(PlayerEntity entity, IPonyData data)
    {
        PlayerPonies.put(entity.getUuid(), new ServerPony(data));
    }

    @Override
    public Optional<IPony> getPony(@Nullable Entity entity) {
        if(entity instanceof PlayerEntity)
            return Optional.of(getPony((PlayerEntity)entity));

        return Optional.empty();
    }

    @Override
    public IPony getPony(PlayerEntity player) {
        return PlayerPonies.get(player.getUuid());
    }

    @Override
    public IPony getPony(Identifier resource) {
        return null;
    }

    @Override
    public IPony getPony(Identifier resource, UUID uuid) {
        return null;
    }

    @Override
    public IPony getBackgroundPony(UUID uuid) {
        return null;
    }

    @Override
    public void removePony(Identifier resource) {

    }
}
