import java.awt.Rectangle;

public class Boss {

    private int x = 300;
    private int y = 50;

    private int width = 200;
    private int height = 100;

    private int health = 50;

    private boolean movingRight = true;

    public void move(){

        if(movingRight){
            x += 4;
        }else{
            x -= 4;
        }

        if(x <= 0){
            movingRight = true;
        }

        if(x + width >= 800){
            movingRight = false;
        }
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }

    public void damage(){
        health--;
    }

    public boolean isDead(){
        return health <= 0;
    }

    public int getHealth(){
        return health;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
}
