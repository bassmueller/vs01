package server;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientDataStructure {
	private Queue<String> messages;
	private String lastMessage;
	private int secHoldLastMsg;
	private Timer timer;
	private final static int SEC_TO_MSEC = 1000;
	
	public ClientDataStructure(int secHoldLastMsg){
		this.messages = new ConcurrentLinkedQueue<String>();
		this.timer = new Timer();
		this.secHoldLastMsg = secHoldLastMsg;
	}
	
	public void setMessage(String msg){
		this.lastMessage = msg;
		this.messages.add(msg);
		this.timer.schedule(new TimerTask(){

			@Override
			public void run() {
				lastMessage = null;
			}
			
		}, this.secHoldLastMsg*SEC_TO_MSEC);
	}
	
	public String getNextMessage(){
		return this.messages.poll();
	}
	
	public String getLastMessage(){
		return this.lastMessage;
	}

}