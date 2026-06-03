import java.awt.Rectangle;

public class EnemyBullet {

    private int x;
    private int y;

    private int width = 8;
    private int height = 16;

    private int speed = 4;

    public EnemyBullet(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void moveDown(){
        y += speed;
    }

    public boolean isOffScreen(){
        return y > 600;
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}