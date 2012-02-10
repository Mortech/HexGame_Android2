package com.sam.hex;

public class BoardTools{
	public static void setGame(int m){
		Global.setN(m);
		clearBoard();
		Global.setPolyXY(new Posn[Global.getN()][Global.getN()]);
	}
	
	public static void makeMove(final int x, final int y, final byte team){
		if(Global.getRunning() && Global.getGameboard()[x][y]==0){
			//Play move
			Global.setGameboard(x,y,team);
        	Global.addToMoveList(new Posn(x,y));
        	
        	
        	//Call next player's move
//        	if(Global.getCurrentPlayer()==(byte) 1){
//        		Global.getPlayer1().getPlayerTurn();
//        	}
//        	else{
//        		Global.getPlayer2().getPlayerTurn();
//        	}
		}
	}
	
	public static int getN(){
		return Global.getN();
	}
	
	public static byte[][] teamGrid(){
		return Global.getGameboard();
	}
	
	public static Posn[][] getPolyXY(){
		return Global.getPolyXY();
	}
	
	public static void setPolyXY(int x, int y, Posn cord){
		Global.setPolyXY(x, y, cord);
	}
	
	public static void clearBoard(){
		Global.setGameboard(new byte[Global.getN()][Global.getN()]);
		for(int i=0;i<Global.getN();i++){
			for(int j=0;j<Global.getN();j++){
				Global.getGameboard()[i][j]=0;
			}
		}
	}
	
	public static void clearMoveList(){
		Global.clearMoveList();
	}
	
	public static void undo(){
		if(!Global.getMoveList().isEmpty()){
			Posn lastMove = Global.getMoveList().get(Global.getMoveList().size()-1);
			Global.setGameboard(lastMove.getX(), lastMove.getY(), (byte) 0);
			Global.removeFromMoveList(lastMove);
			updateCurrentPlayer();
			Global.setRunning(true);
			
			//Call next player's move
        	if(Global.getCurrentPlayer()==(byte) 1){
        		Global.getPlayer1().getPlayerTurn();
        	}
        	else{
        		Global.getPlayer2().getPlayerTurn();
        	}
		}
	}
	
	public static void updateCurrentPlayer(){
		Global.setCurrentPlayer((byte) (Global.getCurrentPlayer()%2+1));
	}
	
	public static boolean checkWinPlayer1() {
//		for (int i = 0; i < Global.gridSize - 1; i++) {
//			if (RegularPolygonGameObject.checkWinTeam((byte) 1,
//					Global.gridSize, i, Global.gamePiece)) {
//				System.out.print("Player one wins");
//				String path=RegularPolygonGameObject.findShortestPath((byte) 1,
//						Global.gridSize, i, Global.gamePiece);
//				RegularPolygonGameObject.colorPath(Global.gridSize,i,path);
//				return true;
//			}
//		}
		if(Global.getGameboard()[1][1]==1 || Global.getGameboard()[1][1]==2){
			return true;
		}
		return false;
	}
	
	public static boolean checkWinPlayer2() {
//		for (int i = 0; i < Global.gridSize - 1; i++) {
//			if (RegularPolygonGameObject.checkWinTeam((byte) 2, i,
//					Global.gridSize, Global.gamePiece)) {
//				System.out.print("Player Two wins");
//				RegularPolygonGameObject.findShortestPath((byte) 2, i,
//				Global.gridSize, Global.gamePiece);
//				return true;
//			}
//		}
		if(Global.getGameboard()[1][1]==1 || Global.getGameboard()[1][1]==2){
			return true;
		}
		return false;
	}
}