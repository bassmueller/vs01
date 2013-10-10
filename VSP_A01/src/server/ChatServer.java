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

import metaData.MessageService;

public class ChatServer implements MessageService {
	
	private int messageID;
	private int secHoldLastMsg;
	private int secLifetimeClient;
	private Map<String, ClientDataStructure> clients;
	private Map<String, Timer> lifetimeClients;
	private Logger logger;
	private final static int SEC_TO_MSEC = 1000;

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
		this.lifetimeClients.put(clientID, new Timer());
		this.lifetimeClients.get(clientID).schedule(new TimerTask(){

			@Override
			public void run() {
				clients.remove(clientID);
				lifetimeClients.remove(clientID);
				logger.warning(String.format("Client %s wurde komplett vom Server entfernt, da die maximale Zeitperiode zwischen dem Abholen von Nachrichten überschritten wurde", clientID));
			}
			
		}, this.secLifetimeClient*SEC_TO_MSEC);
		return this.clients.get(clientID).getNextMessage();
	}

	@Override
	public void newMessage(String clientID, String message)
			throws RemoteException {
		String msg = String.format("<%s> <%s>: <%s> <%s>",this.messageID++, clientID, message, (new Timestamp(new Date().getTime())));
		if(this.clients.containsKey(clientID)){
			this.clients.get(clientID).setMessage(msg);;
			log(String.format("Nachricht für Client %s empfangen: %s", clientID, msg), false);
		}else{
			this.clients.put(clientID, new ClientDataStructure(secHoldLastMsg));
			log(String.format("ClientDataStructure für Client %s angelegt", clientID), false);
			this.clients.get(clientID).setMessage(msg);
			log(String.format("Nachricht für Client %s empfangen: %s", clientID, msg), false);
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
