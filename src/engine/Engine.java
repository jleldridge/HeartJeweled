package engine;

import model.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.File;

public class Engine implements KeyListener{
	final int CELL_SIZE = 50;
	public BufferedImage redHeartSprite, blueHeartSprite, yellowHeartSprite, purpleHeartSprite, pinkHeartSprite;
	public BufferedImage background;
	public Heart[][] gameGrid;
	public int sRow, sCol;
	public int mRow, mCol;
	int rows, cols;
	public Stack<Integer> directionKeyDown;
	public boolean selecting;
	int score, multiplier;

	public Engine(){
		rows = 600/CELL_SIZE;
		cols = 800/CELL_SIZE;
		sRow = mRow = rows/2; mCol = sCol = cols/2;
		gameGrid = new Heart[rows][cols];
		directionKeyDown = new Stack<Integer>();
		try{
			background = ImageIO.read(getClass().getResource("/resources/HeartJeweledBackground.png"));
			redHeartSprite = ImageIO.read(getClass().getResource("/resources/redHeart.png"));
			blueHeartSprite = ImageIO.read(getClass().getResource("/resources/blueHeart.png"));
			purpleHeartSprite = ImageIO.read(getClass().getResource("/resources/purpleHeart.png"));
			pinkHeartSprite = ImageIO.read(getClass().getResource("/resources/pinkHeart.png"));
			yellowHeartSprite = ImageIO.read(getClass().getResource("/resources/yellowHeart.png"));
		}catch(Exception e){
			e.printStackTrace();
		}

		generateHeartGrid();
		score = 0;
		multiplier = 1;
	}

	public void update(){
		//calculate destruction and gravity
		boolean processing = process();
		
		if(!processing){
			multiplier = 1;
			//process input
			if(!directionKeyDown.empty()){
				int code = directionKeyDown.peek();
				if(code == KeyEvent.VK_UP){
					sRow = (sRow - 1) % rows;
				}else if(code == KeyEvent.VK_DOWN){
					sRow = (sRow + 1) % rows;
				}else if(code == KeyEvent.VK_LEFT){
					sCol = (sCol - 1) % cols;
				}else if(code == KeyEvent.VK_RIGHT){
					sCol = (sCol + 1) % cols;
				}
				directionKeyDown.clear();
			}
			if(selecting){
				if(mRow >= 0 && mCol >= 0 && (Math.abs(sRow - mRow) == 1) && (Math.abs(sCol - mCol) == 0)){
					Heart temp;
					
					temp = gameGrid[sRow][sCol];
					gameGrid[sRow][sCol] = gameGrid[mRow][mCol];
					gameGrid[mRow][mCol] = temp;
					
					mRow = -1;
					mCol = -1;
				}else if(mRow >= 0 && mCol >= 0 && (Math.abs(sRow - mRow) == 0) && (Math.abs(sCol - mCol) == 1)){
					Heart temp;

					temp = gameGrid[sRow][sCol];
					gameGrid[sRow][sCol] = gameGrid[mRow][mCol];
					gameGrid[mRow][mCol] = temp;

					mRow = -1;
					mCol = -1;
				}else{
					mRow = sRow;
					mCol = sCol;
				}
				selecting = false;
			}
		}

	}

	public boolean process(){
		boolean processing = false;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				//gravity
				if(i < rows-1 && gameGrid[i][j] != null && gameGrid[i+1][j] == null){
					gameGrid[i+1][j] = gameGrid[i][j];
					gameGrid[i][j] = null;
					return true;
				}
				//row of 3
				if(j < cols - 2){
					if(gameGrid[i][j] != null && gameGrid[i][j+1] != null && gameGrid[i][j+2] != null){
						if(gameGrid[i][j].color.equals(gameGrid[i][j+1].color) &&
							gameGrid[i][j].color.equals(gameGrid[i][j+2].color)){
								gameGrid[i][j] = null;
								gameGrid[i][j+1] = null;
								gameGrid[i][j+2] = null;
								score += (multiplier*100);
								multiplier++;
								return true;
						}
					}
				}
				if(i < rows - 2){
					if(gameGrid[i][j] != null && gameGrid[i+1][j] != null && gameGrid[i+2][j] != null){
						if(gameGrid[i][j].color.equals(gameGrid[i+1][j].color) &&
							gameGrid[i][j].color.equals(gameGrid[i+2][j].color)){
								gameGrid[i][j] = null;
								gameGrid[i+1][j] = null;
								gameGrid[i+2][j] = null;
								score += (multiplier*100);
								multiplier++;
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void render(Graphics g){
		g.drawImage(background, 0, 0, null);

		for(int i = 0; i < rows; i++){
			g.setColor(Color.white);
			g.fillRect(0, i*CELL_SIZE, 800, 1);	
			for(int j = 0; j < cols; j++){
			
				g.fillRect(j*CELL_SIZE, 0, 1, 600);
			}
		}

		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				if(gameGrid[i][j] != null){
					g.drawImage(gameGrid[i][j].sprite, j*50 + 7, i*50 + 2, null);
				}
			}
		}
		g.setColor(Color.green);
		g.fillRect(mCol*50 + 20, mRow*50 + 20, 10, 10);
		g.setColor(Color.blue);
		g.fillRect(sCol*50 + 20, sRow*50 + 20, 10, 10);
		g.setColor(Color.green);
		g.drawString("Score: " + score, 10, 15);
		g.drawString("Multiplier: " + multiplier, 10, 30);
	}

	public void keyPressed(KeyEvent e){
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN || 
				code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT){
			if(!directionKeyDown.contains(Integer.valueOf(code))){
				directionKeyDown.push(Integer.valueOf(code));
			}
		}else{
			selecting = true;
		}
	}

	public void keyReleased(KeyEvent e){
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN || 
				code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT){
			directionKeyDown.remove(Integer.valueOf(code));
		}
	}

	public void keyTyped(KeyEvent e){
	}

	public Heart getHeart(String color){
		if(color.equals("red")){
			return new Heart("red", redHeartSprite);
		}else if(color.equals("blue")){
			return new Heart("blue", blueHeartSprite);
		}else if(color.equals("yellow")){
			return new Heart("yellow", yellowHeartSprite);
		}else if(color.equals("purple")){
			return new Heart("purple", purpleHeartSprite);
		}else if(color.equals("pink")){
			return new Heart("pink", pinkHeartSprite);
		}

		return null;
	}

	public void generateHeartGrid(){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				int random = (int)(Math.random() * (5));
				switch(random){
					case 0:
						gameGrid[i][j] = getHeart("red");
						break;
					case 1:
						gameGrid[i][j] = getHeart("blue");
						break;
					case 2:
						gameGrid[i][j] = getHeart("yellow");
						break;
					case 3:
						gameGrid[i][j] = getHeart("purple");
						break;
					case 4:
						gameGrid[i][j] = getHeart("pink");
						break;
				}
			}
		}
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < rows; j++){
				if(j < cols - 2){
					if(gameGrid[i][j] != null && gameGrid[i][j+1] != null && gameGrid[i][j+2] != null){
						if(gameGrid[i][j].color.equals(gameGrid[i][j+1].color) &&
							gameGrid[i][j].color.equals(gameGrid[i][j+2].color)){
							if(gameGrid[i][j].color.equals("pink")){
								gameGrid[i][j+1] = getHeart("blue");
							}else{
								gameGrid[i][j+1] = getHeart("pink");
							}
						}
					}
				}
				if(i < rows - 2){
					if(gameGrid[i][j] != null && gameGrid[i+1][j] != null && gameGrid[i+2][j] != null){
						if(gameGrid[i][j].color.equals(gameGrid[i+1][j].color) &&
							gameGrid[i][j].color.equals(gameGrid[i+2][j].color)){
							if(gameGrid[i][j].color.equals("red")){
								gameGrid[i+1][j] = getHeart("yellow");
							}else{
								gameGrid[i+1][j] = getHeart("red");
							}
						}
					}
				}
			}
		}
	}
}
