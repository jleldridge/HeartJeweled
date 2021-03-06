package main;

import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import engine.*;

public class GameWindow{
	public static void main(String[] args){
		final int FPS = 30;
		final long TIME_PER_FRAME = 1000000000/FPS;
		
		Engine engine = new Engine();
		//Controller controller = new Controller(engine);
		//view.Renderer renderer = new view.Renderer(engine);

		JFrame window = new JFrame("Happy Valentine's Day <3<3<3<3");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setIgnoreRepaint(true);
		
		Canvas canvas = new Canvas();
		canvas.setSize(800, 600);
		canvas.setIgnoreRepaint(true);

		
		canvas.addKeyListener(engine);
		window.add(canvas);
		window.pack();
		window.setVisible(true);

		long startTime=0, elapsedTime=0, 
		     lastTime = System.nanoTime();
		
		BufferedImage screen = new
			BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics g = screen.getGraphics();
		Graphics ag = canvas.getGraphics();
		
		while(true){
			startTime = System.nanoTime();
			elapsedTime = startTime - lastTime;
			
			if(elapsedTime >= TIME_PER_FRAME){
				g.setColor(Color.black);
				g.fillRect(0, 0, 800, 600);

				//render and update here
				engine.update();
				engine.render(g);
				
				ag.drawImage(screen, 0, 0, null);
				lastTime = startTime;
			}
		}
	}
}
