package com.example.imagebrowser;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageBrowserActivity extends Activity {
	private Gallery mGallery;
	private List<String> mPathList = new ArrayList<String>();
	private GalleryAdapter mGalleryAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mGallery = (Gallery)findViewById(R.id.gallery);
		mGalleryAdapter = new GalleryAdapter(this, mPathList);
		mGallery.setAdapter(mGalleryAdapter);
		
		mPathList.add("/sdcard/Pictures/Berwick Upon Tweed, Northumberland, England.jpg");
		mPathList.add("/sdcard/Pictures/Cashel Castle, Ireland.jpg");
//		mPathList.add("/sdcard/Pictures/City Wallpaper 1080p Vol2 (36).jpg");
//		mPathList.add("/sdcard/Pictures/City Wallpaper 1080p Vol2 (37).jpg");
		
		mGalleryAdapter.notifyDataSetChanged();
	}
	
	private class GalleryAdapter extends BaseAdapter{
		private List<String> pathList = new ArrayList<String>();
		private Context mContext;
		private float mDensity;
		private static final int ITEM_WITH = 1088;
		private static final int ITEM_HEIGHT = 704;
		
		public GalleryAdapter(Context context, List<String> list){
			this.mContext = context;
			this.pathList = list;
			mDensity = mContext.getResources().getDisplayMetrics().density;//??
		}

		@Override
		public int getCount() {
			return pathList.size();
		}

		@Override
		public Object getItem(int position) {
			return pathList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if(convertView == null){
				imageView = new ImageView(mContext);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setLayoutParams(new Gallery.LayoutParams(
						(int)(ITEM_WITH * mDensity + 0.5f),
						(int)(ITEM_HEIGHT * mDensity + 0.5f)));
				//can add background style
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageBitmap(BitmapFactory.decodeFile((String)getItem(position)));
			return imageView;
		}
		
	}

}
