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

   synchronized private void processUserEvent(CMEvent cme) {

      CMUserEvent ue = (CMUserEvent) cme;
      String action = ue.getStringID();

      switch (action) {
      case "startGame":
    	 System.out.println("start");
         client.setStart(true);
         GUI.gameMode=5;
         break;

      case "gameover":
    	  client.setStart(false);
    	  GUI.gameover = true;
          GUI.leftScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "leftScore"));
          GUI.rightScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "rightScore"));
         break;

      case "update":

         
            int id = ue.getID(); // room index

            float x = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "x"));
            float y = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "y"));
            float vx = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "vx"));
            float vy = Float.parseFloat(ue.getEventField(CMInfo.CM_FLOAT, "vy"));
            
            System.out.println(ue.getID() + " " + ue.getEventField(CMInfo.CM_FLOAT, "x")
	            + " " + ue.getEventField(CMInfo.CM_FLOAT, "y")
	            + " " + ue.getEventField(CMInfo.CM_FLOAT, "vx")
	            + " " + ue.getEventField(CMInfo.CM_FLOAT, "vy"));
            if(GUI.player.isEmpty()) {
            	GUI.gameMode = 5;
            	return;
            }
            GUI.player.get(id).x = x;
	        GUI.player.get(id).y = y;
	        GUI.player.get(id).vx = vx;
	        GUI.player.get(id).vy = vy;
           
         
         break;

       
   	  case "gameset":
             GUI.gameset = 5;
             GUI.gamesetFlag = false;
             GUI.leftScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "leftScore"));
             GUI.rightScore = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "rightScore"));
             
             break;  
  
      default:
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

   synchronized private void processDummyEvent(CMEvent cme) {
      CMDummyEvent due = (CMDummyEvent) cme;

 //     if (client.superPeer) {
         switch (due.getDummyInfo()) {
         case "a":
            GUI.computSet.get(due.getID()).vx = -3.0f;
            break;
         case "d":
            GUI.computSet.get(due.getID()).vx = 3.0f;
            break;
         case "w":
            GUI.computSet.get(due.getID()).vy = -3.0f;
            break;
         case "s":
            GUI.computSet.get(due.getID()).vy = 3.0f;
            break;
         case " ":
            GUI.computSet.get(0).vx = (float) ((GUI.computSet.get(0).x - GUI.computSet.get(due.getID()).x) * 0.15);
            GUI.computSet.get(0).vy = (float) ((GUI.computSet.get(0).y - GUI.computSet.get(due.getID()).y) * 0.15);
            break; 
         default:
            break;
         }
 //     } else {
    	  switch(due.getDummyInfo()) {
    	  case "gameover":
    		  GUI.gameover = true;
    		  break;
    	  default:
    		break;
//    	  }
      }

      return;
   }

   private void processDataEvent(CMEvent cme) {
      CMDataEvent de = (CMDataEvent) cme;

      
      client.updatePlayerList();

      switch (de.getID()) {
      case CMDataEvent.NEW_USER:
         System.out.println("[" + de.getUserName() + "] enters group(" + de.getHandlerGroup() + ") in session("
               + de.getHandlerSession() + ").");

         break;
      case CMDataEvent.REMOVE_USER:
         System.out.println("[" + de.getUserName() + "] leaves group(" + de.getHandlerGroup() + ") in session("
               + de.getHandlerSession() + ").");
         // update player list && send free requests to Server
         

         break;
      default:
         return;
      }
   }

}