import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Enemy {
    private int x, y;
    private int width = 40;
    private int height = 60;
    private boolean isAlive;
    private int travelTotal;
    private int travelProgress;
    private boolean travelRight;

    public Enemy(int startX, int startY){
        this.x = startX;
        this.y = startY;
        getDirectionAndDistance();
        travelProgress = 0;
        isAlive=true;
    }

    public int ranNum(int min, int max){
        return (int) (Math.random()*(max-min + 1))+min;
    }

    public void draw(Graphics g){
        if(isAlive){
            g.setColor(Color.red);
            g.fillRect(x,y,width,height);
        }
    }
    public void moveDownAndReverse(){
        if(y>=150&&y<=200){
            if(travelProgress<travelTotal){
                if(travelRight){
                    travelProgress+=4;
                    x+=4;
                }else{
                    travelProgress+=4;
                    x-=4;
                }
            }else{
                y+=2;
            }
        }else{
            y+=2;
        }
    }
    public void getDirectionAndDistance(){
        int temp = ranNum(1,6);

        if(temp==1){
            travelTotal = ranNum(0,x);
            travelRight = false;
        }else{
            travelTotal = ranNum(0,800-x-width);
            travelRight = true;
        }
    }

    public boolean checkLost(){
        return y==600;
    }
    public void setAlive(boolean state){
        isAlive = state;
    }
    public boolean isAlive(){
        return isAlive;
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public int getX(){return x;}
    public int getY(){return y;}
    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }
}
