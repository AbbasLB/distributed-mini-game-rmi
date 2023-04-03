import java.io.Console;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient{
  public static void main(String [] args) {
	
	try {
	  if (args.length < 1) {
	   System.out.println("Usage: java ChatClient <rmiregistry host>");
	   return;
	}
	String host = args[0];

	// Get remote object reference
	Registry registry = LocateRegistry.getRegistry(host); 
	IChatService chatService = (IChatService) registry.lookup("ChatService");

	Scanner sc = new Scanner(System.in);
	IUser user;
	boolean res;
	System.out.println("Hello :)");
	System.out.println();

	do{
		System.out.print("Enter Your UserName: ");
		String userName = sc.nextLine();
		User userImpl =new User(userName);
		user = (IUser) UnicastRemoteObject.exportObject(userImpl, 0);
		res = chatService.register(user);
		if(!res)
			System.out.println("Username already exists.");
	}while(!res);
	
	System.out.println();
	System.out.print(chatService.getHistory());

	while(true)
	{
		System.out.print(user.getUserName()+": ");
		String messageText= sc.nextLine();
		System.out.println();
		chatService.sendMessage(new Message(messageText, user.getUserName()));
			
		if(messageText.equals("\\exit"))
			break;	
	}
	chatService.unregister(user);
	System.out.println();
	System.out.println("Bye Bye :)");
	sc.close();
	System.exit(0);
	} catch (Exception e)  {
		System.err.println("Error on client: " + e);
	}
  }
}