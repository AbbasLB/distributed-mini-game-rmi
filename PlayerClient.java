import java.io.Console;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class PlayerClient{
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

	Scanner sc = new Scanner(System.in);
	IPlayer player;
	ZoneResponse response;
    IZoneNode curZone;
	System.out.println("Welcome to the game :)");
	System.out.println();

	do{
        //TODO: fix userName to be unique(add random number)
		System.out.print("Enter Your UserName: ");
		String userName = sc.nextLine();
        System.out.print("Enter Your X Coordinate: ");
        int xPos=sc.nextInt();
        System.out.print("Enter Your Y Coordinate: ");
        int yPos=sc.nextInt();
        sc.nextLine();

		Player playerImpl =new Player(userName);
		player = (IPlayer) UnicastRemoteObject.exportObject(playerImpl, 0);
		response = gameEntryNode.registerPlayer(playerImpl, new Coordinates(xPos, yPos));
		if(!response.isSuccess())
			System.out.println(response.getMessage());
	}while(!response.isSuccess());

	curZone=response.getZoneDescription().getZoneNode();
	System.out.println();

	while(true)
	{
		System.out.print("Move Direction(L,R,U,D) (Q Quit):");
        String input=sc.nextLine();
        Direction dir=Direction.Left;
        boolean quit=false;
        switch(input)
        {
            case "L":
                dir=Direction.Left;
                break;
            case "R":
                dir=Direction.Right;
            break;
            case "U":
                dir=Direction.Up;
            break;
            case "D":
                dir=Direction.Down;
            break;
            case "Q":
            quit=true;
            break;
            default:
            System.out.print("Invalid Command");
                continue;

        }
        if(quit)
            break;
        response= curZone.movePlayer(player, dir);
        if(response.isSuccess())
            curZone=response.getZoneDescription().getZoneNode();
        if(response.getMessage()!=null && !response.getMessage().isEmpty())
            System.out.println(response.getMessage());
	}
	curZone.unRegisterPlayer(player.getId());
	System.out.println();
	System.out.println("Bye Bye :)");
	sc.close();
	System.exit(0);
	} catch (Exception e)  {
		System.err.println("Error on client: " + e);
	}
  }
}