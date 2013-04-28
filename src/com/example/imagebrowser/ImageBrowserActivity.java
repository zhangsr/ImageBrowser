package com.example.imagebrowser;

import android.app.Activity;
import android.content.Context;
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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageBrowserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CoverFlow coverFlow = new CoverFlow(this);
		ImageAdapter coverImageAdapter = new ImageAdapter(this);
		coverImageAdapter.createReflectedImages();
		coverFlow.setAdapter(coverImageAdapter);
		//set space between items
		coverFlow.setSpacing(-50);
		coverFlow.setAnimationDuration(1000);
		
		setContentView(coverFlow);
		
	}
	
	public class ImageAdapter extends BaseAdapter {
		//int mGalleryItemBackground;
		private Context mContext;
		//private FileInputStream fis;
		private ImageView[] mImages;
	        
		private Integer[] mImageIds = {
				R.drawable.pic1,
				R.drawable.pic2,
				R.drawable.pic3,
				R.drawable.pic4,
				R.drawable.pic5,
				R.drawable.pic6
		};
		
	     
		public ImageAdapter(Context c) {
			this.mContext = c;
			this.mImages = new ImageView[mImageIds.length];
		}
		
		public boolean createReflectedImages() {
			//The gap we want between the reflection and the original image
			final int reflectionGap = 4;
			int index = 0;
			for (int imageId : mImageIds) {
				//zsr: no need to do in sub thread ???
				Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(),
						imageId);
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
				
				ImageView imageView = new ImageView(mContext);
				imageView.setImageBitmap(bitmapWithReflection);
				imageView.setLayoutParams(new CoverFlow.LayoutParams(500, 500));
				//imageView.setScaleType(ScaleType.MATRIX);
				mImages[index++] = imageView;
	          
			}
			return true;
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			//Make sure we set anti-aliasing otherwise we get jaggies
			//BitmapDrawable drawable = (BitmapDrawable) mImages[position].getDrawable();
			//drawable.setAntiAlias(true);//work?
			//reuse cached convertView to 
			if(convertView == null){
				return mImages[position];
			} else {
				return convertView;
			}
			
		}
		
		/** Returns the size (0.0f to 1.0f) of the views 
		 * depending on the 'offset' to the center. */ 
		public float getScale(boolean focused, int offset) { 
			/* Formula: 1 / (2 ^ offset) */ 
			return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
		} 

	}
}
