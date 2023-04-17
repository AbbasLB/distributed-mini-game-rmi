package interfaces;
import java.rmi.RemoteException;

import models.ZoneDescription;

public interface IEntryNode extends IEntryNodePlayer{
    ZoneDescription<IZoneNode> registerZone(IZoneNode zoneNode) throws RemoteException;
}
