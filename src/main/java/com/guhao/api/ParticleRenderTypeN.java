package com.guhao.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

public interface ParticleRenderTypeN {

    ParticleRenderType PARTICLE_SHEET_LIT_NO_CULL = new ParticleRenderType() {
        public void begin(BufferBuilder p_107462_, @NotNull TextureManager p_107463_) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableCull();
            RenderSystem.texParameter(3553, 10242, 33071);
            RenderSystem.texParameter(3553, 10243, 33071);
            RenderSystem.texParameter(3553, 10240, 9729);
            RenderSystem.texParameter(3553, 10241, 9729);
            p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }


        public void end(Tesselator p_107465_) {
            p_107465_.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT_NO_CULL";
        }
    };

    ParticleRenderType PARTICLE_SHEET_LIT_NO_CULL2 = new ParticleRenderType() {
        public void begin(BufferBuilder p_107462_, @NotNull TextureManager p_107463_) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableCull();
            p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }


        public void end(Tesselator p_107465_) {
            p_107465_.end();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT_NO_CULL2";
        }
    };
}

