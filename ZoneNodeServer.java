import java.io.Console;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ZoneNodeServer {
    public static void main(String [] args) {
	
	try {
	  if (args.length < 1) {
	   System.out.println("Usage: java PlayerClient <rmiregistry host>");
	   return;
	}
	String host = args[0];

	// Get remote object reference
	Registry registry = LocateRegistry.getRegistry(host); 
	IEntryNode gameEntryNode = (IEntryNode) registry.lookup("GameEntryService");
    ZoneNode zoneNode =  new ZoneNode();
    IZoneNode h_stub = (IZoneNode) UnicastRemoteObject.exportObject(zoneNode, 0);

    zoneNode.RegisterZone(gameEntryNode, h_stub);
    
    
	} catch (Exception e)  {
		System.err.println("Error on client: " + e);
	}
  }
}
