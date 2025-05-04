// red_black_contour.fsh
#version 150

uniform sampler2D DiffuseSampler; // 颜色纹理
uniform sampler2D DepthSampler;   // 深度纹理
uniform float ViewFarPlane;       // 远平面距离

in vec2 uv;
out vec4 fragColor;

// 深度值转换为线性距离
float linearizeDepth(float depth) {
    return (2.0 * ViewFarPlane) / (ViewFarPlane + 1.0 - depth * (ViewFarPlane - 1.0));
}

void main() {
    // 获取当前像素深度
    float depth = texture(DepthSampler, uv).r;
    float currentDepth = linearizeDepth(depth);

    // 边缘检测（Sobel算子）
    float depthDiff = 0.0;
    const vec2 offset = 1.0 / vec2(1024.0, 768.0); // 根据分辨率调整
    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            float sampleDepth = linearizeDepth(texture(DepthSampler, uv + offset * vec2(x,y)).r);
            depthDiff += abs(currentDepth - sampleDepth);
        }
    }
    depthDiff /= 9.0; // 平均值

    // 颜色处理逻辑
    if (depthDiff > 0.15) { // 边缘阈值
        fragColor = vec4(0.0, 0.0, 0.0, 1.0); // 轮廓黑色
    } else if (depth > 0.999) { // 背景判断
        fragColor = vec4(0.8, 0.0, 0.0, 1.0); // 背景红色
    } else {
        fragColor = vec4(0.8, 0.0, 0.0, 1.0); // 非轮廓区域红色
    }
}