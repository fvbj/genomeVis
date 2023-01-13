package geometry;

import lwjglutils.OGLBuffers;

public class CubeWireFactory {

	static public OGLBuffers make() {
		float[] cube = {
				0, 0, 0, 	1,		1, 0, 0, 1,
				1, 0, 0, 	1,		1, 0, 0, 1,
				0, 0, 0,	1,		0, 1, 0, 1,
				0, 1, 0,	1,		0, 1, 0, 1,
				0, 0, 0,	1,		0, 0, 1, 1,
				0, 0, 1,	1,		0, 0, 1, 1,
				1, 1, 1,	1,		1, 1, 1, 1,
				1, 1, 0,	1,		1, 1, 1, 1,
				1, 0, 1,	1,		1, 1, 1, 1,
				0, 1, 1,	1,		1, 1, 1, 1,
				1, 0, 0,	1,		1, 1, 1, 1,
				0, 1, 0,	1,		1, 1, 1, 1,
				0, 0, 1,	1,		1, 1, 1, 1,
				0, 0, 0,	1,		1, 1, 1, 1,

		};

		int[] indexBufferData = new int[24];
		for (int i = 0; i < 6; i++) {
			indexBufferData[i ] = i ;
		}
		int i=5;
		indexBufferData[++i] = 6 ;
		indexBufferData[++i] = 7 ;
		indexBufferData[++i] = 6 ;
		indexBufferData[++i] = 8 ;
		indexBufferData[++i] = 6 ;
		indexBufferData[++i] = 9 ;

		indexBufferData[++i] = 7 ;
		indexBufferData[++i] = 10 ;
		indexBufferData[++i] = 8 ;
		indexBufferData[++i] = 10 ;

		indexBufferData[++i] = 7 ;
		indexBufferData[++i] = 11 ;
		indexBufferData[++i] = 9 ;
		indexBufferData[++i] = 11 ;

		indexBufferData[++i] = 8 ;
		indexBufferData[++i] = 12 ;
		indexBufferData[++i] = 9 ;
		indexBufferData[++i] = 12 ;

		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 4), 
				new OGLBuffers.Attrib("inColor", 4) };

		return new OGLBuffers(cube, attributes, indexBufferData);
	}
}