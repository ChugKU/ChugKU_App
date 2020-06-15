package client;

import processing.core.PApplet;
import java.util.ArrayList;

public class GUI extends PApplet {

	static ArrayList<Player> player = new ArrayList<Player>();
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
		// size(1120,520);  ౸         
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
	public void draw() {
		switch(gameMode) {
			case 1:
				setBoard();
				if (gameset > 0) {
					player.get(0).vx = 0;
					player.get(0).vy = 0;
					player.get(0).x = 660;
					player.get(0).y = 340;
					for (i = 1; i <= leftPlayer; i++) {
						player.get(i).vx = 0;
						player.get(i).vy = 0;
						player.get(i).x = 200;
						player.get(i).y = 250 + (i - 1) * 100;
					}
					for (i = leftPlayer + 1; i <= leftPlayer + rightPlayer; i++) {
						player.get(i).vx = 0;
						player.get(i).vy = 0;
						player.get(i).x = 1120;
						player.get(i).y = 250 + (i - leftPlayer - 1) * 100;
					}
					
					return;
				}

				if (superPeer) {
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

		if (gameset > 0) {
			player.get(0).vx = 0;
			player.get(0).vy = 0;
			player.get(0).x = 660;
			player.get(0).y = 340;
			for (i = 1; i <= leftPlayer; i++) {
				player.get(i).vx = 0;
				player.get(i).vy = 0;
				player.get(i).x = 200;
				player.get(i).y = 250 + (i - 1) * 100;
			}
			for (i = leftPlayer + 1; i <= leftPlayer + rightPlayer; i++) {
				player.get(i).vx = 0;
				player.get(i).vy = 0;
				player.get(i).x = 1120;
				player.get(i).y = 250 + (i - leftPlayer - 1) * 100;
			}
			
			return;
		}

		if (superPeer) {
			engine();
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

	      background(0, 128, 0); // green
	      strokeWeight(5);
	      strokeCap(ROUND);
	      
	      noFill();
	      stroke(0, 0, 0);
	      rect(50, 240, 50, 200); // x, y, d, h
	      rect(1220, 240, 50, 200); // x, y, d, h

	      // 異援ъ 쇱몃
	      noFill();
	      stroke(248, 248, 255);
	      circle(660, 340, 220); // x, y, r 以媛

	      line(100, 80, 1220, 80); // x, y, d, h 쇱
	      line(100, 80, 100, 600); // x, y, d, h 쇰쇱
	      line(1220, 80, 1220, 600); // x, y, d, h ㅻⅨ쇱
	      line(100, 600, 1220, 600); // x, y, d, h ル쇱
	      line(660, 80, 660, 600); // x, y, d, h 以媛
	      
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
	      for (i = 0; i < player.size(); i++) {
	         for (j = 0; j < player.size(); j++) {
	            if (i != j) {
	               float dx = player.get(i).x - player.get(j).x;
	               float dy = player.get(i).y - player.get(j).y;
	               float dab = abs(sqrt(dx * dx + dy * dy));

	               if (dab <= player.get(i).r + player.get(j).r) { //  怨 諛 �댁 異⑸ 
	                  float sinTheta = dy / abs(sqrt(dx * dx + dy * dy));
	                  float cosTheta = dx / abs(sqrt(dx * dx + dy * dy));
	                  float vxAp = (player.get(i).m - e * player.get(j).m) / (player.get(i).m + player.get(j).m)
	                        * (player.get(i).vx * cosTheta + player.get(i).vy * sinTheta)
	                        + (player.get(j).m + e * player.get(j).m) / (player.get(i).m + player.get(j).m)
	                              * (player.get(j).vx * cosTheta + player.get(j).vy * sinTheta);
	                  float vxBp = (player.get(i).m + e * player.get(i).m) / (player.get(i).m + player.get(j).m)
	                        * (player.get(i).vx * cosTheta + player.get(i).vy * sinTheta)
	                        + (player.get(j).m - e * player.get(i).m) / (player.get(i).m + player.get(j).m)
	                              * (player.get(j).vx * cosTheta + player.get(j).vy * sinTheta);
	                  float vyAp = player.get(i).vx * (-sinTheta) + player.get(i).vy * cosTheta;
	                  float vyBp = player.get(j).vx * (-sinTheta) + player.get(j).vy * cosTheta;

	                  player.get(i).vx = vxAp * cosTheta + vyAp * (-sinTheta);
	                  player.get(i).vy = vxAp * sinTheta + vyAp * cosTheta;
	                  player.get(j).vx = vxBp * cosTheta + vyBp * (-sinTheta);
	                  player.get(j).vy = vxBp * sinTheta + vyBp * cosTheta;

	                  // �댁닿 寃뱀묠 諛⑹
	                  float angleAB = atan2(dy, dx);
	                  float angleplayer = atan2(-dy, -dx);
	                  float moveToDistance = abs(player.get(i).r + player.get(j).r) - dab;
	                  player.get(i).x = player.get(i).x + moveToDistance * cos(angleAB);
	                  player.get(j).x = player.get(j).x + moveToDistance * cos(angleplayer);

	                  player.get(i).vy *= 0.8;
	                  player.get(i).vx *= 0.8;
	                  player.get(j).vx *= 0.8;
	                  player.get(j).vy *= 0.8;
	               }
	            }
	         }
	      }
	      
	      // 쇱そ 怨⑤ ㅼ닿 寃쎌
	      if (player.get(0).x < 100 && player.get(0).x > 50 && player.get(0).y < 440 && player.get(0).y > 240) {
	         gameset = 5;
	         rightScore += 1;
	         gamesetFlag = true;
	      }
	      // ㅻⅨ履 怨⑤ ㅼ닿 寃쎌
	      if (player.get(0).x < 1270 && player.get(0).x > 1220 && player.get(0).y < 440 && player.get(0).y > 240) {
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
	      
	      // 怨 踰쎌 遺れ 寃쎌
	      if (player.get(0).x + player.get(0).vx < player.get(0).r + 100
	            || player.get(0).x + player.get(0).vx > width - player.get(0).r - 100) {
	         if (player.get(0).x + player.get(0).vx < player.get(0).r + 100 && (player.get(0).y < 240 + player.get(0).r || player.get(0).y > 440 - player.get(0).r)) { // left wall
	            player.get(0).x = player.get(0).r + 100;
	            player.get(0).vx *= -0.8; // change x direct
	         } else if (player.get(0).x + player.get(0).vx > width - player.get(0).r - 100 && (player.get(0).y < 240 + player.get(0).r || player.get(0).y > 440 - player.get(0).r)) { // right wall
	            player.get(0).x = width - player.get(0).r - 100;
	            player.get(0).vx *= -0.8; // change x direct
	         }
	         player.get(0).vy *= 0.8;
	      }
	      if (player.get(0).y + player.get(0).vy < player.get(0).r + 80
	            || player.get(0).y + player.get(0).vy > height - player.get(0).r - 80) {
	         if (player.get(0).y + player.get(0).vy < player.get(0).r + 80) { // top wall
	            player.get(0).y = player.get(0).r + 80;
	            player.get(0).vy *= -0.8;
	         } else { // bottom wall
	            player.get(0).y = height - player.get(0).r - 80;
	            player.get(0).vy *= -0.8;
	         }
	         player.get(0).vx *= 0.8;
	      }

	      // �댁대ㅼ 媛μ由 踰쎌 遺ろ 寃쎌
	      for (i = 1; i < player.size(); i++) {
	         if (player.get(i).x + player.get(i).vx < player.get(i).r
	               || player.get(i).x + player.get(i).vx > width - player.get(i).r) {
	            if (player.get(i).x + player.get(i).vx < player.get(i).r) { // left wall
	               player.get(i).x = player.get(i).r;
	               player.get(i).vx = 0; // change x direct
	            } else { // right wall
	               player.get(i).x = width - player.get(i).r;
	               player.get(i).vx = 0; // change x direct
	            }
	         }
	         if (player.get(i).y + player.get(i).vy < player.get(i).r
	               || player.get(i).y + player.get(i).vy > height - player.get(i).r) {
	            if (player.get(i).y + player.get(i).vy < player.get(i).r) { // top wall
	               player.get(i).y = player.get(i).r;
	               player.get(i).vy = 0;
	            } else { // bottom wall
	               player.get(i).y = height - player.get(i).r;
	               player.get(i).vy = 0;
	            }
	         }
	      }
	      
	      // 怨듭 留李곕
	      if (player.get(0).vx > 0.04) {
	         player.get(0).vx -= 0.04;
	      } else if (player.get(0).vx < -0.04) {
	         player.get(0).vx += 0.04;
	      }
	      if (player.get(0).vy > 0.04) {
	         player.get(0).vy -= 0.04;
	      } else if (player.get(0).vy < -0.04) {
	         player.get(0).vy += 0.04;
	      }
	      if (player.get(0).vx < 0.15 && player.get(0).vx > -0.15) {
	         player.get(0).vx = 0;
	      } else if (player.get(0).vy < 0.15 && player.get(0).vy > -0.15) {
	         player.get(0).vy = 0;
	      }
	      // 怨 醫 蹂
	      player.get(0).x += player.get(0).vx;
	      player.get(0).y += player.get(0).vy;

	      // �댁댁 留李곕
	      for (i = 1; i < player.size(); i++) {
	         if (player.get(i).vx > 0.04) {
	            player.get(i).vx -= 0.04;
	         } else if (player.get(i).vx < -0.04) {
	            player.get(i).vx += 0.04;
	         }
	         if (player.get(i).vy > 0.04) {
	            player.get(i).vy -= 0.04;
	         } else if (player.get(i).vy < -0.04) {
	            player.get(i).vy += 0.04;
	         }
	         if (player.get(i).vx < 0.08 && player.get(i).vx > -0.08) {
	            player.get(i).vx = 0;
	         } else if (player.get(i).vy < 0.08 && player.get(i).vy > -0.08) {
	            player.get(i).vy = 0;
	         }
	         // �댁 醫 蹂
	         player.get(i).x += player.get(i).vx;
	         player.get(i).y += player.get(i).vy;
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
		    	  } else {
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