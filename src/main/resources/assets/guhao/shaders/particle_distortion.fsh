#version 150

uniform sampler2D DiffuseSampler; // 主颜色纹理
uniform sampler2D DepthSampler;   // 深度纹理
uniform vec2 ScreenSize;          // 屏幕分辨率
uniform float Time;               // 游戏时间
uniform vec3 ParticlePositions[10]; // 最大支持10个粒子源
uniform int ActiveParticles;      // 当前激活粒子数

varying vec2 texCoord;

void main() {
    // 基础颜色
    vec4 color = texture2D(DiffuseSampler, texCoord);

    // 热浪扭曲计算
    float totalDistortion = 0.0;
    for(int i=0; i<ActiveParticles; i++){
        vec3 pos = ParticlePositions[i];
        vec2 screenPos = vec2(pos.x/ScreenSize.x, pos.y/ScreenSize.y);
        float distance = length(texCoord - screenPos);

        // 距离衰减函数
        float intensity = 1.0 / (1.0 + 50.0 * distance*distance);

        // 动态噪声扭曲
        float noise = sin(Time*3.0 + distance*20.0) * 0.005 * intensity;
        totalDistortion += noise;
    }

    // 应用扭曲
    vec2 distortedUV = texCoord + vec2(totalDistortion, totalDistortion*0.7);
    gl_FragColor = texture2D(DiffuseSampler, distortedUV);

    // 添加辉光
    float glow = smoothstep(0.5, 1.0, intensity) * 0.3;
    gl_FragColor.rgb += vec3(0.3, 0.5, 1.0) * glow;
}