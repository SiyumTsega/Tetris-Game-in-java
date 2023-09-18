package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
	
	public static ArrayList<Block> staticBlocks = new ArrayList<>();
	
	//Others
	
	public static int dropInterval = 60;  /// mino drops every 60 frames
	
	boolean gameOver;

	//Effect
	
	boolean effectCounterOn;
	int effectCounter;
	ArrayList<Integer> effectY = new ArrayList<>();
	
	//Score
	
	int level = 1;
	int lines;
	int score;
	
	
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
			
			if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
				gameOver = true;
				
				GamePanel.music.stop();
				GamePanel.se.play(2, false);
			}
			
			currentMino.deactivating = false;
			
			//replace the current mino with next mino
			
			currentMino = nextMino;
			currentMino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
			
			//If a mino inactive check if a mino can be deleted
			
			checkDelete();
		}
		else {
			currentMino.update();
		}
		
	}
	
	private void checkDelete() {
		
		int x = leftX;
		int y = topY;
		int blockCount = 0;
		int lineCount = 0;
		
		while(x < rightX && y < bottomY) {
			for(int i = 0; i < staticBlocks.size(); i++) {
				if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
					
					//increase the count if there is static block
					
					blockCount++;
				}
			}
			
			x += Block.SIZE;
			
			if(x == rightX) {
				
				//If the current y line is filled, we can delete them
				
				if(blockCount == 12) { 
					
					effectCounterOn = true;
					effectY.add(y);
					
					
					for(int i = staticBlocks.size()-1; i > -1; i--) {
						//remove all the blocks in the current line
						
						if(staticBlocks.get(i).y == y)
							staticBlocks.remove(i);
					}
					
					lineCount++;
					lines++;
					
					///If line score hits certain number, increase the drop speed
					
					//1 is fastest
					
					if(lines % 10 == 0 && dropInterval > 1) {
						level++;
						if(dropInterval > 10) {
							dropInterval -= 10;
						}
						else {
							dropInterval -= 1;
						}
					}
					//Slide down one line
					
					for(int i = 0; i < staticBlocks.size(); i++) {
						if(staticBlocks.get(i).y < y) {
							staticBlocks.get(i).y += Block.SIZE;
				}
				
					}
				}
				
				blockCount = 0;
				x = leftX;
				y += Block.SIZE;
			}
		}
		
		//Score
		if(lineCount > 0) {
			GamePanel.se.play(1, false);
			int singleLineScore = 10 * level;
			score += singleLineScore * lineCount;
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
		
		//Draw Score
		
		g2D.drawRect(x, topY, 250, 300);
		x += 40;
		y = topY + 90;
		g2D.drawString("LEVEL: "+ level, x, y); y += 70;
		g2D.drawString("LINES: "+lines, x, y);  y += 70;
		g2D.drawString("SCORE: "+score, x, y);
		
		
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
		
		//Draw Effect
		
		if(effectCounterOn) {
			effectCounter++;
			g2D.setColor(Color.red);
			
			for(int i = 0; i < effectY.size(); i++) {
				g2D.fillRect(leftX, effectY.get(i), WIDTH, Block.SIZE);
			}
			
			if(effectCounter == 10) {
				effectCounterOn = false;
				effectCounter = 0;
				effectY.clear();
			}
		}
		
		//draw pause and game over
		
		g2D.setColor(Color.yellow);
		g2D.setFont(g2D.getFont().deriveFont(50f));
		if(gameOver) {
			x = leftX + 25;
			y = topY + 320;
			g2D.drawString("GAME OVER", x, y);
		}
		if(KeyHandler.pausePressed) {
			x = leftX + 70;
			y = topY + 320;
			g2D.drawString("PAUSED", x, y);
		}
		
		//Draw the game title
		
		x = 55;
		y = topY + 320;
		
		g2D.setColor(Color.white);
		g2D.setFont(new Font("Times New Roman", Font.ITALIC, 60));
		g2D.drawString("Simple Tetris", x, y);
	}
}
