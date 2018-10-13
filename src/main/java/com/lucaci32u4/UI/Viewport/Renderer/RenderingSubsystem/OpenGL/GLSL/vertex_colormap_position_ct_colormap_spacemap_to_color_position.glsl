#version 120

attribute ivec4 gl_ColorInput; // Int RBG values, including special colors
attribute ivec2 gl_PositionInput; // Int Object space coordinates

uniform ivec4 colorMap[6]; // Special color mapping buffer
uniform vec4 spaceMap; // offsetX, offsetY, pixelsPerUnit, drawDepth

varying vec4 gl_Color; // Sloat RGB values

void main() {
    if (gl_ColorInput.x > 255) {
        gl_ColorInput = ivec4(colorInput[gl_ColorInput.x - 256], gl_ColorInput.w);
    }
    gl_Color = vec4(gl_ColorMap) / vec4(255.0);
    gl_Position = vec3(
        float(gl_PositionInput.x) - spaceMap.x,
        float(gl_PositionInput.y) - spaceMap.y,
        spaceMap.w
    );
    gl_Position = vec3(
        gl_Position.xy * vec2(spaceMap.z),
        spaceMap.z
    );
}
