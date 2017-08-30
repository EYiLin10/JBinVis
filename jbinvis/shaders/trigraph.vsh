varying vec4 v_color;

uniform vec3 u_camera;
uniform float u_A;
uniform float u_B;

void main() {
    vec4 a = gl_Vertex - vec4(32,32,32,0);
    a = a / 32.0;
    a.w = 1;
    gl_Position = gl_ModelViewProjectionMatrix * a;

    float d = distance(u_camera,gl_Vertex.xyz);
    float psize = u_A/(u_B+d*d);

    v_color = gl_Color;
    
    gl_PointSize = clamp(psize, 0.5f, 10.0f);
}