#version 330
layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
uniform sampler2D textureID;
uniform vec4 data;
uniform vec3 color;
uniform vec2 textTextureSize;

in vec2 texCoord;
in vec3 normal;
flat in  vec4 inputData;

out float gl_FragDepth ;

vec3 light = normalize(-vec3(0.9, 0.9, 1.0));
float diffuse, spec, size;

void main() {
	//index in texture of text
	int w = int(data.w);
	vec2 texCoordBase = vec2(w / int(textTextureSize.x), w % int(textTextureSize.x));
	texCoordBase = vec2(0,1 ) + vec2(1, -1) * (texCoordBase.xy + texCoord.xy) / textTextureSize.yx;

	outColor0.rgb = color;
	// alpha (opacity)
	outColor0.a = 1.0;

	vec4 text = texture(textureID, texCoordBase);

	// calculate lighting
	diffuse = max(0.0, dot(light, normalize(normal)));
	vec3 eye = vec3 (0.0, 0.0, 1.0);
	vec3 halfVector = normalize(eye + light);
	spec = max(pow(dot(normalize(normal), halfVector), 5.0), 0.0);
	outColor0.rgb = color * (diffuse + 0.3) + vec3(1.0, 1., 1.) * spec;


	if (length(text.a)>0.3)
		outColor0 = vec4(text.rgb,1); //render text

	outColor1.rgb = vec3((data.w+1)/1000.); //get ID of selected sphere
}
