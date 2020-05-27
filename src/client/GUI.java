package client;

import processing.core.PApplet;
import java.util.ArrayList;

public class GUI extends PApplet {
	
	ArrayList<Player> player = new ArrayList<Player>();
	
	float e = 1; // elastic modulus
	boolean gameover = false;
	float spd;
	int i,j = 0;
	boolean start = true;
	int fault = 0;
	int hits = 0;
	float spin = 0;
	
    // The argument passed to main must match the class name
    public static void main(String[] args) {
        PApplet.main(GUI.class);
    }

    // method used only for setting the size of the window
    public void settings(){
    	size(1320,680);
    	// size(1120,520); 축구장 사이즈
    }

    // identical use to setup in Processing IDE except for size()
    public void setup(){
    	ellipseMode(CENTER);
    	
    	player.add(new Player(7, 300, 250, 0xe6e6fa)); // 첫번째 플레이어는 항상 공
    	player.add(new Player(10, 100, 250, 0xe6e6fa));
    	player.add(new Player(10, 700, 250, 0xe6e6fa));
    }

    // identical use to draw in Prcessing IDE
    public void draw() {
      
        setBoard();
        
        for(i = 0; i < player.size(); i++) {
          for(j = 0; j < player.size(); j++) {
            if(i != j) {
              float dx = player.get(i).x - player.get(j).x;
              float dy = player.get(i).y - player.get(j).y;
              float dab = abs(sqrt(dx*dx + dy*dy));
              
              if(dab <= player.get(i).r + player.get(j).r) { // 두 공 및 플레이어 충돌 시
                float sinTheta = dy / abs(sqrt(dx*dx + dy*dy));
                float cosTheta = dx / abs(sqrt(dx*dx + dy*dy));
                float vxAp = (player.get(i).m - e*player.get(j).m)/(player.get(i).m + player.get(j).m)*(player.get(i).vx*cosTheta + player.get(i).vy*sinTheta) +
                      (player.get(j).m + e*player.get(j).m)/(player.get(i).m + player.get(j).m)*(player.get(j).vx*cosTheta + player.get(j).vy*sinTheta);
                float vxBp = (player.get(i).m + e*player.get(i).m)/(player.get(i).m + player.get(j).m)*(player.get(i).vx*cosTheta + player.get(i).vy*sinTheta) +
                      (player.get(j).m - e*player.get(i).m)/(player.get(i).m + player.get(j).m)*(player.get(j).vx*cosTheta + player.get(j).vy*sinTheta);
                float vyAp = player.get(i).vx*(-sinTheta) + player.get(i).vy*cosTheta;
                float vyBp = player.get(j).vx*(-sinTheta) + player.get(j).vy*cosTheta;
                
                player.get(i).vx = vxAp*cosTheta + vyAp*(-sinTheta);
                player.get(i).vy = vxAp*sinTheta + vyAp*cosTheta;
                player.get(j).vx = vxBp*cosTheta + vyBp*(-sinTheta);
                player.get(j).vy = vxBp*sinTheta + vyBp*cosTheta;
                
                // 플레이어간 겹침 방지
                float angleAB = atan2(dy,dx);
                float angleplayer = atan2(-dy,-dx); 
                float moveToDistance = abs(player.get(i).r + player.get(j).r) - dab;
                player.get(i).x = player.get(i).x + moveToDistance * cos(angleAB);
                player.get(j).x = player.get(j).x + moveToDistance * cos(angleplayer);

                
                player.get(i).vy *= 0.9;
                player.get(i).vx *= 0.9;
                player.get(j).vx *= 0.9;
                player.get(j).vy *= 0.9;
              }
            
              if(player.get(i).x + player.get(i).vx < player.get(i).r || player.get(i).x + player.get(i).vx > width - player.get(i).r) {
                  if(player.get(i).x + player.get(i).vx < player.get(i).r) { // left wall
                      player.get(i).x = player.get(i).r;
                      player.get(i).vx = 0; // change x direct
                  } 
                  else { // right wall
                      player.get(i).x = width - player.get(i).r;
                      player.get(i).vx = 0; // change x direct
                  }            
                  player.get(i).vy *= 0.5;
              }
              if(player.get(i).y + player.get(i).vy< player.get(i).r || player.get(i).y + player.get(i).vy > height - player.get(i).r) {
                  if(player.get(i).y + player.get(i).vy< player.get(i).r) { // top wall
                      player.get(i).y = player.get(i).r;
                      player.get(i).vy *= 0;
                  } 
                  else { // bottom wall
                      player.get(i).y = height - player.get(i).r;
                      player.get(i).vy *= 0;
                  }          
                  player.get(i).vx *= 0.5;
              }
              
              if(player.get(j).x + player.get(j).vx < player.get(j).r || player.get(j).x + player.get(j).vx > width - player.get(j).r) {
                  if(player.get(j).x + player.get(j).vx < player.get(j).r) { // left wall
                      player.get(j).x = player.get(j).r;
                      player.get(j).vx = 0; // change x direct
                  } 
                  else { // right wall
                      player.get(j).x = width - player.get(j).r;
                      player.get(j).vx = 0; // change x direct
                  }            
                  player.get(j).vy *= 0.5;
              }
              
              if(player.get(j).y + player.get(j).vy< player.get(j).r || player.get(j).y + player.get(j).vy > height - player.get(j).r) {
                  if(player.get(j).y + player.get(j).vy< player.get(j).r) { // top wall
                      player.get(j).y = player.get(j).r;
                      player.get(j).vy *= 0;
                  } 
                  else { // bottom wall
                      player.get(j).y = height - player.get(j).r;
                      player.get(j).vy *= 0;
                  }          
                  player.get(j).vx *= 0.5;
              }
            }
          }
        }
        
        for(i = 0; i < player.size(); i++) {
          if(player.get(i).vx > 0){
            player.get(i).vx -= 0.02;
          }
          else {
            player.get(i).vx += 0.02;
          }
          if (player.get(i).vy > 0) {
            player.get(i).vy -= 0.02;
          }
          else {
             player.get(i).vy += 0.02;
          }   
          player.get(i).x += player.get(i).vx;
          player.get(i).y += player.get(i).vy;
          
          if(player.get(i).vx < 0.18f && player.get(i).vx > -0.18f) {
        	  player.get(i).vx = 0;
          }
          else if (player.get(i).vy < 0.18f && player.get(i).vy > -0.18f) {
        	  player.get(i).vy = 0;
          }
        }
        
    }
        
    public void mousePressed() {
    }
    public void mouseDragged() {
    }
    public void mouseReleased() {
    }
    
    public void keyPressed() {	 
        hits++;
        if (key == ' ') {
        	
        }
    }
    
    public void keyTyped() {	 
        hits++;
        if (key == 'a') {
        	player.get(0).vx = -3.5f;
        }
        else if (key == 'd') {
        	player.get(0).vx = 3.5f;
        }
        else if (key == 'w'){
        	player.get(0).vy = -3.5f;
        }
        else if (key == 's'){
        	player.get(0).vy = 3.5f;
        }
    }    
    
    void setBoard() {
    	
    	background(0, 128, 0); // green
        
        // size(1320,680); 전체 맵사이즈
    	  // size(1120,520); 축구장 사이즈
        
        // 줄 특징 설정
        strokeWeight(5);
        strokeCap(ROUND);
        
        // 골대
        noFill();
        stroke(0, 0, 0);
        rect(50, 240, 50, 200); // x, y, d, h
        rect(1220, 240, 50, 200); // x, y, d, h
        
        // 축구장 라인들
        noFill();
        stroke(248, 248, 255);
        circle(660, 340, 220); // x, y, r 중간원
       
        line(100, 80, 1220, 80); // x, y, d, h 윗라인
        line(100, 80, 100, 600); // x, y, d, h 왼라인
        line(1220, 80, 1220, 600); // x, y, d, h 오른라인
        line(100, 600, 1220, 600); // x, y, d, h 아랫라인
        line(660, 80, 660, 600); // x, y, d, h 중간선
      
      for(i = 0; i < player.size(); i++)
      {
    	  player.get(i).update();
      }
      textSize(18);
      fill(14, 226, 240);
      text("Spin : " + spin, 870, 435);
      text("Hits : " + hits, 870, 455);
      text("Faults : " + fault, 870, 475);
     
      if (gameover) {
            text("Game Over!",850,495);
            textSize(64);
            text("Win!",870,495);
            textSize(64);
            fill(217, 72, 202);
            noLoop();
      }
    }
  
    class Player {
        float m; // Mass
        float x; 
        float y;
        float vx;
        float vy;
        float r; // radius
        int clr;
        
        Player(float mass, float x, float y, int clr) 
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
