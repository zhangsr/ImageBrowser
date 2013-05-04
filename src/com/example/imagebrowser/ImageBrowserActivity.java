package com.example.imagebrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * Improve Standard: ?
 * Improves : 
 * 	1.Multithread
 * 	2.ConvertView cache (seems no effort)
 * @author zsr
 *
 */
public class ImageBrowserActivity extends Activity {
	private static final String TAG = "ImageBrowserActivity";
	private static final int DISMISS_PROGRESS_DIALOG = 0;
	private final static String[] IMAGE_FILTER = new String[] {"jpeg", "jpg", "png", "gif", "bmp"};
	
	private List<String> mImgPaths = new ArrayList<String>();
	private int mCurrentIndex;
	private ImageAdapter mCoverImageAdapter;
	private ProgressDialog mProgressDialog;
	private CoverFlow mCoverFlow;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initImgPaths();
		
		initLayout();	//init coverflow
		
		setContentView(mCoverFlow);
	}
	
	/**
	 * Get image paths from intent, to show images in the same directory as the selected one.
	 */
	private void initImgPaths(){
		String currentImgPath = getIntent().getData().getPath();
		String currentImgName = currentImgPath.substring(currentImgPath.lastIndexOf('/') + 1);
		String imgDir = currentImgPath.substring(0, currentImgPath.lastIndexOf('/') + 1);
		File[] imgFiles = new File(imgDir).listFiles(new ImageFileNameFilter());
		mCurrentIndex = getCurrentIndex(currentImgName, imgFiles);
		for(File f : imgFiles){
			mImgPaths.add(imgDir + f.getName());
		}
	}
	
	/**
	 * Init coverflow and adapter
	 */
	private void initLayout(){
		mCoverFlow = new CoverFlow(this);
		mCoverImageAdapter = new ImageAdapter(this, mImgPaths);
		mCoverFlow.setAdapter(mCoverImageAdapter);
		//set space between items
		mCoverFlow.setSpacing(-50);
		mCoverFlow.setSelection(mCurrentIndex, true);
		mCoverFlow.setAnimationDuration(1000);
		
		mCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mCurrentIndex = arg2;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		mCoverFlow.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentIndex = position;
				Intent intent = new Intent();
				intent.putExtra("image_path", mImgPaths.get(mCurrentIndex));
				intent.setClass(ImageBrowserActivity.this, ImageDetailActivity.class);
				startActivity(intent);
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, getResources().getText(R.string.delete));
		menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE,
				getResources().getText(R.string.set_wallpaper));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case Menu.FIRST : {	//delete
			File file = new File(mImgPaths.get(mCurrentIndex));
			if(file.exists()){
				file.delete();
			}
			mImgPaths.remove(mCurrentIndex);
			mCoverImageAdapter.notifyDataSetChanged();
		};break;
		case Menu.FIRST + 1 : {	//set wallpaper
			final WallpaperManager wallpaperManager = 
					(WallpaperManager) getSystemService(WALLPAPER_SERVICE);
			mProgressDialog = ProgressDialog.show(this,
					getResources().getText(R.string.set_wallpaper),
					getResources().getText(R.string.please_wait), true, false);
			new Thread(new Runnable() {
				public void run() {
					try {
						wallpaperManager.setBitmap(BitmapFactory.decodeFile(mImgPaths.get(
								mCurrentIndex)));
						mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		};break;
		}
		return true;
	}
	
	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == DISMISS_PROGRESS_DIALOG) {
				mProgressDialog.dismiss();
			}
		}
	};



	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List mImgPaths;
		private Bitmap mDefaultBitmap;
		
		public ImageAdapter(Context c, List l) {
			this.mContext = c;
			this.mImgPaths = l;
			this.mDefaultBitmap = makeBitmapWithReflection(BitmapFactory.decodeResource(
					getResources(), R.drawable.default_pic));
		}

		public int getCount() {
			return mImgPaths.size();
		}

		public Object getItem(int position) {
			return mImgPaths.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			//reuse cached convertView to avoid memory overflow
			if(convertView == null){
				convertView = new ImageView(mContext);
				//imageView.setScaleType(ScaleType.MATRIX);
				((ImageView) convertView).setImageBitmap(mDefaultBitmap);
				convertView.setLayoutParams(new CoverFlow.LayoutParams(500, 500));
				new MakeBitmapTask((ImageView) convertView, (String)getItem(position)).executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, null);
			} else {
				
			}
			return convertView;
			
		}
		
		

	}
	
	private class MakeBitmapTask extends AsyncTask{
		private ImageView mImageView;
		private String mPath;
		
		public MakeBitmapTask(ImageView i, String p){
			this.mImageView = i;
			this.mPath = p;
		}

		@Override
		protected Object doInBackground(Object... params) {
			return makeBitmapWithReflection(BitmapFactory.decodeFile(mPath));
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			mImageView.setImageBitmap((Bitmap)result);
			mImageView.setLayoutParams(new CoverFlow.LayoutParams(500, 500));
			super.onPostExecute(result);
		}

		
	}
	
	private class ImageFileNameFilter implements FilenameFilter
	{
		//@Override
		public boolean accept(File dir, String fileName) {			
			String extension = null;
			if (fileName.startsWith(".")) {
				return false;
			}
			try {
				extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			} catch (Exception e) {
				return false;
			}
			return inTheArray(IMAGE_FILTER, extension);
		}
		
		private boolean inTheArray(String[] array, String string) {
			int tempLength = array.length;
			for (int i = 0; i < tempLength; ++i) {
				if (array[i].equals(string))
					return true;
			}
			return false;

		}
	}
	
	/**
	 * Steps to make cover flow item
	 * @param path
	 * @return
	 */
	private Bitmap makeBitmapWithReflection(Bitmap originalImage){
		final int reflectionGap = 4;
		
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		//This will not scale but will flip on the Y axis
		//zsr : ??
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);	
		//Create a Bitmap with the flip matrix applied to it.
		//We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width,
				height/2, matrix, false);
		//Create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2),
				Config.ARGB_8888);
		
		//Create a new Canvas with the bitmap that's big enough for
		//the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		//Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		//Draw in the gap
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		//Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		//Create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint(); 
		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, 
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, 
				TileMode.CLAMP); 
		//Set the paint to use this shader (linear gradient)
		paint.setShader(shader); 
		//Set the Transfer mode to be porter duff and destination in
		//??
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
		//Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap,
				paint); 
		
		return bitmapWithReflection;
	}
	
	private int getCurrentIndex(String name, File[] files){
		for(int i = 0; i < files.length; i++){
			if(name.endsWith(files[i].getName()))
				return i;
		}
		return -1;
	}
	
}
