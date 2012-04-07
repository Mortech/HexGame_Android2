package com.sam.hex.lan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;

public class DialogBox implements Runnable{
	AlertDialog.Builder builder;
	public Thread thread;
	public DialogBox(Context context, String message, DialogInterface.OnClickListener clickListener, String positiveButtonText, String neutralButtonText, String negativeButtonText){
		builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(positiveButtonText, clickListener);
		builder.setNeutralButton(neutralButtonText, clickListener);
		builder.setNegativeButton(negativeButtonText, clickListener);
		
		thread = new Thread(this, "dialog"); //Create a new thread.
		thread.start();
	}
	public DialogBox(Context context, String message, DialogInterface.OnClickListener clickListener, String positiveButtonText, String negativeButtonText){
		builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(positiveButtonText, clickListener);
		builder.setNegativeButton(negativeButtonText, clickListener);
		
		thread = new Thread(this, "dialog"); //Create a new thread.
		thread.start();
	}
	public DialogBox(Context context, String message, DialogInterface.OnClickListener clickListener, String neutralButtonText){
		builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setNeutralButton(neutralButtonText, clickListener);
		
		thread = new Thread(this, "dialog"); //Create a new thread.
		thread.start();
	}
	
	@Override
	public void run() {
		Looper.prepare();
		builder.show();
		Looper.loop();
	}
}