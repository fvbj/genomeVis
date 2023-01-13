package geometry;

import lwjglutils.OGLBuffers;

public class AxisFactory {

	static public OGLBuffers make() {
		float[] axis = {
				0, 0, 0, 	1,		1, 0, 0, 1,
				1, 0, 0, 	1,		1, 0, 0, 1,
				0, 0, 0,	1,		0, 1, 0, 1,
				0, 1, 0, 	1,		0, 1, 0, 1,
				0, 0, 0,	1,		0, 0, 1, 1,
				0, 0, 1, 	1,		0, 0, 1, 1,

		};

		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 4), 
				new OGLBuffers.Attrib("inColor", 4) };

		return new OGLBuffers(axis, attributes, null);
	}
}