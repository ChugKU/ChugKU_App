package client;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class Client {
	private CMClientStub clientStub;
	private ClientHandler clientHandler;

	private GUI gui;
	
	private int state, cmd;
	
	boolean superPeer;
	
	String session, group;
	
	ArrayList<Player> playerList;
	ArrayList<String> roomList;
	String roomName;
	
	int playerID;
	
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
		
		playerList = new ArrayList<Player>();
		
		clientStub = new CMClientStub();
		clientHandler = new ClientHandler(clientStub);
		clientStub.setAppEventHandler(clientHandler);
		
		this.session = session;
		this.group = group;
		
		roomList = new ArrayList<String>();
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
	
	public void setRoomList(String[] rooms) {
		roomList.clear();
		for(int i=0; i<rooms.length; i++) {
			roomList.add(rooms[i]);
		}
	}
	
	public ArrayList<String> getRoomList() {
		CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMSession session = interInfo.findSession(myself.getCurrentSession());
		Vector<CMGroup> roomGroup = session.getGroupList();
		ArrayList<String> roomList = new ArrayList<String>();
		
		for(int i=0; i<roomGroup.size(); i++) {
			roomList.add(roomGroup.elementAt(i).getGroupName());
		}
		
		return roomList;
	}
	
	

	public void updateRoomList() {
		// TODO Auto-generated method stub
		state = 1; // lobby panel
		cmd = 1; // update String types of room name arraylist
	}
	// ***** client control *****\\
	
		
		
	// ***** user interaction *****
	public boolean startGame() {
		if(this.superPeer) {
			CMUserEvent cme = new CMUserEvent();
		cme.setID(this.playerID);
			cme.setStringID("startGame");
			cme.setEventField(CMInfo.CM_INT, "ingGame", Integer.toString(1)); //send room number
				
			multicast(cme);
				
			this.ingGame = true;
				
				return true;
			}
			return false;
	}
		
	public void move(int x, int y, int kick) {
		CMUserEvent cme = new CMUserEvent();
		cme.setID(this.playerID);
		cme.setStringID("move");
		cme.setEventField(CMInfo.CM_INT, "x", Integer.toString(x)); //send x=x
		cme.setEventField(CMInfo.CM_INT, "y", Integer.toString(y)); //send y=y
		cme.setEventField(CMInfo.CM_INT, "kick", Integer.toString(kick)); //send kick=0,1
		multicast(cme);
		
		cme = new CMUserEvent();
		cme.setID(this.playerID);
		cme.setStringID("update");
		cme.setEventField(CMInfo.CM_INT, "x", Integer.toString(x)); //send x=x
		cme.setEventField(CMInfo.CM_INT, "y", Integer.toString(y)); //send y=y
		cme.setEventField(CMInfo.CM_INT, "kick", Integer.toString(kick)); //send kick=0,1
		multicast(cme);
	}	
	
//	public void kick() {
//		CMUserEvent cme = new CMUserEvent();
//		cme.setID(this.playerID);
//		cme.setStringID("kick");
//		cme.setEventField(CMInfo.CM_INT, "kick", Integer.toString(1)); //send kick=true
//		multicast(cme);
//	}
	
	public void createRoom(String roomName) {
		boolean bRequestResult = clientStub.requestSessionInfo();
		if(bRequestResult)
			System.out.println("successfully sent the session-info request.");
		else
			System.err.println("failed the session-info request!");
		System.out.println("======");
		
		clientStub.leaveSession();
		long   save_time = System.currentTimeMillis();
		long   curr_time = 0;
		while ( (curr_time - save_time) < 1000 )
		{
			curr_time = System.currentTimeMillis();
		}
		
		clientStub.joinSession("session2");
		
		
		CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMSession session = interInfo.findSession(myself.getCurrentSession());
		
		
		session.createGroup(roomName, myself.getHost(), myself.getUDPPort()); 
		
		save_time = System.currentTimeMillis();
		curr_time = 0;
		while ( (curr_time - save_time) < 1000 )
		{
			curr_time = System.currentTimeMillis();
		}
		
		clientStub.changeGroup(roomName);
	}
	
	public void enterRoom(String roomName) {
		CMDummyEvent due = new CMDummyEvent();
		due.setDummyInfo("enter " + roomName);
		//
		due.setSender("");
		
		clientStub.send(due, "SERVER");
	}
	
	public void exitRoom() {
		//session change
		clientStub.leaveSession();
		
		clientStub.joinSession("Session1");
	}
	// ***** user interaction *****

	
	// ***** game processing *****
	private boolean endGame() {
		if(this.superPeer) { 
			if(curPoint==maxPoint) {
				this.ingGame = false;
					
				CMUserEvent cme = new CMUserEvent();
				cme.setID(this.playerID);
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
	
	public int getPlayerID() {
		return playerID;		
	}
	
	public void setPlayerList(int playerID, Player player) {
		this.playerList.add(playerID, player);
		this.playerList.remove(playerID + 1);
	}
	// ***** client getter, setter *****
	
	
	
	public void multicast(CMEvent cme) {
		clientStub.multicast(cme, session, group);
	}
	
	
	public void init() {
		// TODO Auto-generated method stub		
		boolean ret;
		this.clientHandler.setClient(this); //init 
		
		clientStub.startCM();

		//get client name!
		ret = clientStub.loginCM("User1", "");
		
		//ret = clientStub.joinSession("session1"); // Enter Lobby Session
		if(ret)
			System.out.println(ret + ": successfully sent the session-join request.");
		else
			System.err.println(ret + ": failed the session-join request!");
		
	}	
	
	public static void main(String[] args) {
		Client client = new Client(0,"session1","g1");
		client.init();
		boolean a = true;
		
		while(true) {
			if(a) {
				client.createRoom("aaa");
				client.enterRoom("aaa");
				a=false;
			}
		}
	}
}
