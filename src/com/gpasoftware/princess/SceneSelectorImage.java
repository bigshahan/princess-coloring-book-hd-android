package com.gpasoftware.princess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;


public class SceneSelectorImage extends ImageView {
	Context ctx;
	
	public SceneSelectorImage(Context context) {
		super(context);
		ctx = context;
	}
	
	public SceneSelectorImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public SceneSelectorImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
    }

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Resources r = getResources();
		
		// account for arrows
		int spacingWidthSide = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics());
		
		// bottom padding + title
		int spacingHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, r.getDisplayMetrics());
		
		// width of Image
        int widthOfImage = MeasureSpec.getSize(widthMeasureSpec) - spacingWidthSide*2;
        int heightOfPage = 400;
        
        // Get Height of Page
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        
        try {
        	Point size = new Point();
        	display.getSize(size);
        	heightOfPage = size.y;
        } catch (NoSuchMethodError e) {
        	heightOfPage = display.getHeight();
        }
        
        
        // Calculate Image Height
        int heightOfImage = heightOfPage - spacingHeight;
        
        // calculate height
        Log.w("heightOfPage", Integer.toString(heightOfPage));
        Log.w("width", Integer.toString(widthOfImage));
        Log.w("height", Integer.toString(heightOfImage));
        
        setMeasuredDimension(widthOfImage, heightOfImage);
    }
}
