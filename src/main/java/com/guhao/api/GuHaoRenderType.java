package com.guhao.api;

import com.google.common.collect.Maps;
import com.guhao.client.BloomSpaceParticleRenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class GuHaoRenderType {
    public static final HashMap<ResourceLocation, BloomSpaceParticleRenderType> BloomSpaceRenderTypes = Maps.newHashMap();
    private static int bloomIdx = 0;

    public static BloomSpaceParticleRenderType getBloomSpaceRenderTypeByTexture(ResourceLocation texture) {
        // 使用层级1（同时启用空间破碎和辉光效果）
        return BloomSpaceParticleRenderType.getOrCreate(texture, 1);
    }
}

