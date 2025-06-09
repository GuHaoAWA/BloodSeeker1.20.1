#version 150

in vec4 Position;
in vec2 UV0;
out vec2 texCoord;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * Position;
    texCoord = UV0;
}