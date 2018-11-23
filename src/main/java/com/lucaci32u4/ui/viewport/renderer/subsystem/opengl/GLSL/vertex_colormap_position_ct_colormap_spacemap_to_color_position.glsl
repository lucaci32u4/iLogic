/*
 * iSignal - Digital circuit simulator
 * Copyright (C) 2018-present Iercosan-Lucaci Alexandru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *    ||=============================================||
 *    ||     _  _____  _          _            _     ||
 *    ||    (_)/  ___|(_)       =)_)-         | |    ||
 *    ||     _ \ `--.  _   __ _  _ __    __ _ | |    ||
 *    ||    | | `--. \| | / _` || '_ \  / _` || |    ||
 *    ||    | |/\__/ /| || (_| || | | || (_| || |    ||
 *    ||    |_|\____/ |_| \__, ||_| |_| \__,_||_|    ||
 *    ||                   __/ |                     ||
 *    ||                  |___/  Digital Simulator   ||
 *    ||=============================================||
 */

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
