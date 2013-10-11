package server;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientDataStructure {
	//Nachrichten des Clients
	private Queue<String> messages;
	//Letzte Nachricht
	private String lastMessage;
	//TimeOut für Speicher der zuletzt abgeholten Nachricht
	private int secHoldLastMsg;
	private Timer timer;
	//Konstante zur Umrechnung von sec auf msec
	private final static int SEC_TO_MSEC = 1000;
	
	public ClientDataStructure(int secHoldLastMsg){
		this.messages = new ConcurrentLinkedQueue<String>();
		this.timer = new Timer();
		this.secHoldLastMsg = secHoldLastMsg;
	}
	
	public void setMessage(String msg){
		this.lastMessage = msg;
		//Nachricht wird zur Queue hinzugefuegt
		this.messages.add(msg);
		this.timer.schedule(new TimerTask(){

			@Override
			public void run() {
				lastMessage = null;
			}
			
		}, this.secHoldLastMsg*SEC_TO_MSEC);
	}
	
	public String getNextMessage(){
		//Nachricht wird aus Queue geloescht
		return this.messages.poll();
	}
	
	public String getLastMessage(){
		return this.lastMessage;
	}

}