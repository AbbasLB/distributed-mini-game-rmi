package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

import models.Coordinates;
import models.ZoneResponse;

public interface IEntryNodePlayer extends Remote{
    ZoneResponse registerPlayer(IPlayer player,Coordinates playerCoordinates) throws RemoteException;
}
