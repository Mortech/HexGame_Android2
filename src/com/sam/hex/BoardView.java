package com.sam.hex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.DisplayMetrics;
import android.view.View;
import android.graphics.drawable.BitmapDrawable;

public class BoardView extends View{
	private ShapeDrawable[][] mDrawable;
	private BitmapDrawable background;
	
	public BoardView(Context context){
		super(context);
		calculateGrid(context);
	}
	
	protected void onDraw(Canvas canvas){ //TODO: not being called?
		int n = Global.gridSize;
		background.draw(canvas); //TODO: fix this (where is background set?)
		for(int xc=0;xc<n;xc++)
			for(int yc=0;yc<n;yc++){
				mDrawable[xc][yc].getPaint().setColor(Global.gamePiece[xc][yc].getColor());
				mDrawable[xc][yc].draw(canvas);
			}
	}
	
	public void calculateGrid(Context context){
		int n = Global.gridSize;
		mDrawable = new ShapeDrawable[n][n];
		
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		
		Global.windowHeight=metrics.heightPixels;
		Global.windowWidth=metrics.widthPixels;
		
		double radius=BoardTools.radiusCalculator(Global.windowWidth, Global.windowHeight, Global.gridSize); //Off due to display not equaling drawable area?
		double hrad = radius * Math.sqrt(3) / 2;
		
		int yOffset = (int) ((Global.windowHeight - ((3 * radius / 2)
				* (Global.gamePiece[0].length - 1) + 2 * radius)) / 2);
		int xOffset = (int) ((Global.windowWidth - (hrad * Global.gamePiece.length)) / 2);
		
		//Shape of a hexagon
		Path path = new Path();
        path.moveTo((float)0, (float)-radius);
        path.lineTo((float)hrad, (float)-radius/2);
        path.lineTo((float)hrad, (float)radius/2);
        path.lineTo(0,(float)radius);
        path.lineTo((float)-hrad,(float)radius/2);
        path.lineTo((float)-hrad,(float)-radius/2);
        path.close();
        
		
		for(int xc=0;xc<n;xc++)
			for(int yc=0;yc<n;yc++){
				int x=(int)((hrad + yc * hrad + 2 * hrad * xc) + xOffset);
				int y=(int)(1.5* radius * yc + radius)+ yOffset;
				mDrawable[xc][yc] = new ShapeDrawable(new PathShape(path, (int)hrad*2, (int)radius*2));
				mDrawable[xc][yc].setBounds(x,y,(int)(x+hrad*2),(int)(y+radius*2));
				Global.gamePiece[xc][yc]=new RegularPolygonGameObject(x, y, radius);
			}
		background=new BitmapDrawable(context.getResources(),BoardTools.getBackground(Global.windowWidth, Global.windowHeight));
	}
}