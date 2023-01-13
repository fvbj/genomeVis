import dataModel.Group;
import dataModel.Record;
import dataModel.tree.BinaryTree;
import dataModel.tree.IndexColor;
import dataModel.tree.NewickTree;
import dataModel.tree.NodeNewick;
import geometry.AxisFactory;
import geometry.CubeSolidFactory;
import geometry.GridFactory;
import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static dataModel.GroupMaker.getGroups;
import static dataModel.RecordReader.readCSV;
import static geometry.LinesGeometriesFactory.*;
import static geometry.TextureLabel.createTextureFromCSVRecord;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL33.*;


/**
 * genomeVis:<br/>
 * 
 * @author PGRF FIM UHK
 * @version 1.0
 * @since 2022-09-05
 */

public class Renderer extends AbstractRenderer {



	private boolean mouseButton1 = false;
	private double ox, oy;
	private boolean pick = false;

	private OGLBuffers buffersLine, buffersTree, cube, ball, axis;
	private OGLTextRenderer textRenderer;
	private OGLTexture.Viewer textureViewer;
	private OGLTexture2D fontTexture;
	private OGLRenderTarget renderTarget;
	private TargetViewer targetViewer;

	private int sphereProgram, locMat3, locData, locColor, locViewA, locTextTextureSize;
	private int lineProgram, locMat2;
	private double step = .01;
	//List<List<String>> record;
	private List<Group> groups;
	private HashMap<String, NodeNewick> species;
	private List<Record> dataRecords= new ArrayList<>();
	private Camera cam = new Camera();
	private Vec3D cameraPosition;
	private Mat4 proj; // created in reshape()
	private final Mat4 model = new Mat4Transl( new Vec3D(-0.5)).mul(new Mat4Scale(5));
	private long time;
	private boolean timeUpdate = true, projP = false, help = true;
	private  int backgroundMode = 2, drawMode = 0;
	private int categories = 2;
	private int startIndexColor = 0;
	private int tree = 1;
	private float interpolate = 0;
	private Vec2D maxPosXY;
	private static final float zMin = 0.01f;
	private String speciesTree;
	private String pickedName = "";
	private int pickIndex = -1;
	private Vec2D textTextureSize;
	private final boolean[] showGroup = {true,true,true,true,false,true,true,true,false,false};
	private float size = 1.6f;
	private final String[] speciesNames = {"","fish","mammal","reptile","bird","cnidaria","insect","rotifer","porifera","mollusc","cephalochordate",
			"nematode","annelid","crustacea","arachnid","xenacoelomorph","brachiopod","ctenophore","platyhelmint",
			"chilopod","echinoderm","myxozoa","placozoa"};

	private final GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
	        @Override
	        public void invoke(long window, int w, int h) {
	        	if (w > 0 && h > 0) {
	            	width = w;
					height = h;
					resize();
	        	}
	        }
	    };
	    
	private final GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
			if (action == GLFW_PRESS ){
				switch (key) {
					case GLFW_KEY_PAGE_UP:
						categories++;
						initData(categories);
						break;
					case GLFW_KEY_PAGE_DOWN:
						categories--;
						initData(categories);
						break;
					case GLFW_KEY_INSERT:
						startIndexColor++;
						initData(categories);
						break;
					case GLFW_KEY_DELETE:
						startIndexColor=Math.max(--startIndexColor,0);
						initData(categories);
						break;
					case GLFW_KEY_T:
						tree++;
						tree = tree % 3;
						initData(categories);
						break;
					case GLFW_KEY_0:
						Arrays.fill(showGroup, true);
						break;
					case GLFW_KEY_MINUS:
						Arrays.fill(showGroup, false);
					break;


				}
				if (key > GLFW_KEY_0 && key <= GLFW_KEY_9) {
					int gIndex = key - GLFW_KEY_0 - 1;
					showGroup[gIndex] = !showGroup[gIndex];
				}
			}

			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				switch (key) {
					case GLFW_KEY_W:
						cam = cam.forward(step);
						break;
					case GLFW_KEY_D:
						cam = cam.right(step);
						break;
					case GLFW_KEY_S:
						cam = cam.backward(step);
						break;
					case GLFW_KEY_A:
						cam = cam.left(step);
						break;
					case GLFW_KEY_LEFT_CONTROL:
						cam = cam.down(step);
						break;
					case GLFW_KEY_LEFT_SHIFT:
						cam = cam.up(step);
						break;
					case GLFW_KEY_SPACE:
						cam = cam.withFirstPerson(!cam.getFirstPerson());
						if (!cam.getFirstPerson()) {
							cameraPosition=cam.getPosition();
							cam = cam.withPosition(new Vec3D(0, 0, 0));
						} else {
							cam = cam.withPosition(cameraPosition);
						}
						break;
					case GLFW_KEY_X:
						resetCamera();
						break;
					case GLFW_KEY_R:
						cam = cam.mulRadius(0.9f);
						break;
					case GLFW_KEY_F:
						cam = cam.mulRadius(1.1f);
						break;
					case KeyEvent.VK_P:
						projP = !projP;
						resize();
						break;

					case GLFW_KEY_LEFT:
						interpolate -= 0.1f;
						interpolate = interpolate >= 0 ? interpolate : 0;
						//System.out.println(interpolate);
						break;
					case GLFW_KEY_RIGHT:
						interpolate += 0.1f;
						interpolate = interpolate <= 1 ? interpolate : 1;
						//System.out.println(interpolate);
						break;
					case GLFW_KEY_UP:
						size *= 1.1f;
						size = Math.min(size, 10f);
						break;
					case GLFW_KEY_DOWN:
						size *= 0.9f;
						size = Math.max(size, 0.1f);
						break;
					case GLFW_KEY_V:
						timeUpdate = !timeUpdate;
						break;
					case GLFW_KEY_BACKSPACE:
						backgroundMode++;
						break;
					case GLFW_KEY_H:
						help= !help;
						break;
					case GLFW_KEY_M:
						drawMode++;
						break;
				}
				step +=0.01;
				if (step>2)  step = 1;
			}
			if (action == GLFW_RELEASE ){
				step = 0.01;
			}
		}
	};
	private final GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double x, double y) {
			if (mouseButton1) {
				cam = cam.addAzimuth(Math.PI * (ox - x) / width)
						.addZenith(Math.PI * (oy - y) / width);
				ox = x;
				oy = y;
			}
		}
	};

    private final GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			if (button==GLFW_MOUSE_BUTTON_1){
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double x = xBuffer.get(0);
				double y = yBuffer.get(0);
				if (action == GLFW_PRESS) {
					mouseButton1 = true;
					ox = x;
					oy = y;
				}
				if (action == GLFW_RELEASE) {
					mouseButton1 = false;
					cam = cam.addAzimuth(Math.PI * (ox - x) / width)
							.addZenith(Math.PI * (oy - y) / width);
					ox = x;
					oy = y;
				}
        	}

			if (button==GLFW_MOUSE_BUTTON_2){
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double x = xBuffer.get(0);
				double y = yBuffer.get(0);
				if (action == GLFW_PRESS) {
					ox = x;
					oy = y;
					pick = true;
				}
				if (action == GLFW_RELEASE) {
					pickIndex = -1;
				}

			}
		}

	};
	


	private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		@Override public void invoke (long window, double dx, double dy) {
			if (dy<0)
				cam = cam.mulRadius(0.9f);
			else
				cam = cam.mulRadius(1.1f);

		}
	};
 	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}


	private void resize(){
		if (projP)
			proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 200.0);
		else
			proj = new Mat4OrthoRH(10, 10*height / (double) width, 0.01, 200.0);
		if (textRenderer != null)
			textRenderer.resize(width, height);
		if (renderTarget != null) {
			renderTarget = new OGLRenderTarget(width, height, 2);
			targetViewer = new TargetViewer(renderTarget);
		}
	}

	private static String readIntoString(String filename) {
		StringBuilder buff = new StringBuilder();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) buff.append(line);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buff.toString();
	}


	private void initData(int categories) {
		if (tree == 0) return;
		categories=Math.max(categories,2);
		//categories=Math.min(categories,25);
		NewickTree nt = new NewickTree();
		BinaryTree<NodeNewick> itol = nt.parseNewick(speciesTree);
		nt.calculateDepth(itol, 0, 0);
		int count = 0;
		if (tree == 1){
			nt.calculatePosXYLevel(itol, new Vec2D(), 0);
			//maxPosXY = nt.getMaxPosXY(itol,new Vec2D());
			count = nt.splitByLevel(itol, categories, 0);
		}
		if (tree == 2){
			nt.calculatePosXYDepth(itol, new Vec2D(), 0, 0);
			//maxPosXY = nt.getMaxPosXY(itol,new Vec2D());
			count = nt.splitByDistance(itol, categories/100., 0);
		}
		species = nt.getSpecies(itol.fringe());
		groups = getGroups(itol.fringe(),count + 1);
		System.out.println(itol);
		//System.out.println(maxPosXY);
		maxPosXY = new Vec2D();
		for (int i = 0; i < dataRecords.size(); i++) {
			Record r = dataRecords.get(i);

			Point3D p = r.position;

			String[] labels = r.name.split(" ");
			String label = labels[0];
			if (species.containsKey(label) ){
				//System.out.println(label);
				NodeNewick nn = species.get(label);
				{
					r.positionTree = new Point3D(nn.posXY.getX(),
							nn.posXY.getY(), 0.2);
					r.groupID = nn.groupId;
					Group g = groups.get(r.groupID);
					g.center = g.center.add(p.ignoreW());
					g.count++;
					maxPosXY = new Vec2D(Math.max(maxPosXY.getX(), nn.posXY.getX()),
							Math.max(maxPosXY.getY(), nn.posXY.getY()));
				}
			}

		}
		System.out.println("\nGroups size:" + groups.size());
		int sum=0;
		for (Group g:groups) {
			if (g.count>0) {
				sum++;
				g.center = g.center.mul(1. / g.count);
				System.out.println(g);
			}
		}
		System.out.println("Groups used:" + sum);
		System.out.println();

		buffersTree = createBuffersTreeFromCSVrecord(itol, maxPosXY, zMin, categories/100.f);
		buffersLine = createBuffersLineFromCSVRecord (dataRecords, groups, species, startIndexColor, maxPosXY, zMin);

	}

	private void resetCamera(){
		cam = cam.withPosition(new Vec3D(5, 5, 2.5))
				.withAzimuth(Math.PI * 1.25)
				.withZenith(Math.PI * -0.125)
				.withRadius(8);

	}

	@Override
	public void init() {
		OGLUtils.shaderCheck();

		OGLUtils.printOGLparameters();
		OGLUtils.printLWJLparameters();
		OGLUtils.printJAVAparameters();


		dataRecords = readCSV("data/species.csv");

		speciesTree  = readIntoString("data/speciesTree.txt");

		initData(categories);
		tree = 0;

		//create texture with text
		textTextureSize = new Vec2D(16,32);
		fontTexture = createTextureFromCSVRecord(dataRecords, textTextureSize);
		//for bounding box
		cube = CubeSolidFactory.make();
		axis = AxisFactory.make();
		//for sphere
		ball = GridFactory.make(20,20);

		textRenderer = new OGLTextRenderer(width, height, new Font("SansSerif", Font.PLAIN, 20) );
		textureViewer = new OGLTexture2D.Viewer();

		lineProgram = ShaderUtils.loadProgram("/shaders/line");
		sphereProgram = ShaderUtils.loadProgram("/shaders/sphere");


		locMat2 = glGetUniformLocation(lineProgram, "matMVP");
		locMat3 = glGetUniformLocation(sphereProgram, "matMVP");
		locData = glGetUniformLocation(sphereProgram, "data");
		locColor = glGetUniformLocation(sphereProgram, "color");
		locViewA = glGetUniformLocation(sphereProgram, "aziZen");
		locTextTextureSize = glGetUniformLocation(sphereProgram, "textTextureSize");


		resetCamera();

		time = System.currentTimeMillis();

		renderTarget = new OGLRenderTarget(1024, 1024, 2);
		targetViewer = new TargetViewer(renderTarget);
	}
	
	@Override
	public void display() {
		long currentTime = System.currentTimeMillis();
		String text = "[LMB] [WSAD] [Ctrl/Shift] camera, [RMB] info";
		if (!cam.getFirstPerson())
			text += "[space][RF], ";
		else
			text += "[space], ";
		if (projP)
			text += "[P]ersp, ";
		else
			text += "[p]ersp, ";
		if (tree == 1)
			text+="ca[t]egories [PageUp/Down]:"+categories;
		if (tree == 2)
			text+="[t]hreshold [PageUp/Down]:"+(categories/100.);
		if (tree == 0)
			text+="[t]";
		text+= ", colorMode[backSpace], colorIndex[Ins-Del]:" + startIndexColor;
		text+= ", size[up-down]";
		text +=", position interpolation [left-right]";

		renderTarget.bind();

		glEnable(GL_DEPTH_TEST);
		glClearColor(0.f, 0.f, 0.f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//draw balls
		{
			glUseProgram(sphereProgram);
			double azimuth = (2 * Math.PI + cam.getAzimuth()) % (2*Math.PI);
			glUniform2f(locViewA, (float)azimuth, (float) cam.getZenith());
			glUniform2fv(locTextTextureSize, ToFloatArray.convert(textTextureSize));
			fontTexture.bind(sphereProgram, "textureID", 0);
			for (int i = 0; i < dataRecords.size(); i++) {
				Record r = dataRecords.get(i);
				//System.out.println(r.name + " " + r.abr);
				Point3D pos = r.position;
				Point3D fin = new Point3D(pos);
				//set position of ball
				if (r.name.length()>0) { //by group
					String[] labels = r.name.split(" ");
					String label = labels[0];
					if (species!=null && species.containsKey(label)) {
						fin = new Point3D(species.get(label).posXY.getX() / maxPosXY.getX(),
								species.get(label).posXY.getY() / maxPosXY.getY(), zMin);
						pos = pos.mul(1-interpolate).add(fin.mul(interpolate));

						int id = species.get(label).groupId;
						Col col = IndexColor.getIndexColorCol(id + startIndexColor);
						//System.out.println(col);
						glUniform3fv(locColor, ToFloatArray.convert(col));

					} else
						glUniform3f(locColor, 1, 0, 0);
					{//coloring by init number
						Col col = IndexColor.getIndexColorCol(r.groupNumber + startIndexColor);
						glUniform3fv(locColor, ToFloatArray.convert(col));
					}
				}
				else
					//for ungrouped balls
					glUniform3f(locColor, 0,0,1);

				//show or hide group ball
				//if (r.groupNumber>0) System.out.println(r.groupNumber);
				if (r.groupNumber > 0 && r.groupNumber< 9 && (!showGroup[r.groupNumber - 1]))
					continue;
				if (!showGroup[showGroup.length-2] && r.groupNumber >= (showGroup.length-1))
					continue;

				//blinking picked ball
				if (pickIndex == i && (currentTime/250)%2 == 0)
						continue;

				//Mat4 m = new Mat4RotY(-cam.getZenith()).mul(new Mat4RotZ(cam.getAzimuth()));

				Mat4 m = new Mat4RotY(-cam.getZenith()).mul(new Mat4RotZ(cam.getAzimuth())).mul(new Mat4Transl(pos.ignoreW()));
				glUniformMatrix4fv(locMat3, false,
						ToFloatArray.convert(new Mat4Scale(size).mul(m).mul(model).mul(cam.getViewMatrix()).mul(proj)));
				//interpolated position
				glUniform4f(locData, (float) pos.getX(),
						(float) pos.getY(),
						(float) pos.getZ(),
						(float) i);
				ball.draw(GL_TRIANGLES, sphereProgram);

				//original position
				m = new Mat4RotY(-cam.getZenith()).mul(new Mat4RotZ(cam.getAzimuth())).mul(new Mat4Transl(r.position.ignoreW()));
				glUniformMatrix4fv(locMat3, false,
						ToFloatArray.convert(new Mat4Scale(size).mul(m).mul(model).mul(cam.getViewMatrix()).mul(proj)));
				glUniform4f(locData, (float) r.position.getX(),
						(float) r.position.getY(),
						(float) r.position.getZ(),
						(float) i);
				ball.draw(GL_TRIANGLES, sphereProgram);

				//tree position
				m = new Mat4RotY(-cam.getZenith()).mul(new Mat4RotZ(cam.getAzimuth())).mul(new Mat4Transl(fin.ignoreW()));
				glUniformMatrix4fv(locMat3, false,
						ToFloatArray.convert(new Mat4Scale(size).mul(m).mul(model).mul(cam.getViewMatrix()).mul(proj)));
				glUniform4f(locData, (float) fin.getX(),
						(float) fin.getY(),
						(float) fin.getZ(),
						(float) i);
				if (drawMode % 2  == 0) ball.draw(GL_TRIANGLES, sphereProgram);

			}
		}

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, width, height);
		if (backgroundMode % 2 == 1) glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		if (backgroundMode % 2 == 0) glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(lineProgram);

		targetViewer.view(0); //redraw render target to frame buffer

		glPolygonMode(GL_BACK, GL_FILL);
		glPolygonMode(GL_FRONT, GL_FILL);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);

		glUniformMatrix4fv(locMat2, false,
				ToFloatArray.convert(model.mul(cam.getViewMatrix()).mul(proj)));
		//draw links between balls
		if (buffersLine!=null && tree != 0) buffersLine.draw(GL_LINES, lineProgram);
		//draw tree
		if (buffersTree!=null && drawMode % 2  == 0) buffersTree.draw(GL_LINES, lineProgram);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

		if ((backgroundMode/2) %2 == 0) {//draw filled cube
			cube.draw(GL_TRIANGLES, lineProgram);
		}

		glCullFace(GL_BACK);
		//textureViewer.view(renderTarget.getColorTexture(1),-1,0,0.5);
		//textureViewer.view(renderTarget.getColorTexture(0),0,-1,0.5);
		//textureViewer.view(renderTarget.getDepthTexture(),-1,0,0.5);
		//textureViewer.view(fontTexture,-1,-1,0.5);


		glDisable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		if ((backgroundMode/2) %2 == 1) {//draw axis
			glPolygonMode(GL_BACK, GL_LINE);
			glPolygonMode(GL_FRONT, GL_LINE);
			glLineWidth(5);
			axis.draw(GL_LINES, lineProgram);
		}
		glLineWidth(1);

		//print picked name
		if (pick){
			OGLTexImageFloat image = renderTarget
					.getColorTexture(1)
					.getTexImage(new OGLTexImageFloat.Format(4));
			double xP = ox / width * image.getWidth();
			double yP = (1 - oy / height) * image.getHeight();
			pick = false;
			int index= (int)(image.getPixel((int) xP, (int) yP, 0)*1000) -1;
			if (index>=0 && index<dataRecords.size()) {
				pickedName = dataRecords.get(index).name + " - " + dataRecords.get(index).abr + " - " + index  + " grID: " + dataRecords.get(index).groupID  + " grN: " + dataRecords.get(index).groupNumber;
				pickIndex = index;
			}
			else
				pickedName = "";

		}

		String text2 = "Groups [-0,1,...," + (showGroup.length-1) + "]:";
		/*char mark = '1';
		for (int i = 0; i < showGroup.length - 1; i++) {
			if (showGroup[i])
				text2 += mark;
			mark++;
		}*/

		if (help) {
			textRenderer.setScale(1);
			textRenderer.addStr2D(3, 26, text);
			textRenderer.addStr2D(3, 50, text2);
		}
		textRenderer.setColor(Color.red);
		textRenderer.setBackgroundColor(Color.gray);
		textRenderer.addStr2D(3, height - 80, "DNA   ");
		textRenderer.setColor(Color.green);
		textRenderer.addStr2D(50, height - 80, "cDNA   ");
		textRenderer.setColor(Color.blue);
		textRenderer.addStr2D(110, height - 80, "cds");

		textRenderer.setScale(2);
		for(int i=1; i < speciesNames.length - 1; i++){
			int groupID = Math.min(i,9);
			//if ((i>0 && i<9 && showGroup[i-1]) ||
			//		(i>8 && i<speciesNames.length && showGroup[8])){
			if (showGroup[groupID-1]){
				Col col = IndexColor.getIndexColorCol(i + startIndexColor);
				textRenderer.setColor(new Color(col.getRGB()));
				textRenderer.addStr2D(3, 50+(i*2)*30, groupID + "-" +speciesNames[i]);
			}
		}
		textRenderer.setColor(Color.white);
		textRenderer.setScale(3);
		textRenderer.addStr2D(3, height - 3, pickedName);
		textRenderer.setScale(1);
		textRenderer.addStr2D(width - 70, height - 3, " GC2C");
	}
}