package client;

import processing.core.PApplet;
import java.util.ArrayList;

public class GUI extends PApplet {

	public ArrayList<Player> player = new ArrayList<Player>();
	Client controller;
	
	
	float e = 1; // elastic modulus
	boolean gameset = false;
	boolean gameover = false;
	boolean superPeer = false;
	static int leftPlayer, rightPlayer, myNum;
	int i, j = 0;
	boolean start = true;
	int leftScore = 0, rightScore = 0;
	
	public static void main(String[] args) {
		leftPlayer=1;
		rightPlayer=1;
		myNum=1;
		
		PApplet.main(GUI.class);
		
	}

	// method used only for setting the size of the window
	public void settings() {
		size(1320, 680);
		// size(1120,520); 축구장 사이즈
	}

	// identical use to setup in Processing IDE except for size()
	public void setup() {
		ellipseMode(CENTER);

		player.add(new Player(7, 660, 340, 0xe6e6fa)); // 첫번째 플레이어는 항상 공

		for (i = 0; i < leftPlayer; i++) {
			player.add(new Player(10, 200, 250 + i * 100, 0xe6e6fa));
		}
		for (i = 0; i < rightPlayer; i++) {
			player.add(new Player(10, 1120, 250 + i * 100, 0xe6e6fa));
		}
	}

	// identical use to draw in Prcessing IDE
	public void draw() {
		if(superPeer) {
			compute();
		} compute();
		setBoard();

	}

	public void mousePressed() {
	}

	public void mouseDragged() {
	}

	public void mouseReleased() {
	}

	public void keyReleased() {
		if (key == ' ' && dist(player.get(myNum).x, player.get(myNum).y, player.get(0).x, player.get(0).y) < 55) {
			player.get(0).vx = (float) ((player.get(0).x - player.get(myNum).x) * 0.18);
			player.get(0).vy = (float) ((player.get(0).y - player.get(myNum).y) * 0.18);
		}
	}
	
	public void keyPressed() {
		if (key == ' ' && dist(player.get(myNum).x, player.get(myNum).y, player.get(0).x, player.get(0).y) < 55) {
			player.get(0).vx = (float) ((player.get(0).x - player.get(myNum).x) * 0.18);
			player.get(0).vy = (float) ((player.get(0).y - player.get(myNum).y) * 0.18);
		}
	}

	public void keyTyped() {
		if (key == 'a') {
			player.get(myNum).vx = -3.8f;
		} else if (key == 'd') {
			player.get(myNum).vx = 3.8f;
		} else if (key == 'w') {
			player.get(myNum).vy = -3.8f;
		} else if (key == 's') {
			player.get(myNum).vy = 3.8f;
		}
		
		if (key == CODED) {
			if (keyCode == LEFT) {
				player.get(3).vx = -3.8f;
			} else if (keyCode == RIGHT) {
				player.get(3).vx = 3.8f;
			} else if (keyCode == UP) {
				player.get(3).vy = -3.8f;
			} else if (keyCode == DOWN) {
				player.get(3).vy = 3.8f;
			}
		}
		
		//ClientController.vx = player.get(myNum).vx;
		//ClientController.vy = player.get(myNum).vy;
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

		for (i = 0; i < player.size(); i++) {
			player.get(i).update();
		}
		textSize(18);
		fill(14, 226, 240);
		text("Score Board", 610, 20);
		text(leftScore, 640, 40);
		text(rightScore, 670, 40);

		if (gameset) {
			gameset = false;
			textSize(64);
			
			text("GOAL!!!", 630, 230);
			delay(1000);
			text("3", 630, 230);
			delay(1000);
			text("2", 630, 230);
			delay(1000);
			text("1", 630, 230);
			delay(1000);
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

		Player(float mass, float x, float y, int clr) {
			this.m = mass;
			this.r = 2 * m;
			this.vx = 0;
			this.vy = 0;
			this.x = x;
			this.y = y;
			this.clr = clr;
		}

		void update() {
			fill(clr);
			ellipse(x, y, r * 2, r * 2);
		}
	}
	
	synchronized void compute() {
		for (i = 0; i < player.size(); i++) {
			for (j = 0; j < player.size(); j++) {
				if (i != j) {
					float dx = player.get(i).x - player.get(j).x;
					float dy = player.get(i).y - player.get(j).y;
					float dab = abs(sqrt(dx * dx + dy * dy));

					if (dab <= player.get(i).r + player.get(j).r) { // 두 공 및 플레이어 충돌 시
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

						// 플레이어간 겹침 방지
						float angleAB = atan2(dy, dx);
						float angleplayer = atan2(-dy, -dx);
						float moveToDistance = abs(player.get(i).r + player.get(j).r) - dab;
						player.get(i).x = player.get(i).x + moveToDistance * cos(angleAB);
						player.get(j).x = player.get(j).x + moveToDistance * cos(angleplayer);

						player.get(i).vy *= 0.9;
						player.get(i).vx *= 0.9;
						player.get(j).vx *= 0.9;
						player.get(j).vy *= 0.9;
					}
				}
			}
		}
		
		// 왼쪽 골대에 들어간 경우
		if (player.get(0).x < 100 && player.get(0).x > 50 && player.get(0).y < 440 && player.get(0).y > 240) {
			gameset = true;
			rightScore += 1;
		}
		// 오른쪽 골대에 들어간 경우
		if (player.get(0).x < 1270 && player.get(0).x > 1220 && player.get(0).y < 440 && player.get(0).y > 240) {
			leftScore += 1;
			gameset = true;
		}
		if (gameset) {
			player.get(0).vx = 0;
			player.get(0).vy = 0;
			player.get(0).x = 660;
			player.get(0).y = 340;
			for (i = 1; i <= leftPlayer; i++) {
				player.get(i).vx = 0;
				player.get(i).vy = 0;
				player.get(i).x = 200;
				player.get(i).y = 250 + i * 100;
			}
			for (i = leftPlayer + 1; i <= leftPlayer + rightPlayer; i++) {
				player.get(i).vx = 0;
				player.get(i).vy = 0;
				player.get(i).x = 1120;
				player.get(i).y = 250 + i * 100;
			}
		}
	
		
		// 공 벽에 부딪친 경우
		if (player.get(0).x + player.get(0).vx < player.get(0).r + 100
				|| player.get(0).x + player.get(0).vx > width - player.get(0).r - 100) {
			if (player.get(0).x + player.get(0).vx < player.get(0).r + 100 && (player.get(0).y < 240 + player.get(0).r || player.get(0).y > 440 - player.get(0).r)) { // left wall
				player.get(0).x = player.get(0).r + 100;
				player.get(0).vx *= -0.7; // change x direct
			} else if (player.get(0).x + player.get(0).vx > width - player.get(0).r - 100 && (player.get(0).y < 240 + player.get(0).r || player.get(0).y > 440 - player.get(0).r)) { // right wall
				player.get(0).x = width - player.get(0).r - 100;
				player.get(0).vx *= -0.7; // change x direct
			}
			player.get(0).vy *= 0.7;
		}
		if (player.get(0).y + player.get(0).vy < player.get(0).r + 80
				|| player.get(0).y + player.get(0).vy > height - player.get(0).r - 80) {
			if (player.get(0).y + player.get(0).vy < player.get(0).r + 80) { // top wall
				player.get(0).y = player.get(0).r + 80;
				player.get(0).vy *= -0.7;
			} else { // bottom wall
				player.get(0).y = height - player.get(0).r - 80;
				player.get(0).vy *= -0.7;
			}
			player.get(0).vx *= 0.7;
		}

		// 플레이어들이 벽에 부딪힌 경우
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
		
		// 공의 마찰력
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
		// 공 좌표 변환
		player.get(0).x += player.get(0).vx;
		player.get(0).y += player.get(0).vy;

		// 플레이어의 마찰력
		for (i = 1; i < player.size(); i++) {
			if (player.get(i).vx > 0.06) {
				player.get(i).vx -= 0.06;
			} else if (player.get(i).vx < -0.06) {
				player.get(i).vx += 0.06;
			}
			if (player.get(i).vy > 0.06) {
				player.get(i).vy -= 0.06;
			} else if (player.get(i).vy < -0.06) {
				player.get(i).vy += 0.06;
			}
			if (player.get(i).vx < 0.1 && player.get(i).vx > -0.1) {
				player.get(i).vx = 0;
			} else if (player.get(i).vy < 0.1 && player.get(i).vy > -0.1) {
				player.get(i).vy = 0;
			}
			// 플레이어 좌표 변환
			player.get(i).x += player.get(i).vx;
			player.get(i).y += player.get(i).vy;
		}

	}
}
