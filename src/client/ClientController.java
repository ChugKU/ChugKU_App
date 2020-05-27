package client;

public class ClientController{
	Client client;
	ClientHandler clientHandler;
	Engine engine;
	GUI gui;
	
	private ClientController() {
		client = new Client();
		clientHandler = new ClientHandler();
	}
	
	public static void main(String[] args) {
		ClientController controller = new ClientController();
		
	}
}

class Player{
	private int id;
	private int x,y;
}