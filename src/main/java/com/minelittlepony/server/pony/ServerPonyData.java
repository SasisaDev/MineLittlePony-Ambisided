package com.minelittlepony.server.pony;

import com.google.common.base.MoreObjects;
import com.google.common.base.Suppliers;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.common.util.animation.Interpolator;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerPonyData implements IPonyData {

    private static final short API_IDENTIFIER = (short) 0xABCD;
    // API version - increment this number before any time any data is added/removed/moved in the data stream
    private static final byte API_VERSION = 2;

    private final Race race;
    private final TailLength tailLength;
    private final TailShape tailShape;
    private final Gender gender;


    private final Supplier<Map<String, TriggerPixelType<?>>> triggerPixels = Suppliers.memoize(() -> Util.make(new TreeMap<>(), this::initTriggerPixels));

    private void initTriggerPixels(Map<String, TriggerPixelType<?>> map) {
        map.put("race", race);
        map.put("gender", gender);
    }

    public ServerPonyData(PacketByteBuf buffer) {
        short data = buffer.readShort();
        if (data != API_IDENTIFIER || buffer.readByte() != API_VERSION) {
            race = null;
            tailShape = null;
            tailLength = null;
            gender = null;
            return;
        }
        race = buffer.readEnumConstant(Race.class);
        tailLength = buffer.readEnumConstant(TailLength.class);
        tailShape = buffer.readEnumConstant(TailShape.class);
        gender = buffer.readEnumConstant(Gender.class);

    }

    public ServerPonyData(IPonyData data, boolean noSkin) {
        race = data.getRace();
        gender = data.getGender();
        tailLength = data.getTailLength();
        tailShape = data.getTailShape();
    }

    public PacketByteBuf toBuffer(PacketByteBuf buffer) {
        buffer.writeShort(API_IDENTIFIER);
        buffer.writeByte(API_VERSION);
        buffer.writeEnumConstant(race);
        buffer.writeEnumConstant(gender);

        return buffer;
    }

    @Override
    public Race getRace() {
        return race;
    }

    @Override
    public TailLength getTailLength() {
        return null;
    }

    @Override
    public TailShape getTailShape() {
        return null;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Size getSize() {
        return null;
    }

    @Override
    public int getGlowColor() {
        return 0;
    }

    @Override
    public Wearable[] getGear() {
        return null;
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return false;
    }

    @Override
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }

    @Override
    public Map<String, TriggerPixelType<?>> getTriggerPixels() {
        return triggerPixels.get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .toString();
    }

    private static final class MsgSize implements Size {

        private final int ordinal;
        private final String name;
        private final float shadow;
        private final float scale;
        private final float eyeHeight;
        private final float eyeDistance;
        private final int triggerPixel;

        MsgSize(Size size) {
            ordinal = size.ordinal();
            name = size.name();
            shadow = size.getShadowSize();
            scale = size.getScaleFactor();
            eyeHeight = size.getEyeHeightFactor();
            eyeDistance = size.getEyeDistanceFactor();
            triggerPixel = size.getColorCode();
        }

        MsgSize(PacketByteBuf buffer) {
            ordinal = buffer.readInt();
            name = buffer.readString(32767);
            shadow = buffer.readFloat();
            scale = buffer.readFloat();
            eyeHeight = buffer.readFloat();
            eyeDistance = buffer.readFloat();
            triggerPixel = buffer.readInt();
        }

        public void toBuffer(PacketByteBuf buffer) {
            buffer.writeInt(ordinal);
            buffer.writeString(name);
            buffer.writeFloat(shadow);
            buffer.writeFloat(scale);
            buffer.writeFloat(eyeHeight);
            buffer.writeFloat(eyeDistance);
            buffer.writeFloat(triggerPixel);
        }

        @Override
        public int ordinal() {
            return ordinal;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public float getShadowSize() {
            return shadow;
        }

        @Override
        public float getScaleFactor() {
            return scale;
        }

        @Override
        public float getEyeHeightFactor() {
            return eyeHeight;
        }

        @Override
        public float getEyeDistanceFactor() {
            return eyeDistance;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int getColorCode() {
            return triggerPixel;
        }
    }
}
