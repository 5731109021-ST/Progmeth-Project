package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic.GameBackground;
import logic.GameLogic;
import logic.IRenderable;
import logic.RenderableHolder;
import res.Resource;
import sun.audio.AudioPlayer;

public class GameScreen extends JPanel{
	
	private int startDelayCounter = 0;
	private final int startDelay = 30;
	private int countDownNumber = 4;
	private int winDelay = 300;
	private int winDelayCounter = 0;
	public static boolean isStart;
	public static boolean isPaused;
	public static boolean isGameOver;
	public static boolean isWin;
	public static boolean gameOverScreen;
	
	public GameScreen() {
		isStart = false;
		isPaused = false;
		isGameOver = false;
		isWin = false;
		gameOverScreen = false;
		this.setPreferredSize(new Dimension(GameWindow.SCREEN_WIDTH , GameWindow.SCREEN_HEIGHT));
		setDoubleBuffered(true);
		
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				InputUtility.setKeyPressed(e.getKeyCode(), false);
				InputUtility.setKeyTriggered(e.getKeyCode(),false);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_SPACE && !InputUtility.getKeyPressed(KeyEvent.VK_SPACE) && isStart && !gameOverScreen){
					InputUtility.setKeyPressed(e.getKeyCode(), true);
					InputUtility.setKeyTriggered(e.getKeyCode(),true);
				}
				if(e.getKeyCode()== KeyEvent.VK_W && !InputUtility.getKeyPressed(KeyEvent.VK_W) && isStart && !isGameOver){
					synchronized (RenderableHolder.getInstance().getRenderableList()) {
						RenderableHolder.getInstance().getRenderableList().notifyAll();
					}
					isPaused = !isPaused;
					Resource.pause.play();
					InputUtility.setKeyPressed(e.getKeyCode(), true);
					InputUtility.setKeyTriggered(e.getKeyCode(),true);
				}
			}
		});
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				InputUtility.setMouseLeftDown(false);
				InputUtility.setMouseLeftTriggered(false);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e) && !InputUtility.isMouseLeftDown() && isStart){
					InputUtility.setMouseLeftDown(true);
					InputUtility.setMouseLeftTriggered(true);
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				InputUtility.setMouseOnScreen(false);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				InputUtility.setMouseOnScreen(true);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		this.addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				InputUtility.setMouseX(e.getX());
				InputUtility.setMouseY(e.getY());
				
			}
			public void mouseDragged(MouseEvent e)
			{
				InputUtility.setMouseX(e.getX());
				InputUtility.setMouseY(e.getY());
			}
		});
	}
	
	public void resetParameter(){
		isStart = false;
		isPaused = false;
		isGameOver = false;
		isWin = false;
		gameOverScreen = false;
		winDelay = 200;
		winDelayCounter = 0;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		if(!isStart && !isPaused){
			for(IRenderable object : RenderableHolder.getInstance().getRenderableList()) {
				g2d.setComposite(GameBackground.transcluentBlack);
				object.draw(g2d);
			}
			g2d.setComposite(GameBackground.opaque);
			if(startDelayCounter < startDelay && countDownNumber==4){
				startDelayCounter++;
				g2d.setColor(Color.BLUE);
				g2d.setFont(Resource.countDownFont);
				g2d.drawString("GET READY", 160, 320);
			}
			else if(startDelayCounter < startDelay && countDownNumber < 4){
				startDelayCounter++;
				g2d.setColor(Color.BLUE);
				g2d.setFont(Resource.countDownFont);
				g2d.drawString(Integer.toString(countDownNumber), 380, 320);
			}
			else if(startDelayCounter >= startDelay && countDownNumber==1){
				isStart = true;
				GameManager.playSoundThread();
				startDelayCounter = 0;
				countDownNumber = 4;
			}
			else{
				startDelayCounter = 0;
				countDownNumber--;
			}
		}
		if(isStart && !isPaused){
			for(IRenderable object : RenderableHolder.getInstance().getRenderableList()) {
				object.draw(g2d);
			}
		}
		if(isPaused){
			for(IRenderable object : RenderableHolder.getInstance().getRenderableList()) {
				g2d.setComposite(GameBackground.transcluentBlack);
				object.draw(g2d);
			}
			g2d.setComposite(GameBackground.opaque);
			g2d.setColor(Color.BLUE);
			g2d.setFont(Resource.pauseFont);
			g2d.drawString("PAUSE", 270, 320);
		}
		if(isGameOver && !gameOverScreen){
			GameLogic.getInstance().player.draw(g2d);
		}
		if(isGameOver && gameOverScreen){
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, GameWindow.SCREEN_WIDTH,  GameWindow.SCREEN_HEIGHT);
			g2d.setFont(Resource.pauseFont);
			g2d.setColor(Color.WHITE);
			g2d.drawString("GAME OVER", 140, 320);
		}
		if(isWin && !isGameOver){
			if(winDelayCounter < winDelay){
				winDelayCounter++;
			}
			else{
				g2d.drawImage(Resource.win, null, 300, 220);
				g2d.drawImage(Resource.congrat, null, 10, 120);
			}
		}
	}
}

