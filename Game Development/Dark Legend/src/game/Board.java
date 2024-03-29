package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import game.models.Boss;
import game.models.Chests;
import game.models.ColliderBox;
import game.models.Door;
import game.models.Lever;
import game.models.Player;
import game.models.Scene;

public class Board extends JPanel implements ActionListener, Runnable{			//Arquivo onde acontece todas as acoes do jogo
	private static final long serialVersionUID = 1L;
	//Criacao da variaveis
	private Timer timer;
	private Boss boss;
	public Player box;
	public Rectangle playerHitBox;
	private Rectangle leverHitBox;
	private Rectangle chestHitBox, chestHitBox1;
	private Rectangle bossHitBox;
	private Chests chest;
	private Lever lever;
	private Scene scene;
	private Image keyImage;
	private Door door;
	private Rectangle doorHitBox;
	public Rectangle r2;
	private Image gameOverImage;
	private Image potion;
	private Image endGame;
	private List<ColliderBox> hitBox;
	private int camX, camY;
	private int offsetMaxX = 800 - 335;
	private int offsetMaxY = 600 - 350;
	private int offsetMinX = 0;
	private int offsetMinY = 0;
	private final int DELAY = 10;
	public static boolean collided = false;
	private final int[][] pos = {
			{0, 0, 799, 40}, {0, 0, 43, 599}, {0, 557, 799, 40}, {542, 462, 40, 137}, {757, 507, 40, 90},
			{757, 0, 40, 448}, {328, 207, 356, 40}, {542, 329, 215, 40}, {542, 329, 40, 54}, {285, 0, 40, 340},
			{43, 474, 162, 81}};										// PosicaoX, posicaoY, Largura, Altura.
																		// Cada chave contem o posicionamento de cada hitbox
	
	//Inicio do codigo
	public Board() {
		addKeyListener(new TAdapter());									//Instanciando o teclado
		setPreferredSize(new Dimension(350, 250));
		setFocusable(true);												//Setar o foco para a janela do jogo ao iniciar
		
		initHitBox();													//Chamando o metodo que posiciona todas as hitbox nas paredes
		
																		//Instanciando um monte de coisas
		boss = new Boss();												//Inimigo
		scene = new Scene();											//Cenario
		box = new Player();										//Player
		lever = new Lever();											//Alavanca
		chest = new Chests();											//Baus
		door = new Door();												//Porta
																		//Criando algumas imagens
		ImageIcon imageKey = new ImageIcon(Board.class.getResource("/Key.png"));
		keyImage = imageKey.getImage();
		ImageIcon imagePotion = new ImageIcon(Board.class.getResource("/Potion.png"));
		potion = imagePotion.getImage();

																		//Isso aqui mantem tudo funcionando em seu devido tempo
		timer = new Timer(DELAY, this);
		timer.start();
		/*
		String sql = "UPDATE objective SET done=0 WHERE id=1";
		String sql1 = "UPDATE objective SET done=0 WHERE id=2";
		String sql2 = "UPDATE objective SET done=0 WHERE id=3";
		//Prepara a instrucao SQL
		try {
			PreparedStatement ps = BD.createConnection().prepareStatement(sql);
			ps.executeUpdate();
			ps = BD.createConnection().prepareStatement(sql1);
			ps.executeUpdate();
			ps = BD.createConnection().prepareStatement(sql2);
			ps.executeUpdate();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}*/	
	}
	
	private void initHitBox() {											//Cria todas as hitbox em seu devido lugar
		hitBox = new ArrayList<>();
		for (int[] p : pos) {
				hitBox.add(new ColliderBox(p[0], p[1], p[2], p[3]));				
		}
	}
	@Override
	public void paint(Graphics g) {										//Desenha tudo que aparece na tela
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Toolkit.getDefaultToolkit().sync();
		g2d.translate(-camX, -camY);																					//Movimentacao da camera com o player
		g2d.drawImage(scene.getImage(), scene.getX(), scene.getY(), this);												//Desenha o cenario
		if(Chests.openChest) {
			g2d.drawImage(chest.getImage(), chest.getX(), chest.getY(), chest.getWidth(), chest.getHeight(), this);		//Desenha o Bau 1
			g2d.drawImage(keyImage, box.getX() + 50, box.getY() + 40, 25, 15, null);									//Desenha a Chave
		}
		if(Chests.openChest1) {
			g2d.drawImage(chest.getImage(), chest.getX1(), chest.getY1(), chest.getWidth(), chest.getHeight(), this);	//Desenha o bau 2
			g2d.drawImage(potion, box.getX() + 48, box.getY() + 40, 15, 25, null);										//Desenha a pocao
		}
		g2d.drawImage(box.getImage(), box.getX(), box.getY(), box.getWidth(), box.getHeight(), this);					//Desenha o Player
		g2d.drawImage(lever.getImage(), lever.getX(), lever.getY(), lever.getWidth(), lever.getHeight(), this);			//Desenha a alavanca
		g2d.drawImage(door.getImage(), door.getX(), door.getY(), this);													//Desenha a porta
		g2d.drawImage(lever.getDoorImage(), lever.getDx(), lever.getDy(), this);										//Desenha a porta do Boss
		if(Boss.bossAlive) {			
			g2d.drawImage(boss.getImage(), boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight(), this);			//Se o boss estiver vivo: Desenha o Boss
		}
		g2d.drawImage(gameOverImage, camX, camY, 320, 313, null);														//Desenha a tela de GameOver
		g2d.drawImage(endGame, camX, camY, 320, 313, null);																//Desenha a tela de Fim de Jogo
		g.dispose();																									//Desenha
		repaint();																										//Atualiza os Desenhos
	}
	public void actionPerformed(ActionEvent e) {
		step();
	}
	private void step() {												//Metodo que atualiza a cada movimento do player
		box.boxMove();
		playerHitBox = box.getBounds(box.getX(), box.getY());			//Cria a hitbox do player
		checkCollision();												//Verifica se houve colisao
		
		//Sistema complexo de Camera
		
		camX = box.getX() - 335 / 2;
		camY = box.getY() - 350 / 2;
		
		if (camX > offsetMaxX) {
		    camX = offsetMaxX;
		}
		else if (camX < offsetMinX) {
		    camX = offsetMinX;
		}
		if (camY > offsetMaxY) {
		    camY = offsetMaxY;
		}
		else if (camY < offsetMinY) {
		    camY = offsetMinY;
		}
		repaint();
	}
	public void checkCollision() {										//Checa se houve colisao (acaba na linha 198)
		//Instanciando um monte de HitBox
		leverHitBox = lever.getBounds();
		chestHitBox = chest.getBounds();
		chestHitBox1 = chest.getBounds1();
		doorHitBox = door.getBounds();
		bossHitBox = boss.getBounds();
		Rectangle exit = new Rectangle(750, 449, 50, 58);
		Rectangle bossDoorHitBox = lever.getBoundsDoor();
		
		for (ColliderBox hitBox : hitBox) {
			r2 = hitBox.getBounds();
			if(playerHitBox.intersects(r2)) {							//Sistema de colisao (bugado)
				collided = true;
				if(Player.lastMove == "UP") {
					playerHitBox = box.getBounds(box.getX(), box.getY() + 2);
				} else if(Player.lastMove == "DOWN") {
					playerHitBox = box.getBounds(box.getX(), box.getY() - 2);
				} else if(Player.lastMove == "LEFT") {
					playerHitBox = box.getBounds(box.getX() + 2, box.getY());
				} else if(Player.lastMove == "RIGHT") {
					playerHitBox = box.getBounds(box.getX() - 2, box.getY());
				}
			}else {
				collided = false;
			}
		}
		if(playerHitBox.intersects(bossHitBox) && !Chests.openChest1 && Boss.bossAlive) {
			//Game Over
			ImageIcon gameOverii = new ImageIcon(Board.class.getResource("/YouDied.png"));
			gameOverImage = gameOverii.getImage();
		} 
		if(playerHitBox.intersects(exit) && !Chests.openChest && !Chests.openChest1) {
			ImageIcon endImage = new ImageIcon(Board.class.getResource("/End.png"));
			endGame = endImage.getImage();
		}
		
		if(playerHitBox.intersects(doorHitBox)) {
			if(Player.lastMove == "UP") {
				playerHitBox = box.getBounds(box.getX(), box.getY() + 2);
				door.useDoor(Chests.openChest);
			}
		}
		if(playerHitBox.intersects(bossDoorHitBox) && !Lever.leverActive) {
			if(Player.lastMove == "RIGHT") {
				playerHitBox = box.getBounds(box.getX() -2, box.getY());
			}
		}
	}
	
	private class TAdapter extends KeyAdapter{	//Verifica se foi apertado alguma tecla no teclado
												//Atencao: Nesta parte deve ser inserida os itens no banco de dados
			@Override
			public void keyPressed(KeyEvent e) {
				box.keyPressed(e);
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_SPACE) {					//Se apertar espaco...
					if(playerHitBox.intersects(leverHitBox)) {						//e estiver colidindo com a alavanca
						Lever.leverActive = true;									//ativa a alavanca
						lever.useLever();
					}											//Se apertar espaco...
					if(playerHitBox.intersects(bossHitBox) && Chests.openChest1) {	//e estiver colidinho com o boss e estiver com a pocao na mao
						Boss.x = 125;
						Boss.y = 50;												//Boss se afasta
						Chests.openChest1 = false;									//acaba a pocao
						/*String sql = "UPDATE objective SET done=1 WHERE id=3";
						//Prepara a instrucao SQL
						try {
							PreparedStatement ps = BD.createConnection().prepareStatement(sql);
							ps.executeUpdate();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}*/

					}											//Se apertar espaco...
					if(playerHitBox.intersects(chestHitBox)) {						//e estiver colidindo com o bau 1
						chest.chest();												//Pega a chave na mao
						/*String sql = "UPDATE objective SET done=1 WHERE id=1";
						//Prepara a instrucao SQL
						try {
							PreparedStatement ps = BD.createConnection().prepareStatement(sql);
							ps.executeUpdate();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}*/
					}											//Se apertar espaco...
					if(playerHitBox.intersects(chestHitBox1)) {						//e estiver colidindo com o bau 2
						chest.chest1();												//pega a pocao na mao
						/*String sql = "UPDATE objective SET done=1 WHERE id=2";
						//Prepara a instrucao SQL
						try {
							PreparedStatement ps = BD.createConnection().prepareStatement(sql);
							ps.executeUpdate();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}*/											//Adicionar aqui, inserir no banco de dados: item = pocao
					}
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {				//Verifica se alguma tecla foi solta
				box.keyReleased(e);
			}
	}
	public int getXBox() {
		return box.getX();
	}
	public int getYBox() {
		return box.getY();
	}

	@Override
	public void run() {
		
		
	}
}
