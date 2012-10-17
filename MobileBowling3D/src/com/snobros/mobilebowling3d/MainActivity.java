package com.snobros.mobilebowling3d;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;


public class MainActivity extends Activity {

	private GLSurfaceView mGLView;
	private FrameBuffer frameBuf = null;
	private World world = null;
	private RGBColor bgColor = new RGBColor(50,50,100);
	private Object3D sphere;
	private Object3D plane;
	private Light lighting = null;
	private float xPos = -1;
	private float yPos = -1;
	private GameRenderer theScene;
	private static MainActivity _main = null;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        mGLView = new GLSurfaceView(this);
        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
			
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				
				return configs[0];
			}
		});
        
        theScene = new GameRenderer();
        mGLView.setRenderer(theScene);
        setContentView(mGLView);
    }
    
    
    protected void onPause()
    {
    	super.onPause();
    	mGLView.onPause();
    }
    
    
    protected void onResume()
    {
    	super.onResume();
    	mGLView.onResume();
    }
    
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    */
    
    class GameRenderer implements GLSurfaceView.Renderer
    {
    	
    	
    	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    	{
			
		}
		
    	public void onSurfaceChanged(GL10 gl, int width, int height) 
    	{
    		
    		if(frameBuf != null)
    		{
    			frameBuf.dispose();
    		}
    		
    		frameBuf = new FrameBuffer(gl,width,height);
    		
    		if(_main == null)
    		{
    			world = new World();
    			world.setAmbientLight(20, 20, 20);
    			
    			lighting = new Light(world);
    			lighting.setIntensity(250, 250, 250);
    			
    			Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.face)), 64, 64));
				TextureManager.getInstance().addTexture("texture", texture);
    			
				Texture ballTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.checkerboard)), 64, 64));
				TextureManager.getInstance().addTexture("ballTexture",ballTexture);
				
    			sphere = Primitives.getSphere(20);
    			sphere.calcTextureWrap();
    			sphere.setTexture("ballTexture");
    			sphere.setName("ball");
    			
    			
    			sphere.strip();
    			
    			
    			plane = Primitives.getPlane(3, 100);
    			plane.calcTextureWrapSpherical();
    			plane.setTexture("texture");
    			plane.setName("Alley");
    			plane.rotateX((float)Math.PI/2f);
    			
    			plane.strip();
    			
    			
    			sphere.translate(0,0.5f,-90);
    			sphere.rotateX((float)Math.PI/2);
    			
    			
    			world.addObject(plane);
    			world.addObject(sphere);
    			world.buildAllObjects();
    			
    			
    			Camera cam = world.getCamera();
    			cam.moveCamera(Camera.CAMERA_MOVEOUT,250);
    			cam.moveCamera(Camera.CAMERA_MOVEUP,100);
    			cam.lookAt(plane.getTransformedCenter());
    			
    			//cam.moveCamera(Camera.CAMERA_MOVEOUT,50);
    			//cam.lookAt(sphere.getTransformedCenter());
    			
    			SimpleVector sv = new SimpleVector();
    			sv.set(plane.getTransformedCenter());
    			sv.y -= 300;
    			sv.x -= 100;
    			sv.z += 200;
    			lighting.setPosition(sv);
    			
    			
    			
    			/*
    			SimpleVector sv = new SimpleVector();
    			sv.set(sphere.getTransformedCenter());
    			sv.y -= 100;
    			sv.z -= 100;
    			lighting.setPosition(sv);
    			*/
    			
    			if(_main == null)
    			{
    				_main = MainActivity.this;
    			}
    		}
		}
    	
    	public void onDrawFrame(GL10 gl) 
    	{
    		frameBuf.clear(bgColor);
    		world.renderScene(frameBuf);
    		world.draw(frameBuf);
    		frameBuf.display();
    	}
    	
    }

    
}
