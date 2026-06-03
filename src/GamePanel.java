import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {

    private Player player;
    private Timer timer;

    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean isShooting = false;

    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();

    private int score = 0;
    private int time = 0;
    private int countDown = 60000;
    private int lives = 3;

    private boolean isGameOver = false;

    private long lastShot = 0;

    private Boss boss = null;
    private boolean bossSpawned = false;

    private Sprite gameOverImage;
    private Sprite backgroundImage;
    private Sprite playerImage;
    private Sprite bulletImage;
    private Sprite enemyImage;
    private Sprite bossImage;

    public GamePanel() {

        setPreferredSize(new Dimension(800, 600));

        gameOverImage = new Sprite("gameover.png");
        backgroundImage = new Sprite("beautiful-shining-stars-night-sky.jpg");
        playerImage = new Sprite("image-removebg-preview.png");
        bulletImage = new Sprite("ece9923d1a76efc5720579e31715d58c.png");
        enemyImage = new Sprite("download-removebg-preview.png");
        bossImage = new Sprite("Tung-Tung-Tung-Sahur-Transparent-HQ.png");

        player = new Player(380, 500);

        timer = new Timer(16, this);
        timer.start();

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                if (key == KeyEvent.VK_LEFT) {
                    movingLeft = true;
                }

                if (key == KeyEvent.VK_RIGHT) {
                    movingRight = true;
                }

                if (key == KeyEvent.VK_SPACE) {
                    isShooting = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                int key = e.getKeyCode();

                if (key == KeyEvent.VK_LEFT) {
                    movingLeft = false;
                }

                if (key == KeyEvent.VK_RIGHT) {
                    movingRight = false;
                }

                if (key == KeyEvent.VK_SPACE) {
                    isShooting = false;
                }
            }
        });
    }

    public int ranNum(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, 800, 600, this);
        }

        if (isGameOver || lives <= 0) {

            g.drawImage(gameOverImage.getImage(), 0, 0, 800, 600, this);

            g.setColor(Color.RED);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            g.drawString("YOU HAVE LOST", 50, 150);
            g.drawString("-SCORE: " + score + "-", 50, 275);

            return;
        }

        g.drawImage(
                playerImage.getImage(),
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight(),
                this
        );

        for (Bullet bullet : bullets) {
            g.drawImage(
                    bulletImage.getImage(),
                    bullet.getX(),
                    bullet.getY(),
                    20,
                    20,
                    this
            );
        }

        g.setColor(Color.YELLOW);

        for (EnemyBullet bullet : enemyBullets) {
            g.fillRect(
                    bullet.getX(),
                    bullet.getY(),
                    8,
                    16
            );
        }

        for (Enemy enemy : enemies) {

            if (enemy.isAlive()) {

                g.drawImage(
                        enemyImage.getImage(),
                        enemy.getX(),
                        enemy.getY(),
                        40,
                        60,
                        this
                );
            }
        }

        if (boss != null) {

            g.drawImage(
                    bossImage.getImage(),
                    boss.getX(),
                    boss.getY(),
                    boss.getWidth(),
                    boss.getHeight(),
                    this
            );

            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            g.drawString("BOSS HP: " + boss.getHealth(), 300, 90);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);

        g.setColor(Color.RED);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g.drawString("HEARTS: " + lives, 10, 65);

        g.setColor(Color.ORANGE);
        g.drawString(
                "TIMER OF DEATH -- " + countDown / 1000 + " --",
                250,
                50
        );
    }

    private void update() {

        if (isGameOver || lives <= 0) {
            return;
        }

        time += 16;

        spawnEnemies();
        updateTimer();
        spawnBoss();
        updateBoss();
        updatePlayer();
        updateBullets();
        updateEnemyBullets();
        updateEnemies();
    }

    private void spawnEnemies() {

        if (time % 1600 == 0) {

            enemies.add(new Enemy(ranNum(0, 760), 0));

            for (int i = 0; i < time / 16000; i++) {
                enemies.add(new Enemy(ranNum(0, 760), 0));
            }
        }
    }

    private void updateTimer() {

        countDown -= 16;

        if (countDown <= 0) {

            countDown = 0;
            isGameOver = true;
            timer.stop();
        }
    }

    private void spawnBoss() {

        if (score >= 2500 && !bossSpawned) {

            boss = new Boss();
            bossSpawned = true;
        }
    }

    private void updateBoss() {

        if (boss == null) {
            return;
        }

        boss.move();

        for (int i = bullets.size() - 1; i >= 0; i--) {

            Bullet bullet = bullets.get(i);

            if (bullet.getBounds().intersects(boss.getBounds())) {

                boss.damage();
                bullets.remove(i);

                if (boss.isDead()) {

                    score += 10000;
                    boss = null;
                    return;
                }
            }
        }
    }

    private void updatePlayer() {

        if (movingLeft) {
            player.moveLeft();
        }

        if (movingRight) {
            player.moveRight();
        }

        long currentTime = System.currentTimeMillis();

        if (isShooting && currentTime - lastShot > 200) {

            bullets.add(
                    new Bullet(
                            player.getX() + player.getWidth() / 2,
                            player.getY()
                    )
            );

            lastShot = currentTime;
        }
    }

    private void updateBullets() {

        for (int i = bullets.size() - 1; i >= 0; i--) {

            Bullet bullet = bullets.get(i);

            bullet.moveUp();

            if (bullet.isOffScreen()) {

                bullets.remove(i);
                continue;
            }

            for (Enemy enemy : enemies) {

                if (enemy.isAlive() &&
                        bullet.getBounds().intersects(enemy.getBounds())) {

                    enemy.setAlive(false);
                    bullets.remove(i);
                    score += 100;
                    break;
                }
            }
        }
    }

    private void updateEnemyBullets() {

        for (int i = enemyBullets.size() - 1; i >= 0; i--) {

            EnemyBullet bullet = enemyBullets.get(i);

            bullet.moveDown();

            if (bullet.isOffScreen()) {

                enemyBullets.remove(i);
                continue;
            }

            if (bullet.getBounds().intersects(player.getBounds())) {

                lives--;
                enemyBullets.remove(i);

                if (lives <= 0) {
                    isGameOver = true;
                }
            }
        }
    }

    private void updateEnemies() {

        for (Enemy enemy : enemies) {

            if (!enemy.isAlive()) {
                continue;
            }

            if (enemy.getBounds().intersects(player.getBounds())) {

                lives--;
                enemy.setAlive(false);

                if (lives <= 0) {
                    isGameOver = true;
                }

                continue;
            }

            enemy.moveDownAndReverse();

            if (Math.random() < 0.002) {

                enemyBullets.add(
                        new EnemyBullet(
                                enemy.getX() + enemy.getWidth() / 2,
                                enemy.getY() + enemy.getHeight()
                        )
                );
            }
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {

            if (!enemies.get(i).isAlive()) {
                enemies.remove(i);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        update();
        repaint();
    }
}