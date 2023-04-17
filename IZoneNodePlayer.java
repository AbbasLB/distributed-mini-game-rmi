import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IZoneNodePlayer  extends Remote{
    public ZoneResponse registerPlayer(IPlayer player,Coordinates playerCoordinates) throws RemoteException;
    public void unRegisterPlayer(String playerId) throws RemoteException;
    public ZoneResponse movePlayer(IPlayer player,Direction direction) throws RemoteException;
}
