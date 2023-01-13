import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LwjglWindow {

	public static int WIDTH = 600;
    public static int HEIGHT = 400;
 	private long window;
	private String TITLE = "Window title";
	private AbstractRenderer renderer;
    private static boolean DEBUG = false;

    static {
        if (DEBUG) {
            System.setProperty("org.lwjgl.util.Debug", "true");
            System.setProperty("org.lwjgl.util.NoChecks", "false");
            System.setProperty("org.lwjgl.util.DebugLoader", "true");
            System.setProperty("org.lwjgl.util.DebugAllocator", "true");
            System.setProperty("org.lwjgl.util.DebugStack", "true");
            Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        }
    }

	public LwjglWindow(AbstractRenderer renderer) {
		this(WIDTH, HEIGHT, renderer, false, "Window title");
	}

	public LwjglWindow(AbstractRenderer renderer, boolean debug, String title) {
		this(WIDTH, HEIGHT, renderer, debug, title);
	}

	public LwjglWindow(int width, int height, AbstractRenderer renderer, boolean debug, String title) {
		this.renderer = renderer;
		DEBUG = debug;
		WIDTH = width;
		HEIGHT = height;
		TITLE = title;
		if (DEBUG)
			System.err.println("Run in debugging mode");
		run();
	}
	
	public void run() {
		init();
		loop();
		renderer.dispose();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, renderer.getKeyCallback());
		glfwSetWindowSizeCallback(window,renderer.getWsCallback());
		glfwSetMouseButtonCallback(window,renderer.getMouseCallback());
		glfwSetCursorPosCallback(window,renderer.getCursorCallback());
		glfwSetScrollCallback(window,renderer.getScrollCallback());
		
		if (DEBUG)
			glfwSetErrorCallback(new GLFWErrorCallback() {
	            GLFWErrorCallback delegate = GLFWErrorCallback.createPrint(System.err);

	            @Override
	            public void invoke(int error, long description) {
	                if (error == GLFW_VERSION_UNAVAILABLE)
	                    System.err.println("GLFW_VERSION_UNAVAILABLE: This demo requires OpenGL 2.0 or higher.");
	                if (error == GLFW_NOT_INITIALIZED)
	                    System.err.println("");
	                if (error == GLFW_NO_CURRENT_CONTEXT)
	                    System.err.println("GLFW_NO_CURRENT_CONTEXT");
				    if (error == GLFW_INVALID_ENUM)
				        System.err.println("GLFW_INVALID_ENUM");
				    if (error == GLFW_INVALID_VALUE)
				        System.err.println("GLFW_INVALID_VALUE");
				    if (error == GLFW_OUT_OF_MEMORY)
				        System.err.println("GLFW_OUT_OF_MEMORY");
				    if (error == GLFW_API_UNAVAILABLE)
				        System.err.println("GLFW_API_UNAVAILABLE");
				    if (error == GLFW_VERSION_UNAVAILABLE)
				        System.err.println("GLFW_VERSION_UNAVAILABLE");
				    if (error == GLFW_PLATFORM_ERROR)
				        System.err.println("GLFW_PLATFORM_ERROR");
				    if (error == GLFW_FORMAT_UNAVAILABLE)
				        System.err.println("GLFW_FORMAT_UNAVAILABLE");
				    if (error == GLFW_FORMAT_UNAVAILABLE)
				        System.err.println("GLFW_FORMAT_UNAVAILABLE");
	    
	                delegate.invoke(error, description);
	            }

	            @Override
	            public void free() {
	                delegate.free();
	            }
	        });

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		GL.createCapabilities();

		if (DEBUG)
			GLUtil.setupDebugMessageCallback();

		renderer.getWsCallback().invoke(window, WIDTH, HEIGHT);

		renderer.init();

		while ( !glfwWindowShouldClose(window) ) {
			
			renderer.display();
			
			glfwSwapBuffers(window); // swap the color buffers

			glfwPollEvents();
		}
	}

}