package com.snowbros.mobilebowling3d.scenes;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;




public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder mHolder;
	private GLThread mGLThread;

	public GameSurfaceView(Context context) 
	{
		super(context);
		init();
	}
	
	private void init()
	{
		mHolder = this.getHolder();
		mHolder.addCallback(this);
	}
	
	public void setRenderer(Renderer renderer)
	{
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) 
	{
		
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		
		
	}
	
	
	
	
	
}