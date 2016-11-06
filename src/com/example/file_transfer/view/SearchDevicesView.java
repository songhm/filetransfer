package com.example.file_transfer.view;

import com.example.file_transfer.BuildConfig;
import com.example.file_transfer.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class SearchDevicesView extends BaseView{
	
	public static final String TAG = "SearchDevicesView";
	public static final boolean D  = BuildConfig.DEBUG; 
	
	@SuppressWarnings("unused")
	private long TIME_DIFF = 1500;
	
	int[] lineColor = new int[]{0x7B, 0x7B, 0x7B};
	int[] innerCircle0 = new int[]{0xb9, 0xff, 0xFF};
	int[] innerCircle1 = new int[]{0xdf, 0xff, 0xFF};
	int[] innerCircle2 = new int[]{0xec, 0xff, 0xFF};
	
	int[] argColor = new int[]{0xF3, 0xf3, 0xfa};
	
	private float offsetArgs = 0;
	private boolean isSearching = false;
	private Bitmap bitmap;
	private Bitmap bitmap2;
	private Rect rMoon= new Rect();
	private int width=0;
	private int height=0;
	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
		//offsetArgs = 0;
		invalidate();
	}

	public SearchDevicesView(Context context) {
		super(context);
		initBitmap();
	}
	
	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBitmap();
	}
	
	private void initBitmap(){
		if(bitmap == null){
			bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gplus_search_bg));
		}
		if(bitmap2 == null){
			bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sweep));
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	
		width = getWidth()/2;
		height = getHeight()/2;
		canvas.drawBitmap(bitmap, width - bitmap.getWidth()/2, height - bitmap.getHeight()/2, null);
		canvas.rotate(offsetArgs,width,height);
		rMoon.set(width-bitmap2.getWidth(),height,width,height+bitmap2.getHeight());
		canvas.drawBitmap(bitmap2,null,rMoon,null);
		if(isSearching){
			offsetArgs = offsetArgs + 2;
			invalidate();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:		
			return false;
		case MotionEvent.ACTION_MOVE: 
			return false;
		case MotionEvent.ACTION_UP:
			return false;
		}
		return super.onTouchEvent(event);
	}
	

}

