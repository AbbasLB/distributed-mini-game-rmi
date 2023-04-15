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

    private void sendHello(IPlayer player,String playerId,Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();
        IPlayer playerBottom = coordinatesToPlayer.get(new Coordinates(x+1, y));
        IPlayer playerTop = coordinatesToPlayer.get(new Coordinates(x-1, y));
        IPlayer playerRight = coordinatesToPlayer.get(new Coordinates(x, y+1));
        IPlayer playerLeft = coordinatesToPlayer.get(new Coordinates(x, y-1));
        IPlayer[] players = new IPlayer[]{ playerBottom, playerTop, playerRight, playerLeft };
        for (IPlayer iPlayer : players) {
            if(iPlayer!=null)
            {
                String otherPlayerId;
                try {
                    otherPlayerId= iPlayer.getId();
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(iPlayer);
                    continue;
                } 
                try {
                    player.receiveMessage(otherPlayerId+ ": Hello " + playerId);
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(player);
                    return;
                }
                try {
                    iPlayer.receiveMessage(playerId + ": Hello " + otherPlayerId);
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(iPlayer);
                    continue;
                }
            }
        }
    }
    private void broadcastPlayerCoords(String playerId,Coordinates playerCoordinates)
    {
        HashMap<String,Coordinates>  newPlayerMap = new HashMap<String,Coordinates>();
        newPlayerMap.put(playerId, playerCoordinates);
        for (ConcurrentHashMap.Entry<Coordinates, IPlayer> player : coordinatesToPlayer.entrySet()) {
            try {
                player.getValue().updateMap(newPlayerMap, false, zoneDescription);
            } catch (RemoteException e) {
                unRegisterPlayerInternal(player.getValue());
            }
        }
    }
    private void sendAllPlayersCoords(IPlayer player) throws RemoteException
    {
        player.updateMap(new HashMap<String,Coordinates>(playersToCoordinates),true, zoneDescription);
    }

    //TODO: Handle Failures
    //TODO: Add synchronization
    @Override
    public ZoneResponse registerPlayer(IPlayer player, Coordinates playerCoordinates) throws RemoteException {
        String playerId = player.getId();
        try {
            playerId = player.getId();
        } catch (RemoteException e) {
            return new ZoneResponse("Player Disconnected", false, zoneDescription)
        }
        
        if(playersToCoordinates.containsKey(playerId))
            return new ZoneResponse("Player already registered with that username.", false, zoneDescription);
        if(coordinatesToPlayer.putIfAbsent(playerCoordinates,player)==null){
            playersToCoordinates.putIfAbsent(playerId, playerCoordinates);
            
            broadcastPlayerCoords(playerId,playerCoordinates);
            sendAllPlayersCoords(player);
            sendHello(player,playerId,playerCoordinates);
            
            //TODO: check hello order
            return new ZoneResponse("Player registered to zone ("+playerCoordinates.getX()+","+playerCoordinates.getY()+")", true, zoneDescription);
        }
        return new ZoneResponse("Coordinates occupied by another player", false, zoneDescription);
        
    }

    private void unRegisterPlayerInternal(IPlayer player)
    {
        playersToCoordinates.remove(player);
        // TODO: Delete player from both hashmaps 
        // send position change to other players
        // create separate function to handle remove players on failures
    }
    private void unRegisterPlayerInternal(String playerId)
    {
        Coordinates coordinates=  playersToCoordinates.remove(playerId);
        if(coordinates!=null)
            coordinatesToPlayer.remove(coordinates);
    }

    @Override
    public void unRegisterPlayer(IPlayer player) throws RemoteException {
        unRegisterPlayerInternal(player);
    }

    //TODO: Add synchronization
    @Override
    public ZoneResponse movePlayer(IPlayer player, Direction direction) throws RemoteException {

        //MOve player in both hashmaps
        // //should call updateMap on all players including sender and sends hello to neighboring players(4 neighbors)
        throw new UnsupportedOperationException("Unimplemented method 'movePlayer'");
    }

    
    
}
