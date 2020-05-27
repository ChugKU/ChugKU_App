package client;

import processing.core.PApplet;
import java.util.ArrayList;

public class GUI extends PApplet {
	ArrayList<Ball> ba = new ArrayList<Ball>();
	float e = 1; //elastic modulus
	boolean overball = false;
	boolean locked = false;
	boolean stopped = false;
	float spd;
	int i,j = 0;
	boolean start = true;
	int fault = 0;
	int hits = 0;
	float spin = 0;
	
	
    // The argument passed to main must match the class name
    public static void main(String[] args) {
        PApplet.main("Test");
    }

    // method used only for setting the size of the window
    public void settings(){
    	size(1000,500);
    }

    // identical use to setup in Processing IDE except for size()
    public void setup(){
    	background(37,114,15);
    	ellipseMode(CENTER);
    	//bgm = new SoundFile(this, "123.mp3");
    	//bgm.play();
    	
    	//num0 = white, num1 = black(Gameover)
    	ba.add(new Ball(13, 100, 250, 0xffffff));
    	ba.add(1, new Ball(13, 700, 250, 0x000000));
    	
    	ba.add(new Ball(13, 600, 250, 0x2955E5));
    	//
    	ba.add(new Ball(13, 650, 205, 0xF60B07));
    	ba.add(new Ball(13, 650, 295, 0x07F60C));
    	//
    	ba.add(new Ball(13, 700, 165, 0xFE9A00));
    	ba.add(new Ball(13, 700, 335, 0x7B500F));
    	//
    	ba.add(new Ball(13, 750, 125, 0xF983BD));
    	ba.add(new Ball(13, 750, 210, 0xF0E90E));
    	ba.add(new Ball(13, 750, 295, 0x0DFFF8));
    	ba.add(new Ball(13, 750, 380, 0x9118E3));
    }

    // identical use to draw in Prcessing IDE
    public void draw() 
    {
      if(start)
      {
        setBoard();
        start = false;  
      }
      
      for(i = 0; i< ba.size();i++) //?
      {
        if(ba.get(i).clr == 0x000000)
        {
          break;
        }
      }
      int a = 0;
      for(i = 0; i<ba.size(); i++)
      {
        if(ba.get(i).vx != 0 || ba.get(i).vy != 0)
        {
          stopped = false;
          break;
        }
        stopped = true;
      }
      if(stopped)
      {
         if(dist(ba.get(0).x, ba.get(0).y, mouseX, mouseY) < ba.get(0).r)
         {
           overball = true;
         }
         else
         {
           overball = false;      
         }
      }
      else
      {
        setBoard();
        for(i = 0; i<ba.size(); i++)
        {
          for(j = a; j<ba.size(); j++)
          {
            if(ba.get(i).r == 0)
            {
              continue;
            }
            if(i != j)
            {
              float dx = ba.get(i).x - ba.get(j).x;
              float dy = ba.get(i).y - ba.get(j).y;
              float dab = abs(sqrt(dx*dx + dy*dy));
              
              if(dab <= ba.get(i).r+ba.get(j).r)
              {
                float sinTheta = dy / abs(sqrt(dx*dx + dy*dy));
                float cosTheta = dx / abs(sqrt(dx*dx + dy*dy));
                float vxAp = (ba.get(i).m - e*ba.get(j).m)/(ba.get(i).m + ba.get(j).m)*(ba.get(i).vx*cosTheta + ba.get(i).vy*sinTheta) +
                      (ba.get(j).m + e*ba.get(j).m)/(ba.get(i).m + ba.get(j).m)*(ba.get(j).vx*cosTheta + ba.get(j).vy*sinTheta);
                float vxBp = (ba.get(i).m + e*ba.get(i).m)/(ba.get(i).m + ba.get(j).m)*(ba.get(i).vx*cosTheta + ba.get(i).vy*sinTheta) +
                      (ba.get(j).m - e*ba.get(i).m)/(ba.get(i).m + ba.get(j).m)*(ba.get(j).vx*cosTheta + ba.get(j).vy*sinTheta);
                float vyAp = ba.get(i).vx*(-sinTheta) + ba.get(i).vy*cosTheta;
                float vyBp = ba.get(j).vx*(-sinTheta) + ba.get(j).vy*cosTheta;
                
                ba.get(i).vx = vxAp*cosTheta + vyAp*(-sinTheta);
                ba.get(i).vy = vxAp*sinTheta + vyAp*cosTheta;
                ba.get(j).vx = vxBp*cosTheta + vyBp*(-sinTheta);
                ba.get(j).vy = vxBp*sinTheta + vyBp*cosTheta;
                
                //if two ball is overlaped, seperate from each other.
                float angleAB = atan2(dy,dx);
                float angleBA = atan2(-dy,-dx); 
                float moveToDistance = abs(ba.get(i).r + ba.get(j).r) - dab;
                ba.get(i).x = ba.get(i).x + moveToDistance * cos(angleAB);
                ba.get(j).x = ba.get(j).x + moveToDistance * cos(angleBA);
                
                ba.get(i).vy *= 0.9;
                ba.get(i).vx *= 0.9;
                ba.get(j).vx *= 0.9;
                ba.get(j).vy *= 0.9;
              }
            
              if(ba.get(i).x + ba.get(i).vx < ba.get(i).r || ba.get(i).x + ba.get(i).vx > width - ba.get(i).r)
              {
                  if(ba.get(i).x + ba.get(i).vx < ba.get(i).r) // left wall
                  {
                      ba.get(i).x = ba.get(i).r;
                      ba.get(i).vx *= -1; // change x direct
                      if(i == 0) {                     
                        ba.get(i).vy -= (spin * abs(ba.get(i).vx));     
                        if ( spin > 0.2 ) {
                             spin -= 0.15;
                          }
                          else if (spin < -0.2){
                             spin += 0.15; 
                        }
                      }
                  } 
                  else // right wall
                  {
                      ba.get(i).x = width - ba.get(i).r;
                      ba.get(i).vx *= -1; // change x direct
                      if(i == 0) {
                          ba.get(i).vy += (spin * abs(ba.get(i).vx));
                          if ( spin > 0/2 ) {
                             spin -= 0.15;
                          }
                          else if (spin < -0.2){
                             spin += 0.15; 
                          }
                      }
                  }            
                  ba.get(i).vx *= 0.5;
                  ba.get(i).vy *= 0.5;
              }
              
              if(ba.get(i).y + ba.get(i).vy< ba.get(i).r || ba.get(i).y + ba.get(i).vy > height - ba.get(i).r)
              {
                  if(ba.get(i).y + ba.get(i).vy< ba.get(i).r) // top wall
                  {
                      ba.get(i).y = ba.get(i).r;
                         ba.get(i).vy *= -1;
                        if(i == 0) {                       
                          ba.get(i).vx += (spin * abs(ba.get(i).vy));
                          if ( spin > 0.2 ) {
                             spin -= 0.15;
                          }
                          else if (spin < -0.2){
                             spin += 0.15; 
                          }
                        }
                  } 
                  else  // bottom wall
                  {
                      ba.get(i).y = height - ba.get(i).r;
                       ba.get(i).vy *= -1;
                        if(i == 0) {
                         ba.get(i).vx -= (spin * abs(ba.get(i).vy));
                         if ( spin > 0 ) {
                             spin -= 0.15;
                          }
                          else {
                             spin += 0.15; 
                          }
                        }
                  }          
                  ba.get(i).vx *= 0.5;
                  ba.get(i).vy *= 0.5;
              }
              
              if(ba.get(j).x + ba.get(j).vx< ba.get(j).r || ba.get(j).x + ba.get(j).vx > width - ba.get(j).r)
              {
                  if(ba.get(j).x  + ba.get(j).vx< ba.get(j).r) // left wall
                  {
                      ba.get(j).x = ba.get(j).r;
                        ba.get(j).vx *= -1; // change x direct
                        if(j == 0) {                
                            ba.get(j).vy -= (spin * abs(ba.get(j).vx));
                             if ( spin > 0.2 ) {
                             spin -= 0.15;
                          }
                          else if (spin < -0.2){
                             spin += 0.15; 
                        }
                        }
                  } 
                  else // right wall
                  {
                      ba.get(j).x = width - ba.get(j).r;
                       ba.get(j).vx *= -1; // change x direct
                         if(j == 0){                  
                            ba.get(j).vy += (spin * abs(ba.get(j).vx));
                             if ( spin > 0.2 ) {
                             spin -= 0.15;
                          }
                          else if (spin < -0.2){
                             spin += 0.15; 
                        }
                         }
                  } 
                  
                  ba.get(j).vx *= 0.5;
                  ba.get(j).vy *= 0.5;
              }
              
              if(ba.get(j).y + ba.get(j).vy< ba.get(j).r || ba.get(j).y + ba.get(j).vy > height - ba.get(j).r)
              {
                  if(ba.get(j).y  + ba.get(j).vy < ba.get(j).r) // top wall
                  {
                      ba.get(j).y = ba.get(j).r;
                       ba.get(j).vy *= -1;
                         if(j == 0) {
                        ba.get(j).vx += (spin * abs(ba.get(j).vy));
                         if ( spin > 0 ) {
                             spin -= 0.2;
                          }
                          else {
                             spin += 0.2; 
                        }
                         }
                  } 
                  else // bottom wall
                  {
                      ba.get(j).y = height - ba.get(j).r;
                        ba.get(j).vy *= -1;
                        if(i == 0) {
                         ba.get(j).vx -= (spin * abs(ba.get(j).vy));
                          if ( spin > 0 ) {
                             spin -= 0.2;
                          }
                          else {
                             spin += 0.2; 
                        }
                        }
                  }
               
                  ba.get(j).vx *= 0.5;
                  ba.get(j).vy *= 0.5;
              }
            }
          }
          a++;
        }
        a=0;
        
        for(i = 0; i<ba.size();i++)
        {
          if(  ba.get(i).vx > 0){
            ba.get(i).vx -= 0.01;
          }
          else {
            ba.get(i).vx += 0.01;
          }
          if (ba.get(i).vy > 0) {
            ba.get(i).vy -= 0.01;
          }
          else {
             ba.get(i).vy += 0.01;
          }   
          ba.get(i).x += ba.get(i).vx;
          ba.get(i).y += ba.get(i).vy;
          
          if(ba.get(i).vx < 0.05 && ba.get(i).vx > -0.05 && ba.get(i).vy < 0.05 && ba.get(i).vy > -0.05)
          {
            ba.get(i).vx = 0;
            ba.get(i).vy = 0;
          }
          
          if(dist(0,0,ba.get(i).x,ba.get(i).y) <= 60)
          {
            if(i == 0 && ba.get(i).r != 0)
            {
              ba.get(i).r = 0;
              ba.get(i).x = 100;
              ba.get(i).y = 250;
              ba.get(i).vx = 0;
              ba.get(i).vy = 0;
              fault++;
              setBoard();
            }
            else
            {
              ba.remove(i);
              setBoard();
            }
          }
          else if(dist(1000,0,ba.get(i).x,ba.get(i).y) <= 60)
          {
            if(i == 0 && ba.get(i).r != 0)
            {
              ba.get(i).r = 0;
              ba.get(i).x = 100;
              ba.get(i).y = 250;
              ba.get(i).vx = 0;
              ba.get(i).vy = 0;
              fault++;
              setBoard();
            }
            else
            {
              ba.remove(i);
              setBoard();
            }
          }
          else if(dist(0,500,ba.get(i).x,ba.get(i).y) <= 60)
          {
            if(i == 0 && ba.get(i).r != 0)
            {
              ba.get(i).r = 0;
              ba.get(i).x = 100;
              ba.get(i).y = 250;
              ba.get(i).vx = 0;
              ba.get(i).vy = 0;
              fault++;
              setBoard();
            }
            else
            {
              ba.remove(i);
              setBoard();
            }
          }
          else if(dist(1000,500,ba.get(i).x,ba.get(i).y) <= 60)
          {
            if(i == 0 && ba.get(i).r != 0)
            {
              ba.get(i).r = 0;
              ba.get(i).x = 100;
              ba.get(i).y = 250;
              ba.get(i).vx = 0;
              ba.get(i).vy = 0;
              fault++;
              setBoard();
            }
            else
            {
              ba.remove(i);
              setBoard();
            }
          }
          else if(dist(500, 0,ba.get(i).x,ba.get(i).y) <= 45)
          {
            if(i == 0 && ba.get(i).r != 0)
            {
              ba.get(i).r = 0;
              ba.get(i).x = 100;
              ba.get(i).y = 250;
              ba.get(i).vx = 0;
              ba.get(i).vy = 0;
              fault++;
              setBoard();
            }
            else
            {
              ba.remove(i);
              setBoard();
            }
          }
          else if(dist(500,500,ba.get(i).x,ba.get(i).y) <= 45)
          {
            if(i == 0 && ba.get(i).r != 0)
            {
              ba.get(i).r = 0;
              ba.get(i).x = 100;
              ba.get(i).y = 250;
              ba.get(i).vx = 0;
              ba.get(i).vy = 0;
              fault++;
              setBoard();
            }
            else
            {
              ba.remove(i);
              setBoard();
            }
          }
        } 
      }
    }
  
    
    public void mousePressed()
    {
      // this does not work...
      // still, placing ball on top of other one is possible
      int flag = 0;
      for(i = 1; i< ba.size();i++)
      {
        if(dist(mouseX, mouseY, ba.get(i).x, ba.get(i).y) < 51)
        {
         flag = 1;
        }
      }
      if(ba.get(0).r == 0 && stopped && flag == 0)
      {
            ba.get(0).r = 26;
            ba.get(0).x = mouseX;
            ba.get(0).y = mouseY;
            setBoard();
            locked = false;
      }
      
      if(overball)
      {
        locked = true;
      }
      else
      {
        locked = false;
      }
    }
    
    public void mouseDragged()
    {
      if(locked)
      {
        setBoard();
        line(ba.get(0).x, ba.get(0).y, (2*ba.get(0).x-mouseX),(2*ba.get(0).y-mouseY));
      } 
    }
    
    public void mouseReleased()
    {
      if(locked)
      { 
        spd = (ba.get(0).x-mouseX)/15;
        ba.get(0).vx = spd;
        
        spd = (ba.get(0).y-mouseY)/15;
        ba.get(0).vy = spd;
        overball = false;
        hits++;
      }
    }
    
    public void keyPressed()
    {
       if ( locked && key == CODED )
        {
          if (keyCode == LEFT) {
           spin -= 0.3; 
          }
          else if (keyCode == RIGHT) {
           spin += 0.3; 
          }
          else {
           spin = 0; 
          }
        }
    }
    
    void setBoard()
    {
      background(37,114,15);
      fill(0, 0, 0);
      //arc(x,y, garo, sero, 0, spin degree)
      arc(0, 0, 110, 110, 0, 2*PI); //NW 
      fill(0, 0, 0);
      arc(0, 500, 110, 110, 0, 2*PI); //SW
      fill(0, 0, 0);
      arc(1000, 500, 110, 110, 0, 2*PI); //SE
      fill(0, 0, 0);
      arc(1000, 0, 110, 110, 0, 2*PI); //NE
      fill(0, 0, 0);
      arc(500, 0, 70, 70, 0, 2*PI); //N
      fill(0, 0, 0);
      arc(500, 500, 70, 70, 0, 2*PI); //S
      for(i = 0; i<ba.size();i++)
      {
        ba.get(i).update();
      }
      textSize(18);
      fill(14, 226, 240);
      text("Spin : "+spin, 870, 435);
      text("Hits : "+hits, 870, 455);
      text("Faults : "+fault, 870, 475);
      if(ba.size() != 1)
      {
        if(ba.size() != 2)
        {
          if(ba.get(1).clr == 0x000000)
          {
            text("Remaining : "+(ba.size()-1),850,495);
          }
          else
          {
            text("Game Over!",850,495);
            textSize(64);
            fill(217, 72, 202);
            text("Game Over!", 320, 250);
            noLoop();
          }
        }
        else
        {
          text("Remaining : "+(ba.size()-1),850,495);
        }
      }
      else
      {
        text("Win!",870,495);
        textSize(64);
        fill(217, 72, 202);
        //text("WIN!", 435, 250);
        //noLoop();
      }
    }
  
    class Ball 
    {
        float m;//Mass
        float x;
        float y;
        float vx;
        float vy;
        float r;
        int clr;
        
        Ball(float mass, float x, float y, int clr) 
        {
          this.m = mass;
          this.r = 2 * m;
          this.vx = 0;
          this.vy = 0;
          this.x = x;
          this.y = y;
          this.clr = clr;
        }
        
        void update() 
        {
          fill(clr);
          ellipse(x, y, r*2, r*2);
        }
    }

}