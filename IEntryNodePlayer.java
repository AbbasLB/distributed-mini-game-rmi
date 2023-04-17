import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IEntryNodePlayer extends Remote{
    ZoneResponse registerPlayer(IPlayer player,Coordinates playerCoordinates) throws RemoteException;
}
