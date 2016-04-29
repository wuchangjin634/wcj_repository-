package com.example.android_surface_camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements Callback, OnClickListener {
	private SurfaceView mSuefaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private boolean mPreviewRunning;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		mSuefaceView = (SurfaceView)findViewById(R.id.camera);
		mImageView = (ImageView)findViewById(R.id.image);
		mImageView.setVisibility(View.GONE);
		mSuefaceView.setOnClickListener(this);
		mSurfaceHolder = mSuefaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
	{
		if(mPreviewRunning)
		{
			mCamera.stopPreview();
		}
		Parameters params = mCamera.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);

//		params.set("rotation", 90);
		mCamera.setParameters(params);
		try{
			mCamera.setPreviewDisplay(holder);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}
	
	private AutoFocusCallback mAutoFocusCallBack = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.v("AutoFocusCallback", "AutoFocusCallback , boolean success:" + success);
			Camera.Parameters Parameters = mCamera.getParameters();
			Parameters.setPictureFormat(PixelFormat.JPEG);
			mCamera.setParameters(Parameters);
			mCamera.takePicture(mShutterCallback, null, mPictureCallback);
		}
	};
	
	PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.v("PictureCallback", "¡­onPictureTaken¡­");
			if (data != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				mImageView.setImageBitmap(bitmap);
				mImageView.setVisibility(View.VISIBLE);
				
				File fDir = Environment.getExternalStorageDirectory();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					FileOutputStream fileOutputStream = null;
					try {
						Date date=new Date();
						DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time=format.format(date);
						String fname = "/DCIM/Camera/" + time.replace(" ", "") + ".jpg";
						
						fileOutputStream = new FileOutputStream(new File(fDir,  fname));
						fileOutputStream.write(data, 0, data.length);
						fileOutputStream.flush();
					} catch (Exception e) 
					{
						e.printStackTrace();
					}finally 
					{
						if(fileOutputStream != null)
						{
							try {
								fileOutputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
				mSuefaceView.setVisibility(View.GONE);
				if (mPreviewRunning) {
					mCamera.stopPreview();
					mPreviewRunning = false;
				}
			}
		}
	};
	
	ShutterCallback mShutterCallback = new ShutterCallback() {
		public void onShutter() {

			Log.v("ShutterCallback", "¡­onShutter¡­");
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Log.v("onClick", "¡­onClick¡­");
		mCamera.autoFocus(mAutoFocusCallBack);
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
		mCamera = null;
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			if (mCamera != null)
			{
//				mCamera.autoFocus(mAutoFocusCallBack);
//		        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
