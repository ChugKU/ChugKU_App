package client;

import client.GUI;
import processing.core.PApplet;

public class ClientController {
	
	Client client;
	ClientHandler clientHandler;
	Engine engine;

	int leftPlayer, rightPlayer, myNum;
	static float vx, vy;
	
	ClientController() {
		
		client = new Client();
		clientHandler = new ClientHandler();
	}
	
	public static void main(String[] args) {
		
		ClientController controller = new ClientController();
		controller.leftPlayer = 1;
		controller.rightPlayer = 0;
		controller.myNum = 1;
		
		GUI.leftPlayer = controller.leftPlayer;
		GUI.rightPlayer = controller.rightPlayer;
		GUI.myNum = controller.myNum;
		
		PApplet.main(GUI.class);
		
		while(true) {
			//System.out.println(vx + " " + vy);
		}
	}
	
}