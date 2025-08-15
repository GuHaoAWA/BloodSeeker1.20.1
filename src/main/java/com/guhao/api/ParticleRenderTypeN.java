package com.guhao.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

@OnlyIn(Dist.CLIENT)
public interface ParticleRenderTypeN {

    ParticleRenderType PARTICLE_SHEET_LIT_NO_CULL = new ParticleRenderType() {
        public void begin(BufferBuilder p_107462_, @NotNull TextureManager p_107463_) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableCull();
            RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }


        public void end(Tesselator p_107465_) {
            p_107465_.end();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT_NO_CULL";
        }
    };

    ParticleRenderType PARTICLE_SHEET_LIT_NO_CULL2 = new ParticleRenderType() {
        public void begin(BufferBuilder p_107462_, @NotNull TextureManager p_107463_) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.disableCull();
            p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }


        public void end(Tesselator p_107465_) {
            p_107465_.end();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT_NO_CULL2";
        }
    };
}

