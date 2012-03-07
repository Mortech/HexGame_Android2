package com.sam.hex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;
import android.graphics.Color;

public class BoardView extends View{
	private ShapeDrawable[][] mDrawable;
	private ShapeDrawable[][] mOutline;
	private BitmapDrawable background;
	
	public BoardView(Context context){
		super(context);
		calculateGrid(context);
	}
	
	protected void onDraw(Canvas canvas){
		int n = Global.gridSize;
		background.setTargetDensity(canvas);
		background.setBounds(0,0,Global.windowWidth,Global.windowHeight);
		background.draw(canvas);
		for(int xc=0;xc<n;xc++)
			for(int yc=0;yc<n;yc++){
				mOutline[xc][yc].draw(canvas);
				mDrawable[xc][yc].getPaint().setColor(Global.gamePiece[xc][yc].getColor());
				mDrawable[xc][yc].draw(canvas);
			}
		
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		int n = Global.gridSize;
		mDrawable = new ShapeDrawable[n][n];
		mOutline=new ShapeDrawable[n][n];
		Global.windowHeight=getHeight();
		Global.windowWidth=getWidth();
		//Size of border
		int border=1;
		
		double radius=BoardTools.radiusCalculator(Global.windowWidth, Global.windowHeight, Global.gridSize); 
		double hrad = radius * Math.sqrt(3) / 2;
		int yOffset = (int) ((Global.windowHeight - ((3 * radius / 2)
				* (Global.gamePiece[0].length - 1) + 2 * radius)) / 2);
		int xOffset = (int) ((Global.windowWidth - (hrad * Global.gamePiece.length*2+hrad*(Global.gamePiece.length-1))) / 2);
		System.out.println("hrad="+hrad);
		System.out.println("width="+Global.windowWidth);
		System.out.println("height="+Global.windowHeight);
		System.out.println("xOffset="+xOffset);
		
		
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
				double x=((hrad + yc * hrad + 2 * hrad * xc) + hrad+xOffset);
				double y=(1.5* radius * yc + radius)+ yOffset;
				mDrawable[xc][yc] = new ShapeDrawable(new PathShape(path, (int)hrad*2, (int)radius*2));
				mDrawable[xc][yc].setBounds((int)(x-hrad),(int)(y),(int)(x+hrad)-border,(int)(y+radius*2)-border);
				mOutline[xc][yc] = new ShapeDrawable(new PathShape(path, (int)hrad*2, (int)radius*2));
				mOutline[xc][yc].setBounds((int)(x-hrad),(int)(y),(int)(x+hrad),(int)(y+radius*2));
				mOutline[xc][yc].getPaint().setColor(Color.BLACK);
				Global.gamePiece[xc][yc].set(x-hrad, y, radius);
			}
		background=new BitmapDrawable(BoardTools.getBackground(Global.windowWidth, Global.windowHeight));
	}
	
	public void calculateGrid(Context context){ //TODO: Clean this up!
		int n = Global.gridSize;
/*		mDrawable = new ShapeDrawable[n][n];
		
		
		//DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		
		//Global.windowHeight=metrics.heightPixels;
		//Global.windowWidth=metrics.widthPixels;
		Global.windowHeight=getHeight();
		Global.windowWidth=getWidth();
		
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
        
		*/
		for(int xc=0;xc<n;xc++)
			for(int yc=0;yc<n;yc++){
			//	int x=(int)((hrad + yc * hrad + 2 * hrad * xc) + xOffset);
			//	int y=(int)(1.5* radius * yc + radius)+ yOffset;
			//	mDrawable[xc][yc] = new ShapeDrawable(new PathShape(path, (int)hrad*2, (int)radius*2));
			//	mDrawable[xc][yc].setBounds(x,y,(int)(x+hrad*2),(int)(y+radius*2));
				Global.gamePiece[xc][yc]=new RegularPolygonGameObject();
			}
		//background=new BitmapDrawable(context.getResources(),BoardTools.getBackground(Global.windowWidth, Global.windowHeight));
	}
}