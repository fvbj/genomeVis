#version 330
in vec4 inPosition;
in vec4 inColor;
out vec4 color;
uniform mat4 matMVP;

void main() {
	//color = vec4((inPosition.xyz +1.) / 2., 1.);
	color = inColor;
	gl_Position = matMVP * vec4(inPosition.xyz, 1.0);
}
