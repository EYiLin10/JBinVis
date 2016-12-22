varying vec2 v_texCoord;
varying vec4 v_color;
uniform sampler2D u_texture;
uniform float u_max;
uniform float u_min;

void main() {
    // calculate frequency based on texture colour
    vec4 temp = texture2D(u_texture, v_texCoord);

    float freq = (temp.g * 65280 + temp.b*255) / u_max;
    if(freq > 0) {
        freq = freq * 0.8 + 0.2;
        if(freq > 1.0) freq = 1.0;
    }
    gl_FragColor = vec4(0.0, freq, 0.0, 1.0);    
}