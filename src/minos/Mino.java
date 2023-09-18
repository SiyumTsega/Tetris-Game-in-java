package minos;

import java.awt.Color;
import java.awt.Graphics2D;

import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;

public class Mino {

	public Block[] b = new Block[4];
	public Block[] tempB = new Block[4];
	int autoDropCounter = 0;
	public int direction = 1; //there are four directions 1/2/3/4
	boolean rightCollision, leftCollision, bottomCollision;
	public boolean active = true;
	public boolean deactivating;
	int deactivateCounter = 0;
	
	public void create(Color c) {
		
		b[0] = new Block(c);
		b[1] = new Block(c);
		b[2] = new Block(c);
		b[3] = new Block(c);
		tempB[0] = new Block(c);
		tempB[1] = new Block(c);
		tempB[2] = new Block(c);
		tempB[3] = new Block(c);
	}
	
	public void setXY(int x, int y) {
		
	}
	
	public void updateXY(int direction) {
		
		checkRotationCollision();
		
		if(leftCollision == false && rightCollision == false && bottomCollision == false) {
			
			this.direction = direction;
			b[0].x = tempB[0].x;
			b[0].y = tempB[0].y;
			b[1].x = tempB[1].x;
			b[1].y = tempB[1].y;
			b[2].x = tempB[2].x;
			b[2].y = tempB[2].y;
			b[3].x = tempB[3].x;
			b[3].y = tempB[3].y;
		}
		
	}
	
	public void getDirection1() {}
	public void getDirection2() {}
	public void getDirection3() {}
	public void getDirection4() {}
	
	public void checkMovementCollision() {
		
		leftCollision = false;
		rightCollision = false;
		bottomCollision = false;
		
		//Check static block collision
		
				checkStaticBlockCollision();
		
		//Check Frame collision
		//Left wall
		
		for(int i = 0; i < b.length; i++) {
			if(b[i].x == PlayManager.leftX)
				leftCollision = true;
		}
		
		//Right wall
		
		for(int i = 0; i < b.length; i++) {
			if(b[i].x + Block.SIZE == PlayManager.rightX)
				rightCollision = true;
		}
		
		//Bottom wall
		
		for(int i = 0; i < b.length; i++) {
			if(b[i].y + Block.SIZE == PlayManager.bottomY)
				bottomCollision = true;
		}
	}
	public void checkRotationCollision() {
		
		leftCollision = false;
		rightCollision = false;
		bottomCollision = false;
		
		//Check static block collision
		
		checkStaticBlockCollision();
		
		//Check Frame collision
		//Left wall
		
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].x < PlayManager.leftX)
				leftCollision = true;
		}
		
		//Right wall
		
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].x + Block.SIZE > PlayManager.rightX)
				rightCollision = true;
		}
		
		//Bottom wall
		
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].y + Block.SIZE > PlayManager.bottomY)
				bottomCollision = true;
		}
	}
	
	public void checkStaticBlockCollision() {
		
		for(int i = 0; i < PlayManager.staticBlocks.size(); i++) {
			
			int targetX = PlayManager.staticBlocks.get(i).x;
			int targetY = PlayManager.staticBlocks.get(i).y;
			
			//Check down collision
			
			for(int j = 0; j < b.length; j++) {
				  if(b[j].y + Block.SIZE == targetY && b[j].x == targetX) {
					  bottomCollision = true;
				  }
			}
			
			//Check left collision
			
			for(int j = 0; j < b.length; j++) {
				  if(b[j].x - Block.SIZE == targetX && b[j].y == targetY) {
					  leftCollision = true;
				  }
			}
			
			//Check right collision
			
			for(int j = 0; j < b.length; j++) {
				  if(b[j].x + Block.SIZE == targetX && b[j].y == targetY) {
					  rightCollision = true;
				  }
			}
		}
	}
	                        
	public void update() {
		
		if(deactivating) {
			deactivating();
		}
		
		//Move the mino
		
		if(KeyHandler.upPressed) {
			
				switch (direction) {
				case 1:
					getDirection2();
					break;
				case 2:
					getDirection3();
					break;
				case 3:
					getDirection4();
					break;
				case 4:
					getDirection1();
					break;
			}
			
			KeyHandler.upPressed = false;
			GamePanel.se.play(3, false);
		}
		
		if(KeyHandler.leftPressed) {
				
			if(leftCollision == false) {
				b[0].x -= Block.SIZE;
				b[1].x -= Block.SIZE;
				b[2].x -= Block.SIZE;
				b[3].x -= Block.SIZE;
			}
			
			KeyHandler.leftPressed = false;
		}
		
		checkMovementCollision();
		
		if(KeyHandler.downPressed) {
			
			if(bottomCollision == false) {
				
				b[0].y += Block.SIZE;
				b[1].y += Block.SIZE;
				b[2].y += Block.SIZE;
				b[3].y += Block.SIZE;
				
				//when moved down reset the counter
				autoDropCounter = 0;
			}
			
			KeyHandler.downPressed = false;
		}
		
		if(KeyHandler.rightPressed) {
			
			if(rightCollision == false) {
				b[0].x += Block.SIZE;
				b[1].x += Block.SIZE;
				b[2].x += Block.SIZE;
				b[3].x += Block.SIZE;
			}
			
			
			KeyHandler.rightPressed = false;
		}
		
		if(bottomCollision) {
			if(deactivating == false) {
				GamePanel.se.play(4, false);
			}
			deactivating = true;
		}
		else {
			autoDropCounter++;   ///the counter increases every frame
			if(autoDropCounter == PlayManager.dropInterval) {
				//The mino goes down
				b[0].y += Block.SIZE;
				b[1].y += Block.SIZE;
				b[2].y += Block.SIZE;
				b[3].y += Block.SIZE;
				autoDropCounter = 0;
			}
		}
			
	}
	
	
	private void deactivating() {
		
		deactivateCounter++;
		
		//Wait for 45 mins until deactivate
		
		if(deactivateCounter == 45) {
			deactivateCounter = 0;
			
			checkMovementCollision();
			
			//If bottom collision is still true, deactivate 
			
			if(bottomCollision) {
				active = false;
			}
		}
	}
	public void draw(Graphics2D g2D) {
		
		int margin = 2;
		g2D.setColor(b[0].c);
		g2D.fillRect(b[0].x + margin, b[0].y + margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2D.fillRect(b[1].x + margin, b[1].y + margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2D.fillRect(b[2].x + margin, b[2].y + margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2D.fillRect(b[3].x + margin, b[3].y + margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
	}
}
