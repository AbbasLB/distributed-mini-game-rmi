import java.rmi.server.*; 
import java.rmi.registry.*;

public class EntryNodeServer {
 
    public static void  main(String [] args) {
        try {

        // TODO: make params dynamic
        EntryNode h = new EntryNode(20,2);
        IEntryNode h_stub = (IEntryNode) UnicastRemoteObject.exportObject(h, 0);
        // Register the remote object in RMI registry with a given identifier
        
        Registry registry= LocateRegistry.getRegistry(); 
        registry.bind("GameEntryService", h_stub);
        System.out.println ("Entry Node ready...");

        } catch (Exception e) {
            System.err.println("Error on server :" + e) ;
            e.printStackTrace();
        }
    }
}
