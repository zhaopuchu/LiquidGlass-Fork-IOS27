/*
   Copyright 2025 Kyant

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.linrjk.liquid.internal

import org.intellij.lang.annotations.Language

@Language("AGSL")
private const val RoundedRectSDF = """
float radiusAt(float2 coord, float4 radii) {
    if (coord.x >= 0.0) {
        if (coord.y <= 0.0) return radii.y;
        else return radii.z;
    } else {
        if (coord.y <= 0.0) return radii.x;
        else return radii.w;
    }
}

float sdRoundedRect(float2 coord, float2 halfSize, float radius) {
    float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
    float outside = length(max(cornerCoord, 0.0)) - radius;
    float inside = min(max(cornerCoord.x, cornerCoord.y), 0.0);
    return outside + inside;
}

float2 gradSdRoundedRect(float2 coord, float2 halfSize, float radius) {
    float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
    if (cornerCoord.x >= 0.0 || cornerCoord.y >= 0.0) {
        return sign(coord) * normalize(max(cornerCoord, 0.0));
    } else {
        float gradX = step(cornerCoord.y, cornerCoord.x);
        return sign(coord) * float2(gradX, 1.0 - gradX);
    }
}"""

@Language("AGSL")
private const val RimFactor = """
float rimFactor(float2 coord, float2 rectSize, float4 radii, float lightAngle) {
    float2 halfSize = rectSize * 0.5;
    float2 centeredCoord = coord - halfSize;
    float radius = radiusAt(centeredCoord, radii);

    float gradRadius = min(radius * 1.5, min(halfSize.x, halfSize.y));
    float2 grad = gradSdRoundedRect(centeredCoord, halfSize, gradRadius);
    float2 normal = float2(cos(lightAngle), sin(lightAngle));
    return clamp(dot(grad, normal), -1.0, 1.0);
}

float smoothRimRamp(float rim) {
    float position = clamp(rim * 0.5 + 0.5, 0.0, 1.0);
    return position * position * position * (position * (position * 6.0 - 15.0) + 10.0);
}"""

/**
 * iOS 27 描边沿周长的强度分布：正上/正下最强，左右两侧为 0。
 * 高光和暗边淡出共用它，保证两者相位严格一致。
 *
 * falloff 决定延伸范围（越大越向正上正下收窄），gain 决定核心亮度：
 * 先按幂次衰减再乘 gain 并截断到 1，核心会形成一段饱和的亮带。
 * 两者分开才能同时做到「范围窄」和「核心亮」。
 */
@Language("AGSL")
private const val Ios27EdgeIntensity = """
float ios27EdgeIntensity(float2 coord, float2 rectSize, float4 radii, float falloff, float gain) {
    float2 halfSize = rectSize * 0.5;
    float2 centeredCoord = coord - halfSize;
    float radius = radiusAt(centeredCoord, radii);
    float gradRadius = min(radius * 1.5, min(halfSize.x, halfSize.y));
    float2 grad = gradSdRoundedRect(centeredCoord, halfSize, gradRadius);
    float ramp = pow(clamp(abs(grad.y), 0.0, 1.0), falloff);
    return clamp(ramp * gain, 0.0, 1.0);
}"""

@Language("AGSL")
internal const val RoundedRectRefractionShaderString = """
uniform shader content;

uniform float2 size;
uniform float2 offset;
uniform float4 cornerRadii;
uniform float refractionHeight;
uniform float refractionAmount;
uniform float depthEffect;

$RoundedRectSDF

float circleMap(float x) {
    return 1.0 - sqrt(1.0 - x * x);
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 centeredCoord = (coord + offset) - halfSize;
    float radius = radiusAt(coord, cornerRadii);
    
    float sd = sdRoundedRect(centeredCoord, halfSize, radius);
    if (-sd >= refractionHeight) {
        return content.eval(coord);
    }
    sd = min(sd, 0.0);
    
    float d = circleMap(1.0 - -sd / refractionHeight) * refractionAmount;
    float gradRadius = min(radius * 1.5, min(halfSize.x, halfSize.y));
    float2 grad = normalize(gradSdRoundedRect(centeredCoord, halfSize, gradRadius) + depthEffect * normalize(centeredCoord));
    
    float2 refractedCoord = coord + d * grad;
    return content.eval(refractedCoord);
}"""

@Language("AGSL")
internal val RoundedRectRefractionWithDispersionShaderString = """
uniform shader content;

uniform float2 size;
uniform float2 offset;
uniform float4 cornerRadii;
uniform float refractionHeight;
uniform float refractionAmount;
uniform float depthEffect;
uniform float chromaticAberration;

$RoundedRectSDF

float circleMap(float x) {
    return 1.0 - sqrt(1.0 - x * x);
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 centeredCoord = (coord + offset) - halfSize;
    float radius = radiusAt(coord, cornerRadii);
    
    float sd = sdRoundedRect(centeredCoord, halfSize, radius);
    if (-sd >= refractionHeight) {
        return content.eval(coord);
    }
    sd = min(sd, 0.0);
    
    float d = circleMap(1.0 - -sd / refractionHeight) * refractionAmount;
    float gradRadius = min(radius * 1.5, min(halfSize.x, halfSize.y));
    float2 grad = normalize(gradSdRoundedRect(centeredCoord, halfSize, gradRadius) + depthEffect * normalize(centeredCoord));
    
    float2 refractedCoord = coord + d * grad;
    float dispersionIntensity = chromaticAberration * ((centeredCoord.x * centeredCoord.y) / (halfSize.x * halfSize.y));
    float2 dispersedCoord = d * grad * dispersionIntensity;
    
    half4 color = half4(0.0);
    
    half4 red = content.eval(refractedCoord + dispersedCoord);
    color.r += red.r / 3.5;
    color.a += red.a / 7.0;
    
    half4 orange = content.eval(refractedCoord + dispersedCoord * (2.0 / 3.0));
    color.r += orange.r / 3.5;
    color.g += orange.g / 7.0;
    color.a += orange.a / 7.0;
    
    half4 yellow = content.eval(refractedCoord + dispersedCoord * (1.0 / 3.0));
    color.r += yellow.r / 3.5;
    color.g += yellow.g / 3.5;
    color.a += yellow.a / 7.0;
    
    half4 green = content.eval(refractedCoord);
    color.g += green.g / 3.5;
    color.a += green.a / 7.0;
    
    half4 cyan = content.eval(refractedCoord - dispersedCoord * (1.0 / 3.0));
    color.g += cyan.g / 3.5;
    color.b += cyan.b / 3.0;
    color.a += cyan.a / 7.0;
    
    half4 blue = content.eval(refractedCoord - dispersedCoord * (2.0 / 3.0));
    color.b += blue.b / 3.0;
    color.a += blue.a / 7.0;
    
    half4 purple = content.eval(refractedCoord - dispersedCoord);
    color.r += purple.r / 7.0;
    color.b += purple.b / 3.0;
    color.a += purple.a / 7.0;
    
    return color;
}"""

@Language("AGSL")
internal const val DefaultHighlightShaderString = """
uniform float2 size;
uniform float4 cornerRadii;
layout(color) uniform half4 color;
uniform float angle;
uniform float falloff;
uniform float ambient;
uniform float edgeBlend;

$RoundedRectSDF
$RimFactor

half4 main(float2 coord) {
    float d = rimFactor(coord, size, cornerRadii, angle);
    float symmetricIntensity = pow(abs(d), falloff);
    float directionalIntensity = pow(smoothRimRamp(-d), falloff);
    float rimIntensity = mix(symmetricIntensity, directionalIntensity, edgeBlend);
    float intensity = mix(ambient, 1.0, rimIntensity);
    return color * intensity;
}"""

@Language("AGSL")
internal const val IOS27HighlightShaderString = """
uniform float2 size;
uniform float4 cornerRadii;
layout(color) uniform half4 color;
uniform float falloff;
uniform float gain;

$RoundedRectSDF
$Ios27EdgeIntensity

half4 main(float2 coord) {
    return color * ios27EdgeIntensity(coord, size, cornerRadii, falloff, gain);
}"""

@Language("AGSL")
internal const val IOS27DarkEdgeShaderString = """
uniform float2 size;
uniform float4 cornerRadii;
layout(color) uniform half4 color;
uniform float falloff;
uniform float gain;
uniform float fade;

$RoundedRectSDF
$Ios27EdgeIntensity

half4 main(float2 coord) {
    float intensity = ios27EdgeIntensity(coord, size, cornerRadii, falloff, gain);
    return color * (1.0 - fade * intensity);
}"""

@Language("AGSL")
internal const val DarkEdgeShaderString = """
uniform float2 size;
uniform float4 cornerRadii;
layout(color) uniform half4 color;
uniform float angle;
uniform float directionality;
uniform float falloff;

$RoundedRectSDF
$RimFactor

half4 main(float2 coord) {
    float d = abs(rimFactor(coord, size, cornerRadii, angle));
    float shade = 1.0 - pow(d, falloff);
    float intensity = mix(1.0 - directionality, 1.0, shade);
    return color * intensity;
}"""
