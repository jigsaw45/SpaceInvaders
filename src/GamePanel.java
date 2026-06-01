import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    private Player player;
    private Timer timer;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean isShooting = false;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private int score = 0;
    private int time = 0;
    private int countDown=60000;
    private boolean isGameOver = false;
    private int lives = 3;
    private boolean canShoot = true;

    private Sprite gameOverImage;
    private Sprite backgroundImage;
    private Sprite playerImage;
    private Sprite bulletImage;
    private Sprite enemyImage;

    public GamePanel(){
        setPreferredSize(new Dimension(800, 600));

        gameOverImage = new Sprite("gameover.png");
        backgroundImage = new Sprite("beautiful-shining-stars-night-sky.jpg");
        playerImage = new Sprite("image-removebg-preview.png");
        bulletImage = new Sprite("ece9923d1a76efc5720579e31715d58c.png");
        enemyImage = new Sprite(("download-removebg-preview.png"));

        player = new Player(380, 500);
        timer = new Timer(16, this);

        timer.start();
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if(key == KeyEvent.VK_LEFT) movingLeft = true;
                if(key == KeyEvent.VK_RIGHT) movingRight = true;
                if(key == KeyEvent.VK_SPACE){
                    isShooting = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                int key = e.getKeyCode();
                if(key == KeyEvent.VK_LEFT) movingLeft = false;
                if(key == KeyEvent.VK_RIGHT) movingRight = false;
                if(key == KeyEvent.VK_SPACE) {
                    canShoot = true;
                    isShooting = false;
                }
            }
        });
    }

    public int ranNum(int min, int max){
        return (int) (Math.random()*(max-min + 1))+min;
    }

    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (backgroundImage!=null){
            g.drawImage(backgroundImage.getImage(),0,0,800,600,this);
        }
        if (isGameOver||lives==0){
            g.drawImage(gameOverImage.getImage(),0,0,800,600, this);
            g.setColor(Color.RED);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            g.drawString("YOU HAVE LOST -SCORE:" + score+"-", 50, 150);
            g.drawString("-SCORE:" + score+"-", 50, 275);
        }else {
            g.drawImage(playerImage.getImage(),player.getX(),player.getY(),player.getWidth(),player.getHeight(),this);
            for (Bullet b : bullets) {
                g.drawImage(bulletImage.getImage(),b.getX(),b.getY(),20,20,this);
            }

            for(Enemy e : enemies) {
                if (e.isAlive()){
                    g.drawImage(enemyImage.getImage(),e.getX(),e.getY(),40,60,this);
                }
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            g.drawString("Score " + score, 10, 30);
            g.setColor(Color.RED);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
            g.drawString("HEARTS: " + lives, 10, 65);
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
            g.drawString("TIMER OF DEATH -- " + countDown / 1000 + " -- ", 250, 50);
        }
    }

    private void update() {
        if (isGameOver||lives==0) {
            return;
        }else {

            time += 16;
            if (time % 1600 == 0) {
                enemies.add(new Enemy(ranNum(0, 800), 0));
                for(int i =0;i<time/16000;i++){
                    enemies.add(new Enemy(ranNum(0, 800), 0));
                }
            }

            countDown -= 16;
            if (countDown <= 0) {
                countDown = 0;
                isGameOver = true;
                timer.stop(); // Stop the loop execution
                repaint();    // Trigger final repaint to show image
                return;
            }


            if (movingRight) player.moveRight();
            if (movingLeft) player.moveLeft();
            if (isShooting && canShoot) {
                Bullet bullet = new Bullet(player.getX() + player.getWidth() / 2, player.getY());
                bullets.add(bullet);
                isShooting = false;
                canShoot = false;
            }
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet b = bullets.get(i);
                boolean hit = false;
                b.moveUp();

                if (b.isOffScreen()) {
                    bullets.remove(i);
                }
            }
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet b = bullets.get(i);
                boolean hit = false;
                for (int row = 0; row < enemies.size(); row++) {
                    Enemy e = enemies.get(row);
                    if (e != null && e.isAlive() && b.getBounds().intersects(e.getBounds())) {
                        e.setAlive(false);
                        bullets.remove(i);
                        score += 100;
                        hit = true;
                        break;
                    }
                }
            }
            for (Enemy enemy : enemies) {
                if(enemy.isAlive()){
                    enemy.moveDownAndReverse();
                }
                if(enemy.checkLost()){
                    lives--;
                    enemy.setAlive(false);
                }
            }
            for (int i = enemies.size() - 1; i >= 0; i--) {
                if (!enemies.get(i).isAlive()) {
                    enemies.remove(i);
                }
            }
        }
    }

    @Override public void actionPerformed(ActionEvent e){
        update();
        repaint();
    }
}