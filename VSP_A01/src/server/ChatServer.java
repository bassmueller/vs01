package server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 * 
 * @author Manuel Boesch, Sebastian Mueller
 *
 */
public class ChatServer implements MessageService {

	private int messageID;
	//TimeOut-Zeit für zuletzt ausgelieferte Nachricht
	private int secHoldLastMsg;
	//Gedaechtniszeit bis Löschen des Clients
	private int secLifetimeClient;
	//Datenstruktur zur Speicherung der Client-Daten
	private Map<String, ClientDataStructure> clients;
	//Datenstrukur zur Speicherung des Timer-Outs zur Entfernung der Clients
	private Map<String, Timer> lifetimeClients;
	//Logfile
	private Logger logger;
	private final static int SEC_TO_MSEC = 1000;

	
	/**
	 * Konstruktor mit Standard-Initialisierung des Chat-Servers
	 * 
	 * @param secHoldLastMsg
	 * @param secLifetimeClient
	 */
	public ChatServer(int secHoldLastMsg, int secLifetimeClient) {
	    super();
	    this.clients = new HashMap<String, ClientDataStructure>();
	    this.lifetimeClients = new HashMap<String, Timer>();
	    this.messageID = 0;
	    this.secHoldLastMsg = secHoldLastMsg;
	    this.secLifetimeClient = secLifetimeClient;
	}
	
	@Override
	public String nextMessage(final String clientID) throws RemoteException {
		log(String.format("Client %s holt Nachtricht ab", clientID), false);
		//vorhandene Timer zum Entfernen der Clients canceln
		if(this.lifetimeClients.containsKey(clientID)){
			this.lifetimeClients.get(clientID).cancel();
		}
		
		//Timer konstruieren bzw. neu setzen
		this.lifetimeClients.put(clientID, new Timer());
		//Timer schedulen und TimerTask setzen; TimerTask entfernt den Client vom Server
		this.lifetimeClients.get(clientID).schedule(new TimerTask(){

			@Override
			public void run() {
				clients.remove(clientID);
				lifetimeClients.remove(clientID);
				logger.warning(String.format("Client %s wurde komplett vom Server entfernt, da die maximale Zeitperiode zwischen dem Abholen von Nachrichten ï¿½berschritten wurde", clientID));
			}
			
		}, this.secLifetimeClient*SEC_TO_MSEC);
		
		String returnValue = null;
		if(this.clients.get(clientID) != null){
			returnValue = this.clients.get(clientID).getNextMessage();
		}
		return returnValue;
	}

	@Override
	public void newMessage(String clientID, String message)
			throws RemoteException {
		//Nachricht-Formatierung
		String msg = String.format("<%s> <%s>: <%s> <%s>",this.messageID++, clientID, message, (new Timestamp(new Date().getTime())));
		//Zuweisen der Nachricht zum Client; Anlegen des Clients, falls nicht vorhanden
		if(this.clients.containsKey(clientID)){
			this.clients.get(clientID).setMessage(msg);;
			log(String.format("Nachricht fï¿½r Client %s empfangen: %s", clientID, msg), false);
		}else{
			//
			this.clients.put(clientID, new ClientDataStructure(secHoldLastMsg));
			log(String.format("ClientDataStructure fï¿½r Client %s angelegt", clientID), false);
			this.clients.get(clientID).setMessage(msg);
			log(String.format("Nachricht fï¿½r Client %s empfangen: %s", clientID, msg), false);
		}
	}
	
	private void log(String msg, boolean warning){
		if(this.logger == null){
			this.logger = Logger.getLogger("ChatServerLog");  
		    FileHandler fh;  
		    try {  

		        // This block configure the logger with handler and formatter  
		        fh = new FileHandler("ChatServerLogFile.log");  
		        this.logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  
		        this.logger.setUseParentHandlers(false);

		        // the following statement is used to log any messages  
		        this.logger.info("Logfile initialized...");  

		    } catch (SecurityException e) {  
		        e.printStackTrace();  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }
		}
		
		if(warning){
			this.logger.warning(msg);
		}else{
			this.logger.info(msg);
		}
		
	}
}
