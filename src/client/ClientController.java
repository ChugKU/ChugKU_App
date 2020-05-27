package client;

import java.util.ArrayList;

public class ClientController{
	Client client;
	ClientHandler clientHandler;
	Engine engine;
	GUI gui;
	Player player;
	boolean superPeer;
	ArrayList playerList;
	String session, group;
	
	class Player{
		private int id;
		private int x,y;
		
		public Player(int id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}
	
	private ClientController(int id, String session, String group) {
		this.superPeer = false;
		this.session = session;
		this.group = group;
		
		player = new Player(id, 0, 0);
		
		client = new Client(this.session, this.group);
		clientHandler = new ClientHandler(client.getClientStub());
	
		client.getClientStub().setAppEventHandler(client.getClientEventHandler());
	}
	
	// ***** client control *****
	public void setSuper(boolean superPeer) {
		this.superPeer = superPeer;
		
		if(superPeer) {
			playerList = new ArrayList<Player>();
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	// ***** client control *****
	
	
	// ***** user interaction *****
	public void startGame() {
		
	}
	
	public void endGame() {
		
	}
	
	public void move() {
		
	}
	// ***** user interaction *****
	
	
	// ***** multicast (within clients)*****
	private void sendEvenToClients() {
		client.multicast();
	}
	// ***** multicast (within clients)*****
	
	
	public static void main(String[] args) {
		ClientController controller = new ClientController(1, "s1", "g1"); //user id
		controller.client.getClientStub().startCM();
	}
}

