varying vec2 v_texCoord;
varying vec4 v_color;
uniform sampler2D u_texture;

void main() {
    vec4 amt = texture2D(u_texture, v_texCoord);
    gl_FragColor = vec4(amt.rgb,1);
}