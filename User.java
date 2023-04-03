import java.io.Serializable;
import java.rmi.*;
import java.util.Scanner;

public  class User  implements IUser, Serializable{
    private String userName;
    public User(String userName)
    {
        this.userName=userName;
    }
    @Override
    public String getUserName() throws RemoteException {
        return userName;
    }
    @Override
    public void receiveMessage(String fromUserName,String message) throws RemoteException {
        System.out.println("\u001B[36m" +"\n"+fromUserName+": "+message+"\u001B[0m");
    }
}

