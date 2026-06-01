import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {
    private int x,y;
    private int width = 5;
    private int height = 10;

    public Bullet(int startX, int startY){
        this.x = startX;
        this.y = startY;
    }

    public void moveUp(){
        //y-=20;

        int num = 0;
        if(x>=300){
            num = (int)(Math.random()*20)+1;
        }else{
            num = (int)(Math.random()*10)+1;
        }
        width = num;
        height = num;


        int totallyNormalNumber = (int)(Math.random()*2)+1;
        int rantwo = (int)(Math.random()*2)+1;
        if(totallyNormalNumber==2){
            if(rantwo==2){
                height += (int)(totallyNormalNumber/2)+1;
                y+=num;
            }else{
                height -= (int)(totallyNormalNumber/2)+1;
                y-=num*2;

            }
            width += (int)(totallyNormalNumber/2)+1;
            x+=10;
        }else{
            if(rantwo==2){
                height += (int)(totallyNormalNumber/2)+1;
                y+=num;
            }else{
                height -= (int)(totallyNormalNumber/2)+1;
                y-=num*2;
            }
            width -= (int)(totallyNormalNumber/2)+1;
            x-=10;
        }


    }
    public void draw(Graphics g){
        g.setColor(Color.yellow);
        g.fillRect(x,y,width,height);
    }
    public boolean isOffScreen(){
        return y<0;
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
