import java.rmi.RemoteException;

public interface IEntryNode extends IEntryNodePlayer{
    ZoneDescription<IZoneNode> registerZone(IZoneNode zoneNode) throws RemoteException;
}
