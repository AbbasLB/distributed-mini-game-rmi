import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IEntryNode extends Remote{
    ZoneDescription registerZone(IZoneNode zoneNode) throws RemoteException;
    ZoneResponse registerPlayer(IPlayer player,Coordinates playerCoordinates) throws RemoteException;
}
