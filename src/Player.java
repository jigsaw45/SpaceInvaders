import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Player {
    private int x, y;
    private int width = 40;
    private int height = 40;
    private int speed = 8;

    public Player(int startX, int startY){
        x = startX;
        y = startY;
    }

    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    public void moveLeft(){
        if(x > 0) {
            x -= speed;
        }
    }

    public void moveRight(){
        if(x+width < 800) {
            x += speed;
        }
    }

    public int getSpeed(){
        return speed;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
    public int getX(){return x;}
    public int getY(){return y;}
}
