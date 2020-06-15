package client;

import processing.core.PApplet;
import java.util.ArrayList;

public class GUI extends PApplet {

   static ArrayList<Player> player = new ArrayList<Player>();
   static ArrayList<Player> computSet = new ArrayList<Player>();
   static Button[] button;
   static Button startButton;

   float e = 1; // elastic modulus
   static int gameset = 0;
   static boolean gamesetFlag = false;
   static boolean gameover = false;
   static boolean superPeer = true;
   static String keypress = "";
   static boolean keyType = false;
   static int leftPlayer, rightPlayer, myNum;
   int i, j = 0;
   static boolean start = false;
   static int leftScore = 0, rightScore = 0;
   static int gameMode = 1;
   static boolean[] enterRoom;
   static boolean[] enterRoomReq;
   
   // method used only for setting the size of the window
   public void settings() {
      size(1320, 680);    
   }

   // identical use to setup in Processing IDE except for size()
   public void setup() {
      myNum=-1;
      button = new Button[4];
      for(int i=0; i<4; i++) button[i] = new Button(i);
      startButton = new Button(-1);
      enterRoom = new boolean[4];
      enterRoomReq = new boolean[4];
      gameMode = 2;
      ellipseMode(CENTER);
   }



   // identical use to draw in Prcessing IDE
   synchronized public void draw() {
      switch(gameMode) {
      case 0:
         //hold
         break;
         case 1:
            setBoard();
            
            if (superPeer) {
               if (gameset > 0) {
                  computSet.get(0).vx = 0;
                  computSet.get(0).vy = 0;
                  computSet.get(0).x = 660;
                  computSet.get(0).y = 340;
                  for (i = 1; i <= leftPlayer; i++) {
                     computSet.get(i).vx = 0;
                     computSet.get(i).vy = 0;
                     computSet.get(i).x = 200;
                     computSet.get(i).y = 250 + (i - 1) * 100;
                  }
                  for (i = leftPlayer + 1; i <= leftPlayer + rightPlayer; i++) {
                     computSet.get(i).vx = 0;
                     computSet.get(i).vy = 0;
                     computSet.get(i).x = 1120;
                     computSet.get(i).y = 250 + (i - leftPlayer - 1) * 100;
                  }
               }
               engine();
            }
            break;
         case 2:
            setLobby();
            for(int i=0; i<4; i++) {
               if(enterRoomReq[i]) gameMode = 1;
            }
            break;
         case 5:
            // wait
            break;
         case 6: // create Player
            if(superPeer) {
               // create computSet
               computSet.add(new Player(0, 5, 14, 660, 340, 0xe6e6fa)); // 

               for (i = 0; i < leftPlayer; i++) {
                  computSet.add(new Player(i + 1, 10, 20, 200, 250 + i * 100, 0xe6e6fa));
               }
               for (i = 0; i < rightPlayer; i++) {
                  computSet.add(new Player(leftPlayer + 1 + i, 10, 20, 1120, 250 + i * 100, 0xe6e6fa));
               }
            }
            player.add(new Player(0, 5, 14, 660, 340, 0xe6e6fa)); // 

            for (i = 0; i < leftPlayer; i++) {
               player.add(new Player(i + 1, 10, 20, 200, 250 + i * 100, 0xe6e6fa));
            }
            for (i = 0; i < rightPlayer; i++) {
               player.add(new Player(leftPlayer + 1 + i, 10, 20, 1120, 250 + i * 100, 0xe6e6fa));
            }
            
            GUI.gameMode = 1;
            start = true;
            break;
         default:
            break;
      }

      if (superPeer) {
         engine();
      }
      try {
         Thread.sleep(50);
      } catch(InterruptedException e) {
         System.out.println(e.getMessage());
      }

   }
   
   public void mousePressed() {
   }

   public void mouseDragged() {
   }

   public void mouseReleased() {
   }

   public void keyReleased() {
   }
   
   public void keyPressed() {
         if (key == ' ' && dist(player.get(myNum).x, player.get(myNum).y, player.get(0).x, player.get(0).y) < 55) {
            player.get(0).vx = (float) ((player.get(0).x - player.get(myNum).x) * 0.15);
            player.get(0).vy = (float) ((player.get(0).y - player.get(myNum).y) * 0.15);
            keypress = " ";
            keyType = true;
         }
      }

      public void keyTyped() {
         try {
            if (key == 'a') {
                  player.get(myNum).vx = -3.0f;
                  keypress = "a";
                  keyType = true;
               } else if (key == 'd') {
                  player.get(myNum).vx = 3.0f;
                  keypress = "d";
                  keyType = true;
               } else if (key == 'w') {
                  player.get(myNum).vy = -3.0f;
                  keypress = "w";
                  keyType = true;
               } else if (key == 's') {
                  player.get(myNum).vy = 3.0f;
                  keypress = "s";
                  keyType = true;
               }
         }catch (Exception e) {}
         
      }
   
   void setLobby() {
      //size(1320, 680);
      
      /*background*/
      background(color(71, 200, 62));
      
      /*title*/
      fill(255);
      textSize(80);
      textAlign(CENTER);
      text("ChugKU", 660, 150);
        
      /*all buttons*/
      for(int i=0; i<4; i++) button[i].place();
   }

   void setBoard() {

         background(0, 128, 0); // green 
         strokeWeight(5);
         strokeCap(ROUND);
         
         noFill();
         stroke(0, 0, 0);
         rect(50, 240, 50, 200); // x, y, d, h
         rect(1220, 240, 50, 200); // x, y, d, h

         // 異 援ъ     쇱 몃  
         noFill();
         stroke(248, 248, 255);
         circle(660, 340, 220); // x, y, r 以 媛    

         line(100, 80, 1220, 80); // x, y, d, h      쇱  
         line(100, 80, 100, 600); // x, y, d, h   쇰 쇱  
         line(1220, 80, 1220, 600); // x, y, d, h   ㅻⅨ  쇱  
         line(100, 600, 1220, 600); // x, y, d, h      ル 쇱  
         line(660, 80, 660, 600); // x, y, d, h 以 媛    
         
         for (i = 0; i < player.size(); i++) {
            player.get(i).update();
         }
         textSize(30);
         fill(0, 0, 0);
         text("Score Board", 580, 30);
         text(leftScore, 630, 60);
         text(":", 655, 60);
         text(rightScore, 670, 60);
         
         if (gameover) {
            delay(500);
             textSize(64);
             fill(0, 0, 0);
             text("GAMEOVER", 480, 310);
             delay(500);
             textSize(64);
             fill(0, 0, 0);
             text("GAMEOVER", 480, 310);
             gameMode = 2;
             gameset = 0;
          }
         
         if (gameset == 5) {
            textSize(64);
            fill(0, 0, 0);
            text("GOAL!!!", 562, 210);
            gameset--;
            delay(1000);
         } else if (gameset > 0) {
            textSize(64);
            fill(0, 0, 0);
            text(gameset - 1, 640, 210);
            gameset--;
            delay(1000);
         }
         if(!start) startButton.place();
      }
   
   void engine() { 
         for (i = 0; i < computSet.size(); i++) {
            for (j = 0; j < computSet.size(); j++) {
               if (i != j) {
                  float dx = computSet.get(i).x - computSet.get(j).x;
                  float dy = computSet.get(i).y - computSet.get(j).y;
                  float dab = abs(sqrt(dx * dx + dy * dy));

                  if (dab <= computSet.get(i).r + computSet.get(j).r) { //     怨  諛         댁   異⑸      
                     float sinTheta = dy / abs(sqrt(dx * dx + dy * dy));
                     float cosTheta = dx / abs(sqrt(dx * dx + dy * dy));
                     float vxAp = (computSet.get(i).m - e * computSet.get(j).m) / (computSet.get(i).m + computSet.get(j).m)
                           * (computSet.get(i).vx * cosTheta + computSet.get(i).vy * sinTheta)
                           + (computSet.get(j).m + e * computSet.get(j).m) / (computSet.get(i).m + computSet.get(j).m)
                                 * (computSet.get(j).vx * cosTheta + computSet.get(j).vy * sinTheta);
                     float vxBp = (computSet.get(i).m + e * computSet.get(i).m) / (computSet.get(i).m + computSet.get(j).m)
                           * (computSet.get(i).vx * cosTheta + computSet.get(i).vy * sinTheta)
                           + (computSet.get(j).m - e * computSet.get(i).m) / (computSet.get(i).m + computSet.get(j).m)
                                 * (computSet.get(j).vx * cosTheta + computSet.get(j).vy * sinTheta);
                     float vyAp = computSet.get(i).vx * (-sinTheta) + computSet.get(i).vy * cosTheta;
                     float vyBp = computSet.get(j).vx * (-sinTheta) + computSet.get(j).vy * cosTheta;

                     computSet.get(i).vx = vxAp * cosTheta + vyAp * (-sinTheta);
                     computSet.get(i).vy = vxAp * sinTheta + vyAp * cosTheta;
                     computSet.get(j).vx = vxBp * cosTheta + vyBp * (-sinTheta);
                     computSet.get(j).vy = vxBp * sinTheta + vyBp * cosTheta;

                     //        댁 닿   寃뱀묠 諛⑹  
                     float angleAB = atan2(dy, dx);
                     float angleplayer = atan2(-dy, -dx);
                     float moveToDistance = abs(computSet.get(i).r + computSet.get(j).r) - dab;
                     computSet.get(i).x = computSet.get(i).x + moveToDistance * cos(angleAB);
                     computSet.get(j).x = computSet.get(j).x + moveToDistance * cos(angleplayer);

                     computSet.get(i).vy *= 0.8;
                     computSet.get(i).vx *= 0.8;
                     computSet.get(j).vx *= 0.8;
                     computSet.get(j).vy *= 0.8;
                  }
               }
            }
         }
         
         // 
         if (computSet.get(0).x < 100 && computSet.get(0).x > 50 && computSet.get(0).y < 440 && computSet.get(0).y > 240) {
            gameset = 5;
            rightScore += 1;
            gamesetFlag = true;
         }
         //
         if (computSet.get(0).x < 1270 && computSet.get(0).x > 1220 && computSet.get(0).y < 440 && computSet.get(0).y > 240) {
            leftScore += 1;
            gameset = 5;
            gamesetFlag = true;
         }
         if (rightScore >= 3 || leftScore >= 3) {
            if(rightScore>=3) {
               rightScore = 3;
            }
            if(leftScore>=3) {
               leftScore = 3;
            }    
            gamesetFlag = false;
             gameover = true;
         }
         
         // 怨  踰쎌   遺   れ   寃쎌  
         if (computSet.get(0).x + computSet.get(0).vx < computSet.get(0).r + 100
               || computSet.get(0).x + computSet.get(0).vx > width - computSet.get(0).r - 100) {
            if (computSet.get(0).x + computSet.get(0).vx < computSet.get(0).r + 100 && (computSet.get(0).y < 240 + computSet.get(0).r || computSet.get(0).y > 440 - computSet.get(0).r)) { // left wall
               computSet.get(0).x = computSet.get(0).r + 100;
               computSet.get(0).vx *= -0.8; // change x direct
            } else if (computSet.get(0).x + computSet.get(0).vx > width - computSet.get(0).r - 100 && (computSet.get(0).y < 240 + computSet.get(0).r || computSet.get(0).y > 440 - computSet.get(0).r)) { // right wall
               computSet.get(0).x = width - computSet.get(0).r - 100;
               computSet.get(0).vx *= -0.8; // change x direct
            }
            computSet.get(0).vy *= 0.8;
         }
         if (computSet.get(0).y + computSet.get(0).vy < computSet.get(0).r + 80
               || computSet.get(0).y + computSet.get(0).vy > height - computSet.get(0).r - 80) {
            if (computSet.get(0).y + computSet.get(0).vy < computSet.get(0).r + 80) { // top wall
               computSet.get(0).y = computSet.get(0).r + 80;
               computSet.get(0).vy *= -0.8;
            } else { // bottom wall
               computSet.get(0).y = height - computSet.get(0).r - 80;
               computSet.get(0).vy *= -0.8;
            }
            computSet.get(0).vx *= 0.8;
         }

         //        댁 대 ㅼ   媛   μ  由  踰쎌   遺   ろ   寃쎌  
         for (i = 1; i < computSet.size(); i++) {
            if (computSet.get(i).x + computSet.get(i).vx < computSet.get(i).r
                  || computSet.get(i).x + computSet.get(i).vx > width - computSet.get(i).r) {
               if (computSet.get(i).x + computSet.get(i).vx < computSet.get(i).r) { // left wall
                  computSet.get(i).x = computSet.get(i).r;
                  computSet.get(i).vx = 0; // change x direct
               } else { // right wall
                  computSet.get(i).x = width - computSet.get(i).r;
                  computSet.get(i).vx = 0; // change x direct
               }
            }
            if (computSet.get(i).y + computSet.get(i).vy < computSet.get(i).r
                  || computSet.get(i).y + computSet.get(i).vy > height - computSet.get(i).r) {
               if (computSet.get(i).y + computSet.get(i).vy < computSet.get(i).r) { // top wall
                  computSet.get(i).y = computSet.get(i).r;
                  computSet.get(i).vy = 0;
               } else { // bottom wall
                  computSet.get(i).y = height - computSet.get(i).r;
                  computSet.get(i).vy = 0;
               }
            }
         }
         
         // 怨듭   留 李곕  
         if (computSet.get(0).vx > 0.04) {
            computSet.get(0).vx -= 0.04;
         } else if (computSet.get(0).vx < -0.04) {
            computSet.get(0).vx += 0.04;
         }
         if (computSet.get(0).vy > 0.04) {
            computSet.get(0).vy -= 0.04;
         } else if (computSet.get(0).vy < -0.04) {
            computSet.get(0).vy += 0.04;
         }
         if (computSet.get(0).vx < 0.15 && computSet.get(0).vx > -0.15) {
            computSet.get(0).vx = 0;
         } else if (computSet.get(0).vy < 0.15 && computSet.get(0).vy > -0.15) {
            computSet.get(0).vy = 0;
         }
         // 怨  醫     蹂    
         computSet.get(0).x += computSet.get(0).vx;
         computSet.get(0).y += computSet.get(0).vy;

         //        댁 댁   留 李곕  
         for (i = 1; i < computSet.size(); i++) {
            if (computSet.get(i).vx > 0.04) {
               computSet.get(i).vx -= 0.04;
            } else if (computSet.get(i).vx < -0.04) {
               computSet.get(i).vx += 0.04;
            }
            if (computSet.get(i).vy > 0.04) {
               computSet.get(i).vy -= 0.04;
            } else if (computSet.get(i).vy < -0.04) {
               computSet.get(i).vy += 0.04;
            }
            if (computSet.get(i).vx < 0.08 && computSet.get(i).vx > -0.08) {
               computSet.get(i).vx = 0;
            } else if (computSet.get(i).vy < 0.08 && computSet.get(i).vy > -0.08) {
               computSet.get(i).vy = 0;
            }
            //        댁   醫     蹂    
            computSet.get(i).x += computSet.get(i).vx;
            computSet.get(i).y += computSet.get(i).vy;
         }
      }
   
   class Player {
         
         int id;
         float m; // Mass
         float x;
         float y;
         float vx;
         float vy;
         float r; // radius
         int clr;

         Player(int id, float mass, float r, float x, float y, int clr) {
            this.id = id;
            this.m = mass;
            this.r = r;
            this.vx = 0;
            this.vy = 0;
            this.x = x;
            this.y = y;
            this.clr = clr;
         }

         void update() {
            fill(clr);
            ellipse(x, y, r * 2, r * 2);
            if (id > 0) {
               textSize(30);
               fill(0, 0, 0);
               text(id, x - 9, y + 11);
            }
         }
      }
      
    
    public class Button {
        private int x=410, y;
        private int bWidth=500, bHeight=80;
        private int index;
        private String label;
        
        private boolean pressed=false;
        private int clicked=0;
          
        public Button(int idx) {
          index = idx;
          if(idx>=0) {
             label = "Room No."+(index+1);
             y = 250+idx*100;
          }
          else {
             label = "start game";
             bWidth = 200;
             bHeight = 50;
             x=100;
             y = 20;
          }
        }
        
        public void place() {
          stroke(255);
          strokeWeight(5);
          if(!pressed) {
            noFill();
            rect(x, y, bWidth, bHeight);
            
            fill(255);
            textSize(25);
            textAlign(CENTER, CENTER);
            text(label, x+bWidth/2, y+bHeight/2);
          }
          else {
            fill(255);
            rect(x, y, bWidth, bHeight);
            
            fill(color(71, 200, 62));
            textSize(25);
            textAlign(CENTER, CENTER);
            text(label, x+bWidth/2, y+bHeight/2);
          }
          
          if(clicked==0 && mousePressed) { //mouse pressed
            pressed = onButton(mouseX, mouseY);
            if(pressed) clicked = 1; else clicked = 0; //press button
          }
          else if(clicked==1 && !mousePressed) { //mouse released
            pressed = false;
            clicked = 0;
            if(onButton(mouseX, mouseY)) { //release on button -> decide to enter room
               if(index>=0) {
                     println("Enter Room No."+index);
                    //should be changed -> receive from client that is able of not
                    enterRoom[index] = true;
                    gameMode = 2;
               } else { // start
                 gameMode=3;
               }
            }
            else { //do not release on button -> decide not to enter room
              println("Do not want to Enter Room No."+index);
            }
          }
        }
        
        boolean onButton(int mx, int my) {
          if(mx < x || mx > x+bWidth || my < y || my > y+bHeight) return false;
          else return true;
        }
      }
   
   public static void main(String[] args) {
      GUI.leftPlayer = 1;
      GUI.rightPlayer = 1;
      GUI.myNum = 1;
      
      PApplet.main(GUI.class);
      try {
         Thread.sleep(500);
      } catch(InterruptedException e) {
         System.out.println(e.getMessage());
      }

   }

}