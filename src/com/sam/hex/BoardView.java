package com.sam.hex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Color;

public class BoardView extends View{
	private ShapeDrawable[][] mDrawable;
	private ShapeDrawable[][] mOutline;
	private ShapeDrawable backgroundTopBottom;
	private ShapeDrawable backgroundLeft;
	private ShapeDrawable backgroundRight;
	
	public BoardView(Context context){
		super(context);
		calculateGrid();
	}
	public BoardView(Context context, AttributeSet attrs){
        super(context, attrs);
        calculateGrid();
    }
    public BoardView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        calculateGrid();
    }
	
	protected void onDraw(Canvas canvas){
		int n = Global.gridSize;
		
		colorBackground();
		backgroundTopBottom.draw(canvas);
		backgroundLeft.draw(canvas);
		backgroundRight.draw(canvas);
		for(int xc=0;xc<n;xc++)
			for(int yc=0;yc<n;yc++){
				mOutline[xc][yc].draw(canvas);
				mDrawable[xc][yc].getPaint().setColor(Global.gamePiece[xc][yc].getColor());
				mDrawable[xc][yc].draw(canvas);
			}
		if(Global.currentPlayer==1 && !Global.gameOver){
			Global.player1Icon.setColorFilter(Global.player1.getColor());
			Global.player1Icon.setAlpha(255);
			Global.player2Icon.setColorFilter(Global.player2.getColor());
			Global.player2Icon.setAlpha(80);
		}
		else if(Global.currentPlayer==2 && !Global.gameOver){
			Global.player1Icon.setColorFilter(Global.player1.getColor());
			Global.player1Icon.setAlpha(80);
			Global.player2Icon.setColorFilter(Global.player2.getColor());
			Global.player2Icon.setAlpha(255);
		}
		else{
			Global.player1Icon.setAlpha(80);
			Global.player2Icon.setAlpha(80);
		}
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
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
		
		
        //Shape of a hexagon
  		Path path = new Path();
        path.moveTo((float)0, (float)-radius);
        path.lineTo((float)hrad, (float)-radius/2);
        path.lineTo((float)hrad, (float)radius/2);
        path.lineTo(0,(float)radius);
        path.lineTo((float)-hrad,(float)radius/2);
        path.lineTo((float)-hrad,(float)-radius/2);
        path.close();
        
		//Draw background
        if(Global.windowHeight>Global.windowWidth){
        	Path left = new Path();
	        left.moveTo(0, 0);
	        left.lineTo((float) (Global.gridSize*hrad*2), 0);
	        left.lineTo(0, Global.windowHeight);
	        left.close();
	        Path right = new Path();
	        right.moveTo(Global.windowWidth, Global.windowHeight);
	        right.lineTo((float) (Global.windowWidth-(Global.gridSize*hrad*2)), Global.windowHeight);
	        right.lineTo(Global.windowWidth, 0);
	        right.close();
	        
	        backgroundTopBottom = new ShapeDrawable(new RectShape());
	        backgroundTopBottom.setBounds(0,0,Global.windowWidth,Global.windowHeight);
	        backgroundLeft = new ShapeDrawable(new PathShape(left, Global.windowWidth, Global.windowHeight));
	        backgroundLeft.setBounds(0,(int) (yOffset+hrad),Global.windowWidth,Global.windowHeight);
	        backgroundRight = new ShapeDrawable(new PathShape(right, Global.windowWidth, Global.windowHeight));
	        backgroundRight.setBounds(0,0,Global.windowWidth,(int) (Global.windowHeight-(yOffset+hrad)));
        }
        else{
        	Path left = new Path();
	        left.moveTo(xOffset-(float)hrad, 0);
	        left.lineTo(Global.windowWidth-xOffset-((n-1)*(float)hrad), 0);
	        left.lineTo(Global.windowWidth/2, Global.windowHeight/2);
	        left.close();
	        Path right = new Path();
	        right.moveTo(Global.windowWidth-xOffset+(float)hrad, Global.windowHeight);
	        right.lineTo(xOffset+((n-1)*(float)hrad), Global.windowHeight);
	        right.lineTo(Global.windowWidth/2, Global.windowHeight/2);
	        right.close();
	        
        	backgroundTopBottom = new ShapeDrawable(new RectShape());
	        backgroundTopBottom.setBounds(0,0,Global.windowWidth,Global.windowHeight);
	        backgroundLeft = new ShapeDrawable(new PathShape(left, Global.windowWidth, Global.windowHeight));
	        backgroundLeft.setBounds(0,0,Global.windowWidth,Global.windowHeight);
	        backgroundRight = new ShapeDrawable(new PathShape(right, Global.windowWidth, Global.windowHeight));
	        backgroundRight.setBounds(0,0,Global.windowWidth,Global.windowHeight);
        }
              
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
	}
	
	public void calculateGrid(){
		if(Global.gamePiece[0][0]==null)
		for(int xc=0;xc<Global.gridSize;xc++)
			for(int yc=0;yc<Global.gridSize;yc++)
				Global.gamePiece[xc][yc]=new RegularPolygonGameObject();
	}
	
	private void colorBackground(){
		if(Global.windowHeight>Global.windowWidth){
	        backgroundTopBottom.getPaint().setColor(Global.player2.getColor());
	        backgroundLeft.getPaint().setColor(Global.player1.getColor());
	        backgroundRight.getPaint().setColor(Global.player1.getColor());
        }
        else{
	        backgroundTopBottom.getPaint().setColor(Global.player1.getColor());
	        backgroundLeft.getPaint().setColor(Global.player2.getColor());
	        backgroundRight.getPaint().setColor(Global.player2.getColor());
        }
	}
}