import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneNode implements IZoneNode,Serializable {

    private ZoneDescription zoneDescription;
    private boolean zoneReady;
    private ZoneNeighbors zoneNeighbors;

    private ConcurrentHashMap<String,Coordinates> playersToCoordinates = new ConcurrentHashMap<String,Coordinates>();
    private ConcurrentHashMap<Coordinates,IPlayer> coordinatesToPlayer = new ConcurrentHashMap<Coordinates,IPlayer>();

    public ZoneNode(IEntryNode entryNode)
    {
        //TODO: Export 
        try{
            zoneReady=false;
            zoneDescription = entryNode.registerZone(this);
        }
        catch (Exception e){
            System.err.println("Failed To RegisterZone:" + e) ;
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void linkNeighbors(ZoneNeighbors neighbors) throws RemoteException {
        zoneNeighbors = neighbors;
    }

    @Override
    public void MarkAsReady() throws RemoteException {
        zoneReady = true;
    }
    //TODO: Add synchronization
    @Override
    public ZoneResponse registerPlayer(IPlayer player, Coordinates playerCoordinates) throws RemoteException {
        String playerId = player.getId();
        if(playersToCoordinates.containsKey(playerId))
            return new ZoneResponse("Player already registered with that username.", false, zoneDescription);
        if(coordinatesToPlayer.putIfAbsent(playerCoordinates,player)==null){
            playersToCoordinates.putIfAbsent(playerId, playerCoordinates);
            return new ZoneResponse("Player registered to zone ("+playerCoordinates.getX()+","+playerCoordinates.getY()+")", true, zoneDescription);
        }
        //register player in zone, send update to all other players, and give the player coordinates of the other players
        //TODO:Check if should say hello
        return new ZoneResponse("Coordinates occupied by another player", false, zoneDescription);
        
    }

    @Override
    public void unRegisterPlayer(IPlayer player) throws RemoteException {

        // TODO: Delete player from both hashmaps 
        // send position change to other players
        // create separate function to handle remove players on failures
        throw new UnsupportedOperationException("Unimplemented method 'unRegisterPlayer'");
    }

    //TODO: Add synchronization
    @Override
    public ZoneResponse movePlayer(IPlayer player, Direction direction) throws RemoteException {

        //MOve player in both hashmaps
        // //should call updateMap on all players including sender and sends hello to neighboring players(4 neighbors)
        throw new UnsupportedOperationException("Unimplemented method 'movePlayer'");
    }

    
    
}
