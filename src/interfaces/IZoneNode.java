package interfaces;
import java.rmi.RemoteException;

import models.ZoneNeighbors;

public interface IZoneNode  extends IZoneNodePlayer{
    public void linkNeighbors(ZoneNeighbors neighbors) throws RemoteException;
    public void MarkAsReady() throws RemoteException;
}
