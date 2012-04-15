package com.sam.hex.startup;

import com.sam.hex.BoardTools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;
import android.graphics.Color;

public class StartUpView extends View{
	private ShapeDrawable[] mDrawable = new ShapeDrawable[3];
	private ShapeDrawable[] mOutline = new ShapeDrawable[3];
	public int windowHeight;
	public int windowWidth;
	
	public StartUpView(Context context){
		super(context);
	}
	
	protected void onDraw(Canvas canvas){
		for(int i=0;i<3;i++){
			mOutline[i].draw(canvas);
			mDrawable[i].draw(canvas);
		}
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		windowHeight=getHeight();
		windowWidth=getWidth();
		//Size of border
		int border=1;
		
		double radius=BoardTools.radiusCalculator(windowWidth, windowHeight, 2); 
		double hrad = radius * Math.sqrt(3) / 2;
		
        //Shape of a hexagon
  		Path path = new Path();
        path.moveTo((float)0, (float)-radius);
        path.lineTo((float)hrad, (float)-radius/2);
        path.lineTo((float)hrad, (float)radius/2);
        path.lineTo(0,(float)radius);
        path.lineTo((float)-hrad,(float)radius/2);
        path.lineTo((float)-hrad,(float)-radius/2);
        path.close();
              
		for(int xc=0;xc<3;xc++){
			double x=((hrad + 1 * hrad + 2 * hrad * xc) + hrad);
			double y=(1.5* radius * 1 + radius);
			mDrawable[xc] = new ShapeDrawable(new PathShape(path, (int)hrad*2, (int)radius*2));
			mDrawable[xc].setBounds((int)(x-hrad),(int)(y),(int)(x+hrad)-border,(int)(y+radius*2)-border);
			mDrawable[xc].getPaint().setColor(Color.WHITE);
			mOutline[xc] = new ShapeDrawable(new PathShape(path, (int)hrad*2, (int)radius*2));
			mOutline[xc].setBounds((int)(x-hrad),(int)(y),(int)(x+hrad),(int)(y+radius*2));
			mOutline[xc].getPaint().setColor(Color.BLACK);
		}
	}
}