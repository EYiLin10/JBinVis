varying vec3 v_position;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    v_position = gl_Vertex.xyz;
}
