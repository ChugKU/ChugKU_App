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
import java.util.Random;

import client.GUI.Player;

public class Client {
   private CMClientStub clientStub;
   private ClientHandler clientHandler;

   private int state, cmd;
   private boolean clientStart;

   boolean superPeer;

   String session, group;

   Player player;
   ArrayList<Player> playerList;
   ArrayList<String> roomList;
   String roomName;

   String name_id; // login
   int id; // game interaction

   private int maxPoint, curPoint;

//   class Player {
//	  private String name;
//      private int id;
//      private float x, y, vx, vy;
//      private boolean kick;
//
//      // id in room == index
//      public Player(int id, float x, float y, boolean kick) {
//         this.id = id;
//         this.x = x;
//         this.y = y;
//         this.vx = 0;
//         this.vy = 0;
//         this.kick = kick;
//      }
//
//      public void setPlayer(float x, float y, float vx, float vy, boolean kick) {
//         this.x = x;
//         this.y = y;
//         this.vx = vx;
//         this.vy = vy;
//         this.kick = kick;
//      }
//      
//      public void setName(String name) {
//    	  this.name = name;
//      }
//      
//      public String getName() {
//    	  return name;
//      }
//   }


   Client(int name_id, String session, String group) {

      this.superPeer = false;
      this.session = session;
      this.group = group;
      this.name_id = "user"+name_id;
      this.clientStart = true;

      clientStub = new CMClientStub();
      clientHandler = new ClientHandler(clientStub);
      clientStub.setAppEventHandler(clientHandler);

      roomList = new ArrayList<String>();
      for(int i=0; i<4; i++) {
    	  roomList.add("g"+(i+2));
      }
      
      playerList = new ArrayList<Player>();
      
   }

   // return "client application service" - interaction with CM
   public CMClientStub getClientStub() {
      return clientStub;
   }


   // ***** client control *****
   public void setSuper(boolean superPeer) {
      this.superPeer = superPeer;
   }

   // ***** client control *****\\

   // ***** user interaction *****
   public boolean startGame() {
      this.superPeer = true;
      CMUserEvent cme = new CMUserEvent();
      cme.setID(this.id);
      cme.setStringID("startGame");
      cme.setEventField(CMInfo.CM_INT, "ingGame", Integer.toString(1)); // send room number

      multicast(cme);

      return true;
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
         // System.out.print("잘되었습니다." + i + " ");
      }
      // System.out.println();
   }

//   public void kick() {
//      CMUserEvent cme = new CMUserEvent();
//      cme.setID(this.playerID);
//      cme.setStringID("kick");
//      cme.setEventField(CMInfo.CM_INT, "kick", Integer.toString(1)); //send kick=true
//      multicast(cme);
//   }

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
            enterRoom(2);
            
         } else if (cmd.contentEquals("exit")) {
            exitRoom();
         }
      }
   }

   public boolean enterRoom(int index) {
      boolean ret = clientStub.leaveSession();
      CMSessionEvent se;
      String sessionName = "session2";

      // delay(2sec)
      wait_msec(2000);

      if (ret) {
         se = clientStub.syncJoinSession(sessionName);

         if (se != null) {
            System.out.println("successfully joined a session that has (" + se.getGroupNum() + ") groups.");

            // is room available?
            if (!enterRoomRequest("g"+(index+2))) {
               // return to original session
               exitRoom();
            } else {
            	GUI.enterRoomReq[index] = true;
            	return true;
            }
         } else {
            System.err.println("failed the session-join request!");
            exitRoom();
            return false;
         }
      }
      exitRoom();
      System.out.println("fail to exit session");
      return false;
   }

   private boolean enterRoomRequest(String roomName) {
      CMDummyEvent due, ans;

      due = new CMDummyEvent();
      due.setID(id);
      due.setSender(clientStub.getMyself().getName());
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

   private void exitRoom() {

      System.out.println("leave session");
      boolean ret = clientStub.leaveSession();
      CMSessionEvent se;
      String sessionName = "session1";

      // delay(2sec)
      wait_msec(2000);

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

   private void wait_msec(long time) {
      long save_time = System.currentTimeMillis();
      long curr_time = 0;
      long wait_time = time;
      while ((curr_time - save_time) < wait_time) {
         curr_time = System.currentTimeMillis();
      }
   }
   // ***** user interaction *****

   // ***** game processing *****
   private boolean endGame() {
      if (this.superPeer) {
         if (curPoint == maxPoint) {

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
   public int getMaxPoint() {
      return maxPoint;
   }

   public void setMaxPoint(int maxPoint) {
      this.maxPoint = maxPoint;
   }


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

      wait_msec(2000);

      // get client name!
      ret = clientStub.loginCM(name_id, "");

      wait_msec(2000);

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

     // run();

      try {
         Thread.sleep(8000);
      } catch (InterruptedException e) {
         System.out.println(e.getMessage());
      }
   }

   public static void main(String args[]) {
	   
      Client client = new Client(new Random().nextInt(1000), "session1", "g1");
      client.superPeer = false;
      
      GUI.superPeer = client.superPeer;
      GUI.leftPlayer = 2;
      GUI.rightPlayer = 1;
      GUI.myNum = 3;
      PApplet.main(GUI.class);

      client.init();
      
      int num;
      while (true) {
    	  switch(GUI.gameMode) {
    	  case 1: // game mode
    		  if (!client.superPeer) {
    	            if (GUI.keyType) {
    	               CMDummyEvent due = new CMDummyEvent();
    	               due.setID(client.id);
    	               due.setDummyInfo(GUI.keypress);
    	               client.clientStub.cast(due, "session2", "g1");
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
    		  break;
    	  case 2: // lobby mode > wait for start
    		  for(int i=0; i<4; i++) {
    			  if(GUI.enterRoom[i]) {
    				  if (client.enterRoom(i)) {
    					  client.id = client.getMembers();
    				  }
    				  GUI.enterRoom[i] = false;
    				  break;
    			  }
    		  }
    		  
    		  break;
    	  case 3: // super peer request(start game)
    		  num = client.getMembers();
    		  if(num == 1) {
    			  GUI.gameMode = 1;
    		  } else {
    			  client.setSuper(true);
    			  GUI.gameMode = 5;
    			  
    			  //send start signal to peers
    			  CMUserEvent cue = new CMUserEvent();
    			  cue.setID(client.id);
    			  
    			  int index=-1;
    			  for(int i=0; i<4; i++) {
    				  if(GUI.enterRoomReq[i]) {
    					  index=i;
    				  }
    			  }
    			  cue.setStringID("startGame");

                  client.clientStub.cast(cue, "session2","g"+(index+2));
                  client.superPeer = true;
                  
                  CMDummyEvent due = new CMDummyEvent();
                  due.setDummyInfo("busy g" + (index+2));
                  client.getClientStub().send(due, "SERVER");
    		  
    		  }             
    		  break;
    		  
    	  case 4: 
    		  
    		  break;
    	  case 5: // start game: create player objects
    		  num = client.getMembers();
    		  
    		  if(num == 1) {
    			  GUI.gameMode = 1;
    		  } else {
    			  GUI.leftPlayer = (num%2)+(num/2);
    			  GUI.rightPlayer = num/2;
    			  GUI.gameMode=6; // setup players in GUI
    		  }
    		  break;
    	  case 6:
    		 // wait
    		  break;
    	  default:
    		  	System.err.println("Invalid Mode");
    			 break; 
    	  }
         
      }
   }
   
   public int getMembers() {
	   if(clientStub.getGroupMembers()!= null) {
			return clientStub.getGroupMembers().getMemberNum();
	}
	   return -1;
   }

public void updatePlayerList() {
	
	// TODO Auto-generated method stub
	int userNum = 0;
	
	if(clientStub.getGroupMembers()!= null) {
		userNum = clientStub.getGroupMembers().getMemberNum();
	} else {
		System.err.println("Fail to get Group Member");
		return;
	}
	
	
	CMDummyEvent due = new CMDummyEvent();
	if(userNum == 4) {
		// send busy request
		due.setDummyInfo("busy " + roomName);
		
	} else if (userNum < 4) {
		// send free request
		due.setDummyInfo("free " + roomName);
	}
	
	clientStub.send(due, "SERVER");
}

public void setStart(boolean b) {
	// TODO Auto-generated method stub
	if(b) {
		GUI.gameMode=5;
	}else {
		GUI.gameMode=1;
	}
	GUI.start = b;
}

}