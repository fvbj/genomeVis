#version 330
uniform mat4 matMVP;
uniform int time;
uniform vec4 data;
uniform vec2 windowSize;
uniform vec2 aziZen;

in vec2 inPosition;
out vec2 texCoord;
out vec3 normal;
flat out vec4 inputData;
const float PI = 3.14159;

vec3 sphere(vec2 grid, float r){
	float a = grid.x * PI * 2.;
	float z = grid.y * PI - PI / 2. ;
	return vec3(r * cos(a) * cos(z),
		r*sin(a) * cos(z),
		r*sin(z));
}

vec3 sphereNormal(vec2 grid, float r){
	float a = grid.x * PI * 2.;
	float z = grid.y * PI - PI / 2.;
	vec3 dx = vec3(-r * sin(a) * cos(z) * PI * 2.,
		r*cos(a) * cos(z) * 2. *PI,
		0
		);
	vec3 dy = vec3(-r * cos(a) * sin(z) * PI,
		-r*sin(a) * sin(z) * PI,
		r*cos(z) * PI
		);
	return cross(normalize(dx), normalize(dy));
}
void main() {

	//vec4 position = matMVP * (vec4(sphere(inPosition, 0.01) * 2.,1.) - vec4(data.xyz,0));
	vec4 position = matMVP * vec4(sphere(inPosition, 0.01) * 2.,1.);
	normal = sphereNormal(inPosition, 0.01);
	gl_Position = position;
	inputData = data;
	texCoord = vec2(inPosition.x * 2 - 0.5, 1. - inPosition.y);

} 
