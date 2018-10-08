#version 120

varying vec4 gl_Color; // Float RGB values

uniform uint pickerID; // ID for picking as 4x(8-bit) elements

out vec4 gl_FragColor; // Interpolated utput color of the fragment

void main() {
    gl_FragColor = gl_Color;
}
