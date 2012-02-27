package com.sam.hex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.DisplayMetrics;
import android.view.View;

public class BoardView extends View{
	private ShapeDrawable[][] mDrawable;
	
	public BoardView(Context context){
		super(context);
		calculateGrid(context);
	}
	
	protected void onDraw(Canvas canvas){
		int n = Global.getN();
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(BoardTools.teamGrid()[i][j]==1){
					mDrawable[i][j].getPaint().setColor(Global.playerOne);//player one
				}
				else if(BoardTools.teamGrid()[i][j]==2){
					mDrawable[i][j].getPaint().setColor(Global.playerTwo);//Blue
				}
				else if(BoardTools.teamGrid()[i][j]==3){
					mDrawable[i][j].getPaint().setColor(0xffffff00);//Yellow
				}
				else if(BoardTools.teamGrid()[i][j]==4){
					mDrawable[i][j].getPaint().setColor(0xffffff00);//Yellow
				}
				else{
					mDrawable[i][j].getPaint().setColor(0xff74AC23);
				}
				mDrawable[i][j].draw(canvas);
			}
		}
	}
	
	public void calculateGrid(Context context){
		int n = Global.getN();
		mDrawable = new ShapeDrawable[n][n];
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int L=Math.min(metrics.widthPixels/(3*n/2),metrics.heightPixels/(3*n/2))/2;
		int width = Math.min(metrics.widthPixels/(3*n/2),metrics.heightPixels/(3*n/2));
		int height = Math.min(metrics.widthPixels/(3*n/2),metrics.heightPixels/(3*n/2));
		Global.setHexLength(L);
		//double game_length=L*Math.sqrt(3) * n * (n-1)*L*(Math.sqrt(3)/2);
		
		
		int x=width;
		int y=height;
		int spacing=0;
		
		//Shape of a hexagon
		Path path = new Path();
        path.moveTo(0, 0+L/2);
        path.lineTo(0, L+L/2);
        path.lineTo((float) (-L*Math.sqrt(3)/2),L+L*1/2+L/2);
        path.lineTo((float) (-L*Math.sqrt(3)),L+L/2);
        path.lineTo((float) (-L*Math.sqrt(3)),0+L/2);
        path.lineTo((float) (-L*Math.sqrt(3)/2),-L*1/2+L/2);
        path.close();
        
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				mDrawable[i][j] = new ShapeDrawable(new PathShape(path, width, height));
				mDrawable[i][j].setBounds(x,y,x+width,y+height);
				
				BoardTools.setPolyXY(i, j, new Posn(x-2*L,y));
				
				x+=width;
			}
			
			spacing+=width/2;
			x=width+spacing;
			y+=height;
		}
	}
}