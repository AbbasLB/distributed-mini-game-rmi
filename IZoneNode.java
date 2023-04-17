import java.rmi.RemoteException;

public interface IZoneNode  extends IZoneNodePlayer{
    public void linkNeighbors(ZoneNeighbors neighbors) throws RemoteException;
    public void MarkAsReady() throws RemoteException;
}
