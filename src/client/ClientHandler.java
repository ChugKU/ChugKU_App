package client; //jihyun hi~^^~f

import kr.ac.konkuk.ccslab.cm.event.CMDataEvent;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

public class ClientHandler implements CMAppEventHandler {
   private Client client;
   private CMClientStub clientStub;
   boolean draw, update;

   public ClientHandler(CMClientStub stub) {
      clientStub = stub;
   }

   public void setClient(Client client) {
      this.client = client;
   }

   @Override
   public void processEvent(CMEvent cme) {

      switch (cme.getType()) {

      case CMInfo.CM_SESSION_EVENT:
         processSessionEvent(cme);
         break;
      case CMInfo.CM_USER_EVENT:
         processUserEvent(cme);
         break;
      case CMInfo.CM_DUMMY_EVENT:
         processDummyEvent(cme);
         break;
      case CMInfo.CM_DATA_EVENT:
         processDataEvent(cme);
         break;
      default:
         return;
      }

   }

   private void processUserEvent(CMEvent cme) {

      CMUserEvent ue = (CMUserEvent) cme;
      String action = ue.getStringID();
      // System.out.println(action + " 諛");

      switch (action) {
      case "startGame":
         client.setIngGame(true);
         break;

      case "gameover":
    	  GUI.gameover = true;
          GUI.leftScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "leftScore"));
          GUI.rightScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "rightScore"));
         break;

      case "update":

         if (!client.superPeer) {
            int id = ue.getID(); 
            float x = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "x"));
            float y = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "y"));
            float vx = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "vx"));
            float vy = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "vy"));

            GUI.player.get(id).x = x;
            GUI.player.get(id).y = y;
            GUI.player.get(id).vx = vx;
            GUI.player.get(id).vy = vy;
         }
         break;

       
   	  case "gameset":
             GUI.gameset = 5;
             GUI.gamesetFlag = false;
             GUI.leftScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "leftScore"));
             GUI.rightScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "rightScore"));
             
//             if(client.superPeer && GUI.gameover) {
//            	 client.gameover();
//             }
             break;  
  
      default:
//         int id = ue.getID(); //room index
//         float x = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "x"));
//         float y = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "y"));
//         float vx = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "vx"));
//         float vy = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "vy"));
//         int kick = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "kick"));
//      
//         System.out.println(id + "�蹂대�  諛 " + x + " " + y + " " + vx + " " + vy + " ");
//         client.setPlayer(id, x, y, vx, vy,  kick==0 ? false : true);
//         
//         client.setPlayer(id, x, y, vx, vy, false);
//         GUI.player.get(id).x = x;
//         GUI.player.get(id).y = y;
//         GUI.player.get(id).vx = vx;
//         GUI.player.get(id).vy = vy;   
         break;
      }

   }

   private void processSessionEvent(CMEvent cme) {
      CMSessionEvent se = (CMSessionEvent) cme;
      switch (se.getID()) {
      case CMSessionEvent.REGISTER_USER_ACK:
         if (se.getReturnCode() == 1) {
            // user registration succeeded
            System.out.println("User[" + se.getUserName() + "] successfully registered at time["
                  + se.getCreationTime() + "].");
         } else {
            // user registration failed
            System.out.println("User[" + se.getUserName() + "] failed to register!");
         }
         break;
      case CMSessionEvent.UNEXPECTED_SERVER_DISCONNECTION:
         System.err.println("Unexpected disconnection from [" + se.getChannelName() + "] with key["
               + se.getChannelNum() + "]!");
         break;
      case CMSessionEvent.INTENTIONALLY_DISCONNECT:
         System.err.println("Intentionally disconnected all channels from [" + se.getChannelName() + "]!");
         break;
      default:
         return;
      }
   }

   private void processDummyEvent(CMEvent cme) {
      CMDummyEvent due = (CMDummyEvent) cme;

      if (client.superPeer) {
         switch (due.getDummyInfo()) {
         case "a":
            GUI.player.get(due.getID()).vx = -3.0f;
            break;
         case "d":
            GUI.player.get(due.getID()).vx = 3.0f;
            break;
         case "w":
            GUI.player.get(due.getID()).vy = -3.0f;
            break;
         case "s":
            GUI.player.get(due.getID()).vy = 3.0f;
            break;
         case " ":
            GUI.player.get(0).vx = (float) ((GUI.player.get(0).x - GUI.player.get(due.getID()).x) * 0.15);
            GUI.player.get(0).vy = (float) ((GUI.player.get(0).y - GUI.player.get(due.getID()).y) * 0.15);
            break; 
         default:
            break;
         }
      } else {
    	  switch(due.getDummyInfo()) {
    	  case "gameover":
    		  GUI.gameover = true;
    		  break;
    	  default:
    		break;
    	  }
      }

//      System.out.println("Dummy Event: "+due.getDummyInfo());
//      // Parse Client-Server Request
//      String inputStr[] = due.getDummyInfo().split(" ");
//      
//      switch (inputStr[0]) {
//      case "okay":
//         clientStub.leaveSession();
//         clientStub.joinSession("session2");
//         
//         clientStub.changeGroup(inputStr[1]);
//         break;
//      case "deny":
//         // Room Access Deny
//         client.updateRoomList();
//         break;
//      case "ingGame":
//         CMDummyEvent response = new CMDummyEvent();
//         response.setID(due.getID());
//         if (client.isIngGame()) {
//            due.setDummyInfo("deny");
//         } else {
//            due.setDummyInfo("okay");
//         }
//         clientStub.send(due, "SERVER");
//         break;
//      default:
//         return;
//      }

      return;
   }

   private void processDataEvent(CMEvent cme) {
      CMDataEvent de = (CMDataEvent) cme;
      switch (de.getID()) {
      case CMDataEvent.NEW_USER:
         System.out.println("[" + de.getUserName() + "] enters group(" + de.getHandlerGroup() + ") in session("
               + de.getHandlerSession() + ").");
         break;
      case CMDataEvent.REMOVE_USER:
         System.out.println("[" + de.getUserName() + "] leaves group(" + de.getHandlerGroup() + ") in session("
               + de.getHandlerSession() + ").");
         break;
      default:
         return;
      }
   }

}