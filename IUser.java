import java.rmi.*;

public interface IUser extends Remote{
    public String getUserName() throws RemoteException;
    public void receiveMessage(String fromUserName,String message) throws RemoteException;
}
