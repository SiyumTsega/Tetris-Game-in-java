package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import minos.*;

public class PlayManager {

	///Main play area
	
	final int WIDTH = 360;
	final int HEIGHT = 600;
	public static int leftX;
	public static int rightX;
	public static int topY;
	public static int bottomY;
	
	///Mino
	
	Mino currentMino;
	final int MINO_START_X;
	final int MINO_START_Y;
	Mino nextMino;
	final int NEXTMINO_X;
	final int NEXTMINO_Y;
	ArrayList<Block> staticBlocks = new ArrayList<>();
	
	//Others
	
	public static int dropInterval = 60;  /// mino drops every 60 frames

	public PlayManager() {
		
		/// Main Play Area Frame
		
		leftX = GamePanel.WIDTH/2 - WIDTH/2;
		rightX = leftX + WIDTH;
		topY = 50;
		bottomY = topY + HEIGHT;
		
		MINO_START_X = leftX + WIDTH/2 - Block.SIZE;
		MINO_START_Y = topY + Block.SIZE;
		
		NEXTMINO_X = rightX + 175;
		NEXTMINO_Y = topY + 500;
		
		///Set the starting mino
		
		currentMino = pickMino();
		currentMino.setXY(MINO_START_X, MINO_START_Y);
		nextMino = pickMino();
		nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
				
	}

	private Mino pickMino() {
		
		Mino mino = null;
		int i = new Random().nextInt(7);
		
		switch(i) {
		case 0: mino = new MinoL1(); break;
		case 1: mino = new MinoL2(); break;
		case 2: mino = new MinoT(); break;
		case 3: mino = new MinoBar(); break;
		case 4: mino = new MinoSquare(); break;
		case 5: mino = new MinoZ1(); break;
		case 6: mino = new MinoZ1(); break;
		}
		
		return mino;
	}
	public void update() {
		
		//check if the current mino is active
		if(currentMino.active == false) {
			
			staticBlocks.add(currentMino.b[0]);
			staticBlocks.add(currentMino.b[1]);
			staticBlocks.add(currentMino.b[2]);
			staticBlocks.add(currentMino.b[3]);
			
			//replace the current mino with next mino
			
			currentMino = nextMino;
			currentMino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
		}
		else {
			currentMino.update();
		}
		
	}
	
	public void draw(Graphics2D g2D) {
		
		//Draw Play Area Frame
		
		g2D.setColor(Color.white);
		g2D.setStroke(new BasicStroke(4f));
		g2D.drawRect(leftX - 4, topY - 4, WIDTH + 8, HEIGHT + 8);
		
		//Draw next mino frame
		
		int x = rightX + 100;
		int y = bottomY - 200;
		g2D.drawRect(x, y, 200, 200);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2D.drawString("NEXT", x+60, y+60);
		
		///Draw the current mino
		
		if(currentMino != null) {
			currentMino.draw(g2D);
		}
		
		//draw next mino
		
		nextMino.draw(g2D);
		
		//draw the static block
		
		for(int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).draw(g2D);
		}
		
		
		//draw pause string
		g2D.setColor(Color.yellow);
		g2D.setFont(g2D.getFont().deriveFont(50f));
		if(KeyHandler.pausePressed) {
			x = leftX + 70;
			y = topY + 320;
			g2D.drawString("PAUSED", x, y);
		}
	}
}
