package com.example.imagebrowser;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TouchView extends ImageView{
	private static final int STEP = 50;
	private int mScreenWidth;
	private int mScreenHeight;
	private int mViewX;
	private int mViewY;
	private int mWindowX;
	private int mWindowY;
	private Context mContext;
	private int mLeft;
	private int mRight;
	private int mTop;
	private int mBottom;
	
	
	

	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}



	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}



	public TouchView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	private void init(){
		mLeft = 0;
		mRight = this.getWidth();
		mTop = 0;
		mBottom = this.getHeight();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScreenWidth = ((Activity)mContext).getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeight = ((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			mWindowX = (int)event.getRawX();
			mWindowY = (int)event.getRawY();
			mViewX = (int)event.getX();
			mViewY = (int)event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int tempLeft = mWindowX - mViewX;
			int tempRight = mWindowX - mViewX + this.getWidth();
			int tempTop = mWindowY - mViewY;
			int tempBottom = mWindowY - mViewY + this.getHeight();
			//if new left and right both in or both out of the window
			if((tempLeft >= 0 && tempRight <= mScreenWidth) || (tempLeft <= 0 && tempRight >= mScreenWidth)){
				mLeft = tempLeft;
				mRight = tempRight;
			}
			//if new top and bottom both in or both out of the window
			if((tempTop >= 0 && tempBottom <= mScreenHeight) || (tempTop <= 0 && tempBottom >= mScreenHeight)){
				mTop = tempTop;
				mBottom = tempBottom;
			}
			this.layout(mLeft, mTop, mRight, mBottom);
			mWindowX = (int)event.getRawX();
			mWindowY = (int)event.getRawY();
			break;
		}
		return true;
	}



	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int tempTop;
		int tempBottom;
		int tempLeft;
		int tempRight;
		
		Log.i("!!!!!!!!!", keyCode + "");
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_UP:
			tempTop = mTop - STEP;
			tempBottom = mBottom - STEP;
			if((tempTop >= 0 && tempBottom <= mScreenHeight) || (tempTop <= 0 && tempBottom >= mScreenHeight)){
				mTop = tempTop;
				mBottom = tempBottom;
				this.layout(mLeft, mTop, mRight, mBottom);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case 88:	//??
			tempLeft = mLeft - STEP;
			tempRight = mRight - STEP;
			if((tempLeft >= 0 && tempRight <= mScreenWidth) || (tempLeft <= 0 && tempRight >= mScreenWidth)){
				mLeft = tempLeft;
				mRight = tempRight;
				this.layout(mLeft, mTop, mRight, mBottom);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			tempLeft = mLeft + STEP;
			tempRight = mRight + STEP;
			if((tempLeft >= 0 && tempRight <= mScreenWidth) || (tempLeft <= 0 && tempRight >= mScreenWidth)){
				mLeft = tempLeft;
				mRight = tempRight;
				this.layout(mLeft, mTop, mRight, mBottom);
			}
			break;
		}
			
		return true;
	}
	
	
	

}
