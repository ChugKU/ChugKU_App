package client;

import java.util.ArrayList;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class Client {
	private CMClientStub clientStub;
	private ClientHandler clientHandler;
	
	Engine engine;
	GUI gui;
	
	Player player;
	boolean superPeer;
	
	String session, group;
	
	ArrayList playerList;
	ArrayList roomList; 
	String roomName;
	
	private boolean inRoom, ingGame;
	private int maxPoint, curPoint;


	class Player{
		private int id;
		private int x,y;
		
		public Player(int id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}
	
	Client(int id, String session, String group){
		
		this.superPeer = false;
		this.session = session;
		this.group = group;
		this.inRoom = false;
		this.ingGame = false;
		
		player = new Player(id, 0, 0);
		
		clientStub = new CMClientStub();
		clientHandler = new ClientHandler(clientStub);
		clientStub.setAppEventHandler(clientHandler);
		
		this.session = session;
		this.group = group;
	}	
	
	//return "client application service" - interaction with CM
	public CMClientStub getClientStub() {
		return clientStub;
	}		
	
	// ***** client control *****
	public void setSuper(boolean superPeer) {
		this.superPeer = superPeer;
			
		if(superPeer) {
			playerList = new ArrayList<Player>();
		}
	}
	// ***** client control *****\\
	
		
		
	// ***** user interaction *****
	public boolean startGame() {
		if(this.superPeer) {
			CMUserEvent cme = new CMUserEvent();
		cme.setID(this.player.id);
			cme.setStringID("startGame");
			cme.setEventField(CMInfo.CM_INT, "ingGame", Integer.toString(1)); //send room number
				
			multicast(cme);
				
			this.ingGame = true;
				
				return true;
			}
			return false;
	}
		
	public void move(int x, int y) {
		CMUserEvent cme = new CMUserEvent();
		cme.setID(this.player.id);
		cme.setStringID("move");
		cme.setEventField(CMInfo.CM_INT, "x", Integer.toString(x)); //send x=x
		cme.setEventField(CMInfo.CM_INT, "y", Integer.toString(y)); //send y=y
		multicast(cme);
	}	
	
	public void kick() {
		CMUserEvent cme = new CMUserEvent();
		cme.setID(this.player.id);
		cme.setStringID("kick");
		cme.setEventField(CMInfo.CM_INT, "kick", Integer.toString(1)); //send kick=true
		multicast(cme);
	}
	// ***** user interaction *****

	
	// ***** game processing *****
	private boolean endGame() {
		if(this.superPeer) { 
			if(curPoint==maxPoint) {
				this.ingGame = false;
					
				CMUserEvent cme = new CMUserEvent();
				cme.setID(this.player.id);
				cme.setStringID("endGame");
				cme.setEventField(CMInfo.CM_INT, "ingGame", Integer.toString(0)); //send ingGame=false (gameover)
				
				multicast(cme);
					
				return true;
			}
		}
			return false;
		}
		
	private void addPoint() {
		this.curPoint++;
	
	}
	// ***** game processing *****
	
	
	// ***** client getter, setter *****
	public boolean isInRoom() {
		return inRoom;
	}

	public void setInRoom(boolean inRoom) {
		this.inRoom = inRoom;
	}

	public boolean isIngGame() {
		return ingGame;
	}

	public void setIngGame(boolean ingGame) {
		this.ingGame = ingGame;
	}

	public int getMaxPoint() {
		return maxPoint;
	}

	public void setMaxPoint(int maxPoint) {
		this.maxPoint = maxPoint;
	}
	
	public Player getPlayer() {
		return player;		
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	// ***** client getter, setter *****
	
	
	
	public void multicast(CMEvent cme) {
		clientStub.multicast(cme, session, group);
		
	}
	

	public static void main(String[] args) {
		Client client = new Client(1, "session2", "g1"); //user id
		
		client.clientHandler.setClient(client); //init 
		
		client.getClientStub().startCM();
		
		
	}
}
