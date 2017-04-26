varying vec3 v_position;

void main() {
    vec3 res = (v_position + 1.0)/2.0;
    vec4 clr =  vec4(res, 1);
    gl_FragColor = clr;
}