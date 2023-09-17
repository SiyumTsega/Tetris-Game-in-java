package minos;

import java.awt.Color;
import java.awt.Graphics2D;

public class Block {

	public int x, y;
	public static final int SIZE = 30;
	public Color c;

	public Block(Color c) {

		this.c = c;
	}

	public void draw(Graphics2D g2D) {
		
		int margin = 2;
		g2D.setColor(c);
		g2D.drawRect(x + margin, y + margin, SIZE - (margin*2), SIZE - (margin*2));
	}
}
