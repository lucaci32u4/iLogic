#version 120

attribute uvec4 gl_ColorInput; // Uint RBG values, including special colors
attribute uvec2 gl_PositionInput; // Uint Object space coordinates

uniform uvec4 colorMap[6]; // Special color mapping buffer
uniform vec4 spaceMap; // offsetX, offsetY, pixelsPerUnit, drawDepth

varying vec4 gl_Color; // Sloat RGB values

void main() {
    if (gl_ColorInput.x > 255) {
        gl_ColorInput = uvec4(colorInput[gl_ColorInput.x - 256], gl_ColorInput.w);
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
