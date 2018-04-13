varying vec2 v_texCoord;
varying vec4 v_color;
uniform sampler2D u_front;
uniform sampler2D u_back;
uniform sampler3D u_volume;

vec4 getColor(vec3 coord) {
    vec4 clr = texture3D(u_volume, coord);
    return clr;
}

vec4 blendColor(vec4 c1, vec4 c2) {
    vec3 res = clamp(c1.rgb + 0.65*c2.rgb, 0,1);
    return vec4(res,1);
}

void main() {
    vec3 front = texture2D(u_front, v_texCoord).rgb;
    vec3 back = texture2D(u_back, v_texCoord).rgb;
    vec3 dir = back-front;
    float totalDist = length(dir);
    if(totalDist < 0.000001) {
        gl_FragColor = vec4(0,0,0,1); //colour of the space outside the cube
        return;
    }
   
    float stepSize = 0.025 / totalDist;
    float t = stepSize;

    vec3 curPos = front;
    vec4 curColor = getColor(curPos);
    vec4 nexColor;

    while(t < 1) {
        nexColor = getColor(curPos);
        curColor = blendColor(curColor, nexColor);
        
        t = t+stepSize;
        curPos = front + t * dir;
    }  

    curPos = back;
    nexColor = getColor(curPos);
    curColor = blendColor(curColor, nexColor);

    gl_FragColor = vec4(curColor.rgb,1);
}
