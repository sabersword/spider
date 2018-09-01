package com.ypq;

public class Player {
	public Player() {
		
	}
	
	public void stopPlaying() {
		System.out.println("stop playing!");
	}
	
	public void stopPlaying(String arg1) {
		System.out.println("stop playing with" + arg1);
	}
	
	public void stopPlaying(String arg1, String arg2) {
		System.out.println("stop playing with" + arg1 + "with" + arg2);
	}
}
