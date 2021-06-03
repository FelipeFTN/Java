package game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class RectangleBox extends JPanel{
	private Image image;
	private int x, y, dx, dy, w, h;
	public static String lastMove;
	
	public RectangleBox() {
		playerImage();
	}
	public void playerImage() {
		ImageIcon ii = new ImageIcon("res//PlayerResized.png");
		image = ii.getImage();
		
		w = image.getWidth(null);
		h = image.getHeight(null);
	}
	public void boxMove() {
		x += dx;
		y += dy;
	}
	
	//Controles
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_UP) {
			dy = -2;
			lastMove = "UP";
		}
		if(key == KeyEvent.VK_DOWN) {
			dy = 2;
			lastMove = "DOWN";
		}
		if(key == KeyEvent.VK_LEFT) {
			dx = -2;
			lastMove = "LEFT";
		}
		if(key == KeyEvent.VK_RIGHT) {
			dx = 2;
			lastMove = "RIGHT";
		}
	}
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_UP) {
			dy = 0;
		}
		if(key == KeyEvent.VK_DOWN) {
			dy = 0;
		}
		if(key == KeyEvent.VK_LEFT) {
			dx = 0;
		}
		if(key == KeyEvent.VK_RIGHT) {
			dx = 0;
		}
	}
	
	//Metodos get
	
	public Image getImage() {
		return image;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Rectangle getBounds(int x, int y) {
		this.x = x;
		this.y = y;
		return new Rectangle(x, y, w, h);
	}
}