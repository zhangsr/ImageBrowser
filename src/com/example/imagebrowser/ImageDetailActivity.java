package com.example.imagebrowser;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class ImageDetailActivity extends Activity{
	private String mImgPath;
	private TouchView mTouchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_view);
		
		mImgPath = getIntent().getStringExtra("image_path");
		
		mTouchView = (TouchView)findViewById(R.id.image);
		
		mTouchView.setImageBitmap(BitmapFactory.decodeFile(mImgPath));
	}
	
}
