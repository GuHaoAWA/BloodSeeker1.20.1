#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform float Time;
uniform vec4 ParticleData[10];
uniform int ParticleCount;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    float totalDistortion = 0.0;

    // 应用粒子位置扭曲
    for(int i = 0; i < ParticleCount; i++) {
        vec4 data = ParticleData[i];
        vec2 particleScreenPos = data.xy / ScreenSize;
        float distance = length(uv - particleScreenPos);

        // 距离衰减系数
        float attenuation = 1.0 / (1.0 + 50.0 * distance * distance);

        // 动态扭曲计算
        float wave = sin(Time * 3.0 + distance * 30.0) * 0.005 * attenuation * data.w;
        totalDistortion += wave;
    }

    // 应用热浪效果
    vec2 distortedUV = vec2(
    uv.x + totalDistortion * 1.2,
    uv.y + totalDistortion * 0.8
    );

    // 采样最终颜色
    fragColor = texture(DiffuseSampler, distortedUV);

    // 添加辉光效果
    float glow = smoothstep(0.7, 1.0, fragColor.a) * 0.3;
    fragColor.rgb += vec3(0.3, 0.5, 1.0) * glow;
}