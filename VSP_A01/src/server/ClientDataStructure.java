package server;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * 
 * @author Manuel Boesch, Sebastian Mueller
 *
 */
public class ClientDataStructure {
	//Nachrichten des Clients
	private Queue<String> messages;
	//Letzte Nachricht
	private String lastMessage;
	//TimeOut f�r Speicher der zuletzt abgeholten Nachricht
	private int secHoldLastMsg;
	//Maximale Anzahl von Nachrichten in der Nachrichtenqueue
	private int maxNumberOfMessages;
	private Timer timer;
	//Konstante zur Umrechnung von sec auf msec
	private final static int SEC_TO_MSEC = 1000;
	
	/**
	 * Konstruktor
	 *  
	 * @param secHoldLastMsg
	 */
	public ClientDataStructure(int secHoldLastMsg, int maxNumberOfMessages){
		this.messages = new ConcurrentLinkedQueue<String>();
		this.timer = new Timer();
		this.secHoldLastMsg = secHoldLastMsg;
		this.maxNumberOfMessages = maxNumberOfMessages;
	}
	
	public void setMessage(String msg){
		while(this.messages.size()>=this.maxNumberOfMessages){
			this.messages.poll();
		}
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