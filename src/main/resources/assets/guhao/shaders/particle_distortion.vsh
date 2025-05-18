#version 150

// 输入顶点属性 (由Minecraft自动提供)
in vec4 Position; // 顶点位置 (已包含在NDC坐标系)
in vec2 UV0;      // 纹理坐标
in vec4 Color;    // 顶点颜色

// 输出到片段着色器的变量
out vec2 texCoord;
out vec4 vertexColor;

// Uniforms (可选)
uniform mat4 ModelViewMat; // 模型视图矩阵
uniform mat4 ProjMat;       // 投影矩阵

void main() {
    // 直接将位置传递给gl_Position
    gl_Position = ProjMat * ModelViewMat * Position;

    // 传递纹理坐标（根据Minecraft的UV坐标系）
    texCoord = UV0;

    // 传递顶点颜色（如果需要）
    vertexColor = Color;
}