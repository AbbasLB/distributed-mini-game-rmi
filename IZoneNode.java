import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IZoneNode  extends Remote{
    public void linkNeighbors(ZoneNeighbors neighbors) throws RemoteException;
    public void MarkAsReady() throws RemoteException;
    public ZoneResponse registerPlayer(IPlayer player,Coordinates playerCoordinates) throws RemoteException;
    public void unRegisterPlayer(IPlayer player) throws RemoteException;
    public ZoneResponse movePlayer(IPlayer player,Direction direction) throws RemoteException;
}
