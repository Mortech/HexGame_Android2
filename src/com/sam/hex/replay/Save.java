package com.sam.hex.replay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.text.InputType;
import android.widget.EditText;

import com.sam.hex.Global;
import com.sam.hex.PlayerObject;

public class Save{
	public static String fileName;
	
	private void saveGame(String fileName){
    	Thread saving = new Thread(new ThreadGroup("Save"), new save(), "saving", 200000);
		saving.start();
		try {
			saving.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		showSavedDialog("Saved!");
	}
    
    class save implements Runnable{

		@Override
		public void run() {
			createDirIfNoneExists(File.separator + "Hex" + File.separator);
			String file = Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + fileName;
			if(file!=null){
				if(!file.toLowerCase().endsWith(".rhex")){
				    file = file + ".rhex";
				}
				try {
					ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
					
					outputStream.writeObject(Global.player1Color);
					outputStream.writeObject(Global.player2Color);
					outputStream.writeObject(Global.player1Name);
					outputStream.writeObject(Global.player2Name);
					outputStream.writeObject(Global.moveList);
					outputStream.writeObject(Global.gridSize);
					outputStream.writeObject(Global.moveNumber);
					if(Global.player1.supportsSave()){
						outputStream.writeObject(Global.player1);
						outputStream.writeObject(Global.player1Type);
					}
					else{
						outputStream.writeObject(new PlayerObject((byte)1));
						outputStream.writeObject((byte)0);
					}
					if(Global.player2.supportsSave()){
						outputStream.writeObject(Global.player2);
						outputStream.writeObject(Global.player2Type);
					}
					else {
						outputStream.writeObject(new PlayerObject((byte)2));
						outputStream.writeObject((byte)0);
					}
					
					outputStream.flush();
                    outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	
    }
	
	public static boolean createDirIfNoneExists(String path) {
	    boolean ret = true;

	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            ret = false;
	        }
	    }
	    return ret;
	}
	
	public void showSavingDialog(){
        final EditText editText = new EditText(Global.board.getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        editText.setText(dateFormat.format(date) + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle("Enter a filename")
        .setView(editText)
        .setPositiveButton("OK", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			fileName = editText.getText().toString();
    			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + fileName);
    			String filePath = file.getPath();
    			if(!filePath.toLowerCase().endsWith(".rhex")){
    			    file = new File(filePath + ".rhex");
    			}
    			saveGame(fileName);
    		}
        })
        .setNegativeButton("Cancel", null)
        .show();
    }
	
	private void showSavedDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle(message)
        .setNeutralButton("Okay", null)
        .show();
    }
}