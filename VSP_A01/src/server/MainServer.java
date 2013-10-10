package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import metaData.MessageService;

public class MainServer {

	public static void main(String[] args) {
		/*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
        try {
            String name = "MessageService";
            MessageService service = new ChatServer(15, 30);
            MessageService stub =
                (MessageService) UnicastRemoteObject.exportObject(service, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("MessageService bound");
        } catch (Exception e) {
            System.err.println("MessageService exception:");
            e.printStackTrace();
        }
	}

}
