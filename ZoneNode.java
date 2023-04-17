import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneNode implements IZoneNode,Serializable {

    private ZoneDescription<IZoneNode> zoneDescription;
    private boolean zoneReady;
    private ZoneNeighbors zoneNeighbors;

    private ConcurrentHashMap<String,Coordinates> playersToCoordinates = new ConcurrentHashMap<String,Coordinates>();
    private ConcurrentHashMap<Coordinates,SimpleEntry<IPlayer,String>> coordinatesToPlayer = new ConcurrentHashMap<Coordinates,SimpleEntry<IPlayer,String>>();

    public ZoneNode()
    {
        zoneReady=false;
    }
    public void RegisterZone(IEntryNode entryNode,IZoneNode exportedZoneNode)
    {
        try{
            zoneDescription = entryNode.registerZone(exportedZoneNode);
            if(zoneDescription==null)
            {
                System.err.println("Couldn't register zone since all zones registered.") ;
                System.exit(1);
            }
        }
        catch (Exception e){
            System.err.println("Failed To RegisterZone:" + e) ;
            e.printStackTrace();
            System.exit(1);
        }
    }
    private ZoneDescription<IZoneNodePlayer> getPlayerZoneDesc()
    {
        if(zoneDescription==null)
            return null;
        return new ZoneDescription<IZoneNodePlayer>(zoneDescription.getZoneNode(), zoneDescription.getxBase(),zoneDescription.getyBase(), zoneDescription.getxBound(), zoneDescription.getyBound(), zoneDescription.getMatrixSize());
    }

    @Override
    public void linkNeighbors(ZoneNeighbors neighbors) throws RemoteException {
        zoneNeighbors = neighbors;
        System.out.println("Top Neighbor = "+neighbors.getTopZone());
        System.out.println("Bottom Neighbor = "+neighbors.getBottomZone());
        System.out.println("Left Neighbor = "+neighbors.getLeftZone());
        System.out.println("Right Neighbor = "+neighbors.getRightZone());
        System.out.println("----------------");
    }

    @Override
    public void MarkAsReady() throws RemoteException {
        zoneReady = true;
    }

    private void sendHello(IPlayer player,String playerId,Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();
        ArrayList<SimpleEntry<IPlayer,String>> players = new ArrayList<SimpleEntry<IPlayer,String>>();
        players.add( coordinatesToPlayer.get(new Coordinates(x+1, y)) );
        players.add( coordinatesToPlayer.get(new Coordinates(x-1, y)) );
        players.add( coordinatesToPlayer.get(new Coordinates(x, y+1)) );
        players.add( coordinatesToPlayer.get(new Coordinates(x, y-1)) );
        for (SimpleEntry<IPlayer,String> iPlayer : players) {
            if(iPlayer!=null)
            {
                String otherPlayerId;
                try {
                    otherPlayerId= iPlayer.getKey().getId();
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(iPlayer.getValue());
                    continue;
                } 
                try {
                    player.receiveMessage(otherPlayerId+ ": Hello " + playerId);
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(iPlayer.getValue());
                    return;
                }
                try {
                    iPlayer.getKey().receiveMessage(playerId + ": Hello " + otherPlayerId);
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(iPlayer.getValue());
                    continue;
                }
            }
        }
    }
    private void broadcastPlayerCoords(String playerId,Coordinates playerCoordinates)
    {
        HashMap<String,Coordinates>  newPlayerMap = new HashMap<String,Coordinates>();
        newPlayerMap.put(playerId, playerCoordinates);
        for (ConcurrentHashMap.Entry<Coordinates, SimpleEntry<IPlayer,String>> player : coordinatesToPlayer.entrySet()) {
            try {
                player.getValue().getKey().updateMap(newPlayerMap, false, getPlayerZoneDesc());
            } catch (RemoteException e) {
                unRegisterPlayerInternal(player.getValue().getValue());
            }
        }
    }
    private void sendAllPlayersCoords(IPlayer player) throws RemoteException
    {
        player.updateMap(new HashMap<String,Coordinates>(playersToCoordinates),true, getPlayerZoneDesc());
    }

    //TODO: Handle Failures
    //TODO: Add synchronization
    @Override
    public ZoneResponse registerPlayer(IPlayer player, Coordinates playerCoordinates) throws RemoteException {
        if(!zoneReady)
            return new ZoneResponse("Zone not ready yet", false, getPlayerZoneDesc());

        String playerId = player.getId();
        try {
            playerId = player.getId();
        } catch (RemoteException e) {
            return new ZoneResponse("Player Disconnected", false, getPlayerZoneDesc());
        }
        
        if(playersToCoordinates.containsKey(playerId))
            return new ZoneResponse("Player already registered with that username.", false, getPlayerZoneDesc());
        if(coordinatesToPlayer.putIfAbsent(playerCoordinates,new SimpleEntry<IPlayer,String>(player,playerId))==null){
            playersToCoordinates.putIfAbsent(playerId, playerCoordinates);
            
            broadcastPlayerCoords(playerId,playerCoordinates);
            sendAllPlayersCoords(player);
            sendHello(player,playerId,playerCoordinates);
            
            //TODO: check hello order
            return new ZoneResponse("Player registered to zone at position ("+playerCoordinates.getX()+","+playerCoordinates.getY()+")", true, getPlayerZoneDesc());
        }
        return new ZoneResponse("Coordinates occupied by another player", false, getPlayerZoneDesc());
        
    }

    private void unRegisterPlayerInternal(String playerId)
    {
        Coordinates coordinates=  playersToCoordinates.remove(playerId);
        if(coordinates!=null){
            coordinatesToPlayer.remove(coordinates);
            broadcastPlayerCoords(playerId, null);
        }
    }

    @Override
    public void unRegisterPlayer(String playerId) throws RemoteException {
        unRegisterPlayerInternal(playerId);
    }

    private Coordinates destCoords(Coordinates coordinates,Direction direction)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();
        if(direction==Direction.Up)
            return new Coordinates(x, y-1);
        if(direction==Direction.Down)
            return new Coordinates(x, y+1);
        if(direction==Direction.Left)
            return new Coordinates(x-1, y);
        if(direction==Direction.Right)
            return new Coordinates(x+1, y);
        return coordinates;
    }

    private IZoneNode getZoneByCoords(Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();
        int xBound=zoneDescription.getxBound();
        int xBase=zoneDescription.getxBase();
        int yBase=zoneDescription.getyBase();
        int yBound=zoneDescription.getyBound();
        System.out.println("Coords("+x+","+y+") xBase="+xBase+" xBound="+xBound+" yBase="+yBase+" yBound="+yBound);
        if(x >= xBound)
            return zoneNeighbors.getRightZone();
        if(x < xBase)
            return zoneNeighbors.getLeftZone();
        if(y >= yBound)
            return zoneNeighbors.getBottomZone();
        if(y < yBase)
            return zoneNeighbors.getTopZone();
        return this;
    }

    //TODO: Add synchronization
    @Override
    public ZoneResponse movePlayer(IPlayer player, Direction direction) throws RemoteException {

        String playerId = player.getId();
        try {
            playerId = player.getId();
        } catch (RemoteException e) {
            return new ZoneResponse("Player Disconnected", false, getPlayerZoneDesc());
        }
        System.out.println("Direction = "+direction);
        Coordinates coordinates =  playersToCoordinates.get(playerId);
        if(coordinates==null)
            return new ZoneResponse("Player is not registered.", false, getPlayerZoneDesc());
        Coordinates destCoordinates=destCoords(coordinates, direction);
        IZoneNode destZone= getZoneByCoords(destCoordinates);
        if(destZone==null)
        {
            return new ZoneResponse("Coordinates out of bounds", false, getPlayerZoneDesc());
        }
        else if(destZone==this)
        {
            if(coordinatesToPlayer.putIfAbsent(destCoordinates,new SimpleEntry<IPlayer,String>(player,playerId))==null){
                playersToCoordinates.put(playerId, destCoordinates);
                coordinatesToPlayer.remove(coordinates);
                
                broadcastPlayerCoords(playerId,destCoordinates);
                sendHello(player,playerId,destCoordinates);
                
                return new ZoneResponse("Player moved to position ("+destCoordinates.getX()+","+destCoordinates.getY()+")", true, getPlayerZoneDesc());
            }
            return new ZoneResponse("Coordinates occupied by another player", false, getPlayerZoneDesc());
        }
        else{
            ZoneResponse destZoneResponse = destZone.registerPlayer(player, destCoordinates);
            if(destZoneResponse.isSuccess())
                unRegisterPlayerInternal(playerId);
            return destZoneResponse;
        }
    }

    
    
}
