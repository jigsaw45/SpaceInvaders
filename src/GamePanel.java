import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
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

    // leaderboard
    private String playerName = "PLAYER";
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<Integer> scores = new ArrayList<>();

    // sprites
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
        bossImage = new Sprite("something.jpeg");

        player = new Player(380, 500);

        timer = new Timer(16, this);
        timer.start();

        loadLeaderboard();

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                if (key == KeyEvent.VK_LEFT) movingLeft = true;
                if (key == KeyEvent.VK_RIGHT) movingRight = true;
                if (key == KeyEvent.VK_SPACE) isShooting = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {

                int key = e.getKeyCode();

                if (key == KeyEvent.VK_LEFT) movingLeft = false;
                if (key == KeyEvent.VK_RIGHT) movingRight = false;
                if (key == KeyEvent.VK_SPACE) isShooting = false;
            }
        });
    }

    // ---------------- GAME LOOP ----------------

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {

        if (isGameOver || lives <= 0) return;

        time += 16;
        countDown -= 16;

        if (countDown <= 0) {
            endGame();
            return;
        }

        spawnEnemies();
        spawnBoss();

        updatePlayer();
        updateBullets();
        updateEnemies();
        updateEnemyBullets();
        updateBoss();
    }

    // ---------------- PLAYER ----------------

    private void updatePlayer() {

        if (movingLeft) player.moveLeft();
        if (movingRight) player.moveRight();

        long now = System.currentTimeMillis();

        if (isShooting && now - lastShot > 200) {

            bullets.add(new Bullet(
                    player.getX() + player.getWidth() / 2,
                    player.getY()
            ));

            lastShot = now;
        }
    }

    // ---------------- BULLETS ----------------

    private void updateBullets() {

        for (int i = bullets.size() - 1; i >= 0; i--) {

            Bullet b = bullets.get(i);
            b.moveUp();

            if (b.isOffScreen()) {
                bullets.remove(i);
                continue;
            }

            for (Enemy e : enemies) {

                if (e.isAlive() && b.getBounds().intersects(e.getBounds())) {

                    e.setAlive(false);
                    bullets.remove(i);
                    score += 100;
                    break;
                }
            }
        }
    }

    // ---------------- ENEMIES ----------------

    private void spawnEnemies() {

        if (time % 1600 == 0) {

            enemies.add(new Enemy(ranNum(0, 760), 0));
        }
    }

    private void updateEnemies() {

        for (Enemy e : enemies) {

            if (!e.isAlive()) continue;

            if (e.getBounds().intersects(player.getBounds())) {
                damagePlayer();
                e.setAlive(false);
                continue;
            }

            e.moveDownAndReverse();

            if (Math.random() < 0.002) {

                enemyBullets.add(new EnemyBullet(
                        e.getX() + e.getWidth() / 2,
                        e.getY() + e.getHeight()
                ));
            }
        }

        enemies.removeIf(e -> !e.isAlive());
    }

    // ---------------- ENEMY BULLETS ----------------

    private void updateEnemyBullets() {

        for (int i = enemyBullets.size() - 1; i >= 0; i--) {

            EnemyBullet b = enemyBullets.get(i);

            b.moveDown();

            if (b.isOffScreen()) {
                enemyBullets.remove(i);
                continue;
            }

            if (b.getBounds().intersects(player.getBounds())) {

                enemyBullets.remove(i);
                damagePlayer();
            }
        }
    }

    // ---------------- BOSS ----------------

    private void spawnBoss() {

        if (score >= 5000 && !bossSpawned) {
            boss = new Boss();
            bossSpawned = true;
        }
    }

    private void updateBoss() {

        if (boss == null) return;

        boss.move();

        for (int i = bullets.size() - 1; i >= 0; i--) {

            Bullet b = bullets.get(i);

            if (b.getBounds().intersects(boss.getBounds())) {

                boss.damage();
                bullets.remove(i);

                if (boss.isDead()) {

                    score += 10000;
                    boss = null;
                }

                return;
            }
        }
    }

    // ---------------- DAMAGE ----------------

    private void damagePlayer() {

        lives--;

        if (lives <= 0) {
            endGame();
        }
    }

    private void endGame() {

        isGameOver = true;
        timer.stop();

        addScore(playerName, score);
    }

    // ---------------- DRAW ----------------

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(backgroundImage.getImage(), 0, 0, 800, 600, this);

        if (isGameOver) {

            g.drawImage(gameOverImage.getImage(), 0, 0, 800, 600, this);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));

            g.drawString("Score: " + score, 50, 200);

            drawLeaderboard(g);
            return;
        }

        g.drawImage(playerImage.getImage(),
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight(),
                this);

        for (Bullet b : bullets) {
            g.drawImage(bulletImage.getImage(), b.getX(), b.getY(), 20, 20, this);
        }

        for (Enemy e : enemies) {
            g.drawImage(enemyImage.getImage(), e.getX(), e.getY(), 40, 60, this);
        }

        for (EnemyBullet b : enemyBullets) {
            g.setColor(Color.YELLOW);
            g.fillRect(b.getX(), b.getY(), 8, 16);
        }

        if (boss != null) {
            g.drawImage(bossImage.getImage(),
                    boss.getX(),
                    boss.getY(),
                    boss.getWidth(),
                    boss.getHeight(),
                    this);
        }

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
        g.drawString("Time: " + countDown / 1000, 10, 60);
    }

    // ---------------- LEADERBOARD ----------------

    private void loadLeaderboard() {

        try {

            File f = new File("leaderboard.txt");

            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));

            String line;

            while ((line = br.readLine()) != null) {

                String[] p = line.split(" ");

                names.add(p[0]);
                scores.add(Integer.parseInt(p[1]));
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addScore(String name, int score) {

        names.add(name);
        scores.add(score);

        for (int i = 0; i < scores.size(); i++) {
            for (int j = i + 1; j < scores.size(); j++) {

                if (scores.get(j) > scores.get(i)) {

                    int t = scores.get(i);
                    scores.set(i, scores.get(j));
                    scores.set(j, t);

                    String tn = names.get(i);
                    names.set(i, names.get(j));
                    names.set(j, tn);
                }
            }
        }

        while (scores.size() > 5) {
            scores.remove(scores.size() - 1);
            names.remove(names.size() - 1);
        }

        saveLeaderboard();
    }

    private void saveLeaderboard() {

        try {

            FileWriter w = new FileWriter("leaderboard.txt");

            for (int i = 0; i < names.size(); i++) {
                w.write(names.get(i) + " " + scores.get(i) + "\n");
            }

            w.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawLeaderboard(Graphics g) {

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.CYAN);

        g.drawString("LEADERBOARD:", 500, 150);

        for (int i = 0; i < names.size(); i++) {

            g.drawString(
                    (i + 1) + ". " + names.get(i) + " - " + scores.get(i),
                    500,
                    180 + i * 25
            );
        }
    }

    // ---------------- UTIL ----------------

    public int ranNum(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}