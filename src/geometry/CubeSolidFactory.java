package geometry;

import lwjglutils.OGLBuffers;
import transforms.Point3D;

public class CubeSolidFactory {

	static public OGLBuffers make() {
		float[] cube = {
				// bottom (z-) face
				1, 0, 0,	1,   0, 0, -1, 	1, 0,
				0, 0, 0,	1,   0, 0, -1,	0, 0,
				1, 1, 0,	1,   0, 0, -1,	1, 1,
				0, 1, 0,	1,   0, 0, -1,	0, 1,
				// x- face
				0, 1, 0,	1,   -1, 0, 0,	1, 0,
				0, 0, 0,	1,   -1, 0, 0,	0, 0,
				0, 1, 1,	1,   -1, 0, 0,	1, 1,
				0, 0, 1,	1,   -1, 0, 0,	0, 1,
				// y- face
				1, 0, 0,	1,   0, -1, 0,	1, 0,
				1, 0, 1,	1,   0, -1, 0,	1, 1,
				0, 0, 0,	1,   0, -1, 0,	0, 0,
				0, 0, 1,	1,   0, -1, 0,	0, 1,
				// top (z+) face
				1, 0, 1,	1,   0, 0, 1,	1, 0,
				1, 1, 1,	1,   0, 0, 1,	1, 1,
				0, 0, 1,	1,   0, 0, 1,	0, 0,
				0, 1, 1,	1,   0, 0, 1,	0, 1,
				// x+ face
				1, 1, 0,	1,   1, 0, 0,	1, 0,
				1, 1, 1,	1,   1, 0, 0,	1, 1,
				1, 0, 0,	1,   1, 0, 0,	0, 0,
				1, 0, 1,	1,   1, 0, 0,	0, 1,
				// y+ face
				1, 1, 0,	1,   0, 1, 0,	1, 0,
				0, 1, 0,	1,   0, 1, 0,	0, 0,
				1, 1, 1,	1,   0, 1, 0,	1, 1,
				0, 1, 1,	1,   0, 1, 0,	0, 1

		};
		int recSize = 9;
		float[] vertexBufferData = new float[24*recSize];
		for(int i=0; i<24; i++){
			vertexBufferData[i*recSize] = cube[i*recSize];
			vertexBufferData[i*recSize + 1] = cube[i*recSize + 1];
			vertexBufferData[i*recSize + 2] = cube[i*recSize + 2];
			vertexBufferData[i*recSize + 3] = 1;
			vertexBufferData[i*recSize + 4] =  (cube[i*recSize + 4] ==-1) ? 0.5f : cube[i*recSize + 6] ;	//red
			vertexBufferData[i*recSize + 5] =  (cube[i*recSize + 5] ==-1) ? 0.5f : cube[i*recSize + 6] ;	//green
			vertexBufferData[i*recSize + 6] =  (cube[i*recSize + 6] ==-1) ? 0.5f : cube[i*recSize + 6] ;	//blue
			vertexBufferData[i*recSize + 7] =  0.5f ; //alpha
		}


		int[] indexBufferData = new int[36];
		int k = 0;
		for (int i = 0; i<6; i++){
			indexBufferData[k++] = i*4;
			indexBufferData[k++] = i*4 + 1;
			indexBufferData[k++] = i*4 + 2;
			indexBufferData[k++] = i*4 + 2;
			indexBufferData[k++] = i*4 + 1;
			indexBufferData[k++] = i*4 + 3;

		}


		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 4),
				new OGLBuffers.Attrib("inColor", 3),
				new OGLBuffers.Attrib("inUV", 2)
		};
		return new OGLBuffers(vertexBufferData, attributes, indexBufferData);
	}
}