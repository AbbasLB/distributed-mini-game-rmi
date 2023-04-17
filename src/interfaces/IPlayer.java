package interfaces;
import java.util.HashMap;

import models.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayer extends Remote{
    public String getId() throws RemoteException;
    public void updateMap(HashMap<String,Coordinates>  players, boolean zoneChanged,ZoneDescription<IZoneNodePlayer> zoneDescription)throws RemoteException;
    public void receiveMessage(String message) throws RemoteException;
}