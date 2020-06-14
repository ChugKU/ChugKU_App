package client;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import processing.core.PApplet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class Client {
	private CMClientStub clientStub;
	private ClientHandler clientHandler;

	private int state, cmd;
	private boolean clientStart;

	boolean superPeer;

	String session, group;

	Player player;
	//ArrayList<Player> playerList;
	ArrayList<String> roomList;
	String roomName;

	int id; // login

	private boolean inRoom, ingGame;
	private int maxPoint, curPoint;

	class Player {
		private int id;
		private float x, y, vx, vy;
		private boolean kick;

		// id in room == index
		public Player(int id, float x, float y, boolean kick) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.vx = 0;
			this.vy = 0;
			this.kick = kick;
		}

		public void setPlayer(float x, float y, float vx, float vy, boolean kick) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.kick = kick;
		}
	}

//	public void setPlayer(int id, float x, float y, float vx, float vy, boolean kick) {
//		this.playerList.get(id).id = id;
//		this.playerList.get(id).x = x;
//		this.playerList.get(id).y = y;
//		this.playerList.get(id).vx = vx;
//		this.playerList.get(id).vy = vy;
//		this.playerList.get(id).kick = kick;
//	}

	Client(int id, String session, String group) {

		this.superPeer = false;
		this.session = session;
		this.group = group;
		this.inRoom = false;
		this.ingGame = false;
		this.id = id;
		this.clientStart = true;

		//playerList = new ArrayList<Player>();
		// this.player = new Player(id, 0, 0, false);

		clientStub = new CMClientStub();
		clientHandler = new ClientHandler(clientStub);
		clientStub.setAppEventHandler(clientHandler);

		this.session = session;
		this.group = group;

		roomList = new ArrayList<String>();
	}

	// return "client application service" - interaction with CM
	public CMClientStub getClientStub() {
		return clientStub;
	}

	// get XY from GUI
//	public void getInfo() {
//		for (int i = 0; i < this.playerList.size(); i++) {
//			this.playerList.get(i).setPlayer(GUI.player.get(i).x, GUI.player.get(i).y, GUI.player.get(i).vx,
//					GUI.player.get(i).vy, false);
//		}
//	}

	// ***** client control *****
	public void setSuper(boolean superPeer) {
		this.superPeer = superPeer;

//		if (superPeer) {
//			playerList = new ArrayList<Player>();
//		}
	}

	public void setRoomList(String[] rooms) {
		roomList.clear();
		for (int i = 0; i < rooms.length; i++) {
			roomList.add(rooms[i]);
		}
	}

	public ArrayList<String> getRoomList() {
		CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMSession session = interInfo.findSession(myself.getCurrentSession());
		Vector<CMGroup> roomGroup = session.getGroupList();
		ArrayList<String> roomList = new ArrayList<String>();

		for (int i = 0; i < roomGroup.size(); i++) {
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
		if (this.superPeer) {
			CMUserEvent cme = new CMUserEvent();
			cme.setID(this.id);
			cme.setStringID("startGame");
			cme.setEventField(CMInfo.CM_INT, "ingGame", Integer.toString(1)); // send room number

			multicast(cme);

			this.ingGame = true;

			return true;
		}
		return false;
	}

	public void update() {
		CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
		CMConfigurationInfo confInfo = clientStub.getCMInfo().getConfigurationInfo();
		// System.out.println("====== test multicast chat in current group");

		// check user state
		CMUser myself = interInfo.getMyself();
		if (myself.getState() != CMInfo.CM_SESSION_JOIN) {
			System.out.println("You must join a session and a group for multicasting.");
			return;
		}

		// check communication architecture
		if (!confInfo.getCommArch().equals("CM_PS")) {
			System.out.println("CM must start with CM_PS mode which enables multicast per group!");
			return;
		}

		for (int i = 0; i < GUI.player.size(); i++) {

			CMUserEvent cme = new CMUserEvent();
			cme.setID(GUI.player.get(i).id);
			cme.setStringID("update");
			cme.setEventField(CMInfo.CM_FLOAT, "x", Float.toString(GUI.player.get(i).x)); // send x=x
			cme.setEventField(CMInfo.CM_FLOAT, "y", Float.toString(GUI.player.get(i).y)); // send y=y
			cme.setEventField(CMInfo.CM_FLOAT, "vx", Float.toString(GUI.player.get(i).vx)); // send x=x
			cme.setEventField(CMInfo.CM_FLOAT, "vy", Float.toString(GUI.player.get(i).vy)); // send y=y
			//cme.setEventField(CMInfo.CM_INT, "kick", GUI.player.get(i).kick ? "1" : "0"); // send kick=0,1

			// cme.setHandlerSession(myself.getCurrentSession());
			// cme.setHandlerGroup(myself.getCurrentGroup());

			multicast(cme);
			//System.out.print("잘되었습니다." + i + " ");
		}
		// System.out.println();
	}

//	public void kick() {
//		CMUserEvent cme = new CMUserEvent();
//		cme.setID(this.playerID);
//		cme.setStringID("kick");
//		cme.setEventField(CMInfo.CM_INT, "kick", Integer.toString(1)); //send kick=true
//		multicast(cme);
//	}

	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String cmd = null;

		while (clientStart) {
			System.out.print("> ");
			try {
				cmd = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (cmd.contentEquals("enter")) {
				enterRoom("g2");
			} else if (cmd.contentEquals("exit")) {
				exitRoom();
			}
		}
	}

	public void enterRoom(String roomName) {
		boolean ret = clientStub.leaveSession();
		CMSessionEvent se;
		String sessionName = "session2";

		// delay(2sec)
		wait_sec(2);

		if (ret) {
			se = clientStub.syncJoinSession(sessionName);

			if (se != null) {
				System.out.println("successfully joined a session that has (" + se.getGroupNum() + ") groups.");

				// is room available?
				if (!enterRoomRequest(roomName)) {
					// return to original session
					exitRoom();
				}
			} else {
				System.err.println("failed the session-join request!");
			}

			return;
		}
		System.out.println("fail to exit session");

	}

	private boolean enterRoomRequest(String roomName) {
		CMDummyEvent due, ans;

		due = new CMDummyEvent();
		due.setID(id);
		due.setDummyInfo("enter " + roomName);

		ans = (CMDummyEvent) clientStub.sendrecv(due, "SERVER", CMInfo.CM_DUMMY_EVENT, id, 3000);

		if (ans != null) {
			if (ans.getDummyInfo().contentEquals("okay")) {
				clientStub.changeGroup(roomName);
				return true;
			}
		}
		return false;
	}

	public void exitRoom() {

		System.out.println("leave session");
		boolean ret = clientStub.leaveSession();
		CMSessionEvent se;
		String sessionName = "session1";

		// delay(2sec)
		wait_sec(2);

		if (ret) {
			se = clientStub.syncJoinSession(sessionName);

			if (se != null)
				System.out.println("successfully joined a session that has (" + se.getGroupNum() + ") groups.");
			else
				System.err.println("failed the session-join request!");
			return;
		}
		System.out.println("fail to exit session");
	}

	private void wait_sec(long time) {
		long save_time = System.currentTimeMillis();
		long curr_time = 0;
		long wait_time = time * 1000;
		while ((curr_time - save_time) < wait_time) {
			curr_time = System.currentTimeMillis();
		}
	}
	// ***** user interaction *****

	// ***** game processing *****
	private boolean endGame() {
		if (this.superPeer) {
			if (curPoint == maxPoint) {
				this.ingGame = false;

				CMUserEvent cme = new CMUserEvent();
				cme.setID(id);
				cme.setStringID("endGame");
				cme.setEventField(CMInfo.CM_INT, "ingGame", Integer.toString(0)); // send ingGame=false (gameover)

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

//	public void setPlayerList(int playerID, Player player) {
//		this.playerList.add(playerID, player);
//		this.playerList.remove(playerID + 1);
//	}
	// ***** client getter, setter *****

	public void multicast(CMEvent cme) {
		// clientStub.multicast(cme, "session1", "g1");
		clientStub.cast(cme, "session1", "g1");
	}

	public void init() {
		// TODO Auto-generated method stub
		boolean ret;
		this.clientHandler.setClient(this); // init
		this.clientStub.startCM();

		wait_sec(2);

		// get client name!
		ret = clientStub.loginCM("user1", "");

		wait_sec(2);

		boolean bRequestResult = false;
		System.out.println("====== request session info from default server");
		bRequestResult = clientStub.requestSessionInfo();
		if (bRequestResult)
			System.out.println("successfully sent the session-info request.");
		else
			System.err.println("failed the session-info request!");
		System.out.println("======");

		if (ret)
			System.out.println(ret + ": successfully sent the session-join request.");
		else
			System.err.println(ret + ": failed the session-join request!");

		//run();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String args[]) {
		Client client = new Client(1, "session1", "g1");
		client.superPeer = true;
		
		GUI.superPeer = client.superPeer;
		GUI.leftPlayer = 2;
		GUI.rightPlayer = 1;
		GUI.myNum = 1;
		PApplet.main(GUI.class);

		client.init();

		while (true) {
//			for (int i = 0; i < client.playerList.size(); i++) {
//				System.out.print(i + "번x:" + GUI.player.get(i).x + ", " + i + "번y:" + GUI.player.get(i).y + ", ");
//			}
//			System.out.println();

			//client.getInfo(); // update from gui

			if (!client.superPeer) {
				if (GUI.keyType) {
					CMDummyEvent due = new CMDummyEvent();
					due.setID(client.id);
					due.setDummyInfo(GUI.keypress);
					client.clientStub.cast(due, "session1", "g1");
					GUI.keyType = false;
				}
			} else {
				client.update(); // muticast in my session group
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}