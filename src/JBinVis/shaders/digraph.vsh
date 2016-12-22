varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	v_texCoord = vec2(gl_MultiTexCoord0);
        v_color = gl_Color;
}
