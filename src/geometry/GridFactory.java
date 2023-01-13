package geometry;

import lwjglutils.OGLBuffers;

public class GridFactory {

	static public OGLBuffers make(int m, int n) {
		float[] vertexBuffer = new float[2 * m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				vertexBuffer[(i * n + j) * 2] = (float) i / (m - 1);
				vertexBuffer[(i * n + j) * 2 + 1] = (float) j / (n - 1);
				// System.out.println(vertexBuffer[i*m+j] + " " +
				// vertexBuffer[i*m+j+1]);
			}
		}

		int[] indexBuffer = new int[6 * (m - 1) * (n - 1)];
		int z = 0;
		for (int j = 0; j < m - 1; j++)
			for (int k = 0; k < n - 1; k++) {
				int i = j * n + k;

				indexBuffer[z] = i;
				indexBuffer[z + 1] = i + n;
				indexBuffer[z + 2] = i + n + 1;
				indexBuffer[z + 3] = i;
				indexBuffer[z + 4] = i + n + 1;
				indexBuffer[z + 5] = i + 1;
				z += 6;
			}

		OGLBuffers.Attrib[] attributes = { 
				new OGLBuffers.Attrib("inPosition", 2)
				//,	new OGLBuffers.Attrib("inNormal", 3)
				//,	new OGLBuffers.Attrib("inTextureCoordinates", 2)
				};
		

		return new OGLBuffers(vertexBuffer, attributes, indexBuffer);
	}

}
