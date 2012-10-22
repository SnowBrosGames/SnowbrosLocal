package com.snowbros.mobilebowling3d;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;

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
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;

public class GameActivity extends Activity {

	private GLSurfaceView mGLView;
	private FrameBuffer frameBuf;
	private World world;
	private Object3D sphere;
	private Object3D plane;
	private Object3D[] pins;
	private int SIZE = 2;
	private SkyBox box;
	private Light lighting;
	private float xPos = -1;
	private float yPos = -1;
	private GameRenderer theScene;
	private static GameActivity _main = null;
	
	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        if (_main != null) {
			copy(_main);
		}
       
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
    
    
	private void copy(Object src) {
		try {
			
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
    
    protected void onStop()
    {
    	super.onStop();
  
    	System.out.println("Closing the App.");
    }
    
    
    public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			touchTurn = xd / 100f;
			touchTurnUp = yd / 100f;
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
		}

		return super.onTouchEvent(me);
	}

    
    
    
    class GameRenderer implements GLSurfaceView.Renderer
    {
    	String floorTxt = "floor+plane";
    	String ballTxt = "ballTexture";
    	String mountTxt = "mountains";
    	String faceTxt = "faces";
    	
    	String leftTxt = "leftImage";
    	String rightTxt = "rightImage";
    	String upTxt = "skyImage";
    	String bottomTxt = "bottomImage";
    	String backTxt = "backImage";
    	String frontTxt = "frontImage";
    	SimpleVector sv;
    	Camera cam;
    	
    	
    	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    	{
    		
    		System.out.println("Creating the Surface Scene.");
    		
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
    			
				pins = new Object3D[SIZE];
				sv = new SimpleVector();
	   		
	   		 	world = new World();
				world.setAmbientLight(20, 20, 20);
				
				lighting = new Light(world);
				lighting.setIntensity(250, 250, 250);
				
				cam = world.getCamera();
				
				Texture floorTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.stone)), 64, 64));
				Texture faceTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.face)), 64, 64));
				Texture ballTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.checkerboard)), 64, 64));
				Texture frontTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.front)), 512,512));
				Texture backTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.back)), 512,512));
				Texture leftTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.left)), 512,512));
				Texture rightTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.right)), 512,512));
				Texture bottomTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.bottom)), 512,512));
				Texture upTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.top)), 512,512));
				
				TextureManager tm = TextureManager.getInstance();
				tm.addTexture(floorTxt, floorTexture);
				tm.addTexture(faceTxt, faceTexture);
				tm.addTexture(ballTxt,ballTexture);
				tm.addTexture(frontTxt,frontTexture);
				tm.addTexture(backTxt,backTexture);
				tm.addTexture(leftTxt,leftTexture);
				tm.addTexture(rightTxt,rightTexture);
				tm.addTexture(bottomTxt,bottomTexture);
				tm.addTexture(upTxt,upTexture);
				
				box = new SkyBox(leftTxt,frontTxt,rightTxt,backTxt, upTxt,bottomTxt,6144);
			
				for(int i = 0;  i < SIZE; i++)
				{
					pins[i] = Primitives.getCone(10);
					pins[i].calcTextureWrapSpherical();
					pins[i].setTexture(faceTxt);
					pins[i].rotateX(-210.5f);
					pins[i].rotateY(300.0f);
					pins[i].strip();
			    }
				
			
				
				sphere = Primitives.getSphere(10);
				sphere.calcTextureWrap();
				sphere.setTexture(ballTxt);
				//sphere.setName("ball");
				sphere.translate(0,-15,-120);
				sphere.rotateX((float)Math.PI/2);
				sphere.strip();
				
				
				plane = Primitives.getPlane(3, 100);
				plane.align(cam);
				plane.calcTextureWrap();
				plane.setTexture(floorTxt);
				plane.rotateX((float)Math.PI/2f);
				plane.strip();
				
				pins[0].translate(0.0f, -25.5f, 50.0f);
				pins[1].translate(25.0f, -25.5f,50.f);
				
				
				world.addObject(pins[0]);
				world.addObject(pins[1]);
				world.addObject(plane);
    			world.addObject(sphere);
    			world.buildAllObjects();
    			
    			sv.set(plane.getTransformedCenter());
    			sv.y -= 300;
    			sv.x -= 100;
    			sv.z += 200;
    			lighting.setPosition(sv);
				
    			cam.moveCamera(Camera.CAMERA_MOVEOUT,180);
    			cam.moveCamera(Camera.CAMERA_MOVEUP,100);
    			cam.lookAt(plane.getTransformedCenter());
					

				if (_main == null) {
					_main = GameActivity.this;
					MemoryHelper.compact();
				}
    			
    			
    		}
		}
    	
    	public void onDrawFrame(GL10 gl) 
    	{
    		
    		try {
    			
    		if (touchTurn != 0) {
				world.getCamera().rotateY(touchTurn);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				world.getCamera().rotateX(touchTurnUp);
				touchTurnUp = 0;
			}
    		
    		frameBuf.clear();
    		box.render(world, frameBuf);
    		world.renderScene(frameBuf);
    		world.draw(frameBuf);
    		frameBuf.display();
    	
    	
    } catch (Exception e) {
    }
    	
 }

}




}
