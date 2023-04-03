import java.rmi.*;

public interface IChatService extends Remote {
	public boolean register(IUser client) throws RemoteException;
	public void sendMessage(Message message) throws RemoteException;
	public String getHistory() throws RemoteException;
	public void unregister(IUser client) throws RemoteException;
}
