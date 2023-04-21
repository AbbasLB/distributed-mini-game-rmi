package services;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.*;
import models.*;

public class ZoneNode implements IZoneNode,Serializable {

    private ZoneDescription<IZoneNode> zoneDescription;
    private boolean zoneReady;
    private ZoneNeighbors zoneNeighbors;

    private final Object playersLock = new Object();
    private Map<String,Coordinates> playersToCoordinates = new ConcurrentHashMap<String,Coordinates>();

    //store the IPlayer object and the id of each player at a occupied coordinates
    private Map<Coordinates,SimpleEntry<IPlayer,String>> coordinatesToPlayer = new ConcurrentHashMap<Coordinates,SimpleEntry<IPlayer,String>>();

    public ZoneNode()
    {
        zoneReady=false;
    }
    // register zone to the entry node
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

    // receive the neighboring zones from the entry node 
    @Override
    public void linkNeighbors(ZoneNeighbors neighbors) throws RemoteException {
        zoneNeighbors = neighbors;
        System.out.println("Top Neighbor = "+neighbors.getTopZone());
        System.out.println("Bottom Neighbor = "+neighbors.getBottomZone());
        System.out.println("Left Neighbor = "+neighbors.getLeftZone());
        System.out.println("Right Neighbor = "+neighbors.getRightZone());
        System.out.println("----------------");
    }

    //mark the zone as ready to start accepting players
    @Override
    public void MarkAsReady() throws RemoteException {
        zoneReady = true;
    }

    // sends hello to close players
    private void sendHelloToClosePlayers(IPlayer player,String playerId,Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();
        ArrayList<SimpleEntry<IPlayer,String>> players = new ArrayList<SimpleEntry<IPlayer,String>>();
        
        // check the left,right,top and bottom cells for players to send hello
        players.add( coordinatesToPlayer.get(new Coordinates(x+1, y)) );
        players.add( coordinatesToPlayer.get(new Coordinates(x-1, y)) );
        players.add( coordinatesToPlayer.get(new Coordinates(x, y+1)) );
        players.add( coordinatesToPlayer.get(new Coordinates(x, y-1)) );

        //try sending 
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
                player.getValue().getKey().receiveUpdate(newPlayerMap, false, null);
            } catch (RemoteException e) {
                unRegisterPlayerInternal(player.getValue().getValue());
            }
        }
    }
    private void sendAllPlayersCoords(IPlayer player) throws RemoteException
    {
        player.receiveUpdate(new HashMap<String,Coordinates>(playersToCoordinates),true, getPlayerZoneDesc());
    }

    //TODO: Handle Failures
    @Override
    public ZoneResponse registerPlayer(IPlayer player, Coordinates playerCoordinates) throws RemoteException {
        synchronized(playersLock)
        {
            if(!zoneReady)
            return new ZoneResponse("Zone not ready yet", false, zoneDescription.getZoneNode());

            String playerId = player.getId();
            try {
                playerId = player.getId();
            } catch (RemoteException e) {
                return new ZoneResponse("Player Disconnected", false, zoneDescription.getZoneNode());
            }
            
            if(playersToCoordinates.containsKey(playerId))
                return new ZoneResponse("Player already registered with that username.", false, zoneDescription.getZoneNode());
            SimpleEntry<IPlayer,String> playerInDest= coordinatesToPlayer.putIfAbsent(playerCoordinates,new SimpleEntry<IPlayer,String>(player,playerId));
            if(playerInDest==null){
                playersToCoordinates.putIfAbsent(playerId, playerCoordinates);
                
                broadcastPlayerCoords(playerId,playerCoordinates);
                sendAllPlayersCoords(player);
                sendHelloToClosePlayers(player,playerId,playerCoordinates);
                
                //TODO: check hello order
                return new ZoneResponse("Player registered to zone at position ("+playerCoordinates.getX()+","+playerCoordinates.getY()+")", true, zoneDescription.getZoneNode());
            }
            try {
                //test if player in the destination  crashed
                playerInDest.getKey().getId();
            } catch (RemoteException e) {
                unRegisterPlayerInternal(playerInDest.getValue());
                //retry registering player again
                return registerPlayer(player,playerCoordinates);
            }

            return new ZoneResponse("Coordinates occupied by another player", false, zoneDescription.getZoneNode());
        }
    }

    private void unRegisterPlayerInternal(String playerId)
    {
        synchronized(playersLock)
        {
            Coordinates coordinates=  playersToCoordinates.remove(playerId);
            if(coordinates!=null){
                coordinatesToPlayer.remove(coordinates);
                broadcastPlayerCoords(playerId, null);
            }
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

    @Override
    public ZoneResponse movePlayer(IPlayer player, Direction direction) throws RemoteException {

        synchronized(playersLock)
        {
            String playerId = player.getId();
            try {
                playerId = player.getId();
            } catch (RemoteException e) {
                return new ZoneResponse("Player Disconnected", false, zoneDescription.getZoneNode());
            }
            System.out.println("Direction = "+direction);
            Coordinates coordinates =  playersToCoordinates.get(playerId);
            if(coordinates==null)
                return new ZoneResponse("Player is not registered.", false, zoneDescription.getZoneNode());
            Coordinates destCoordinates=destCoords(coordinates, direction);
            IZoneNode destZone= getZoneByCoords(destCoordinates);

            //if destination out of the map
            if(destZone==null)
            {
                return new ZoneResponse("Coordinates out of bounds", false, zoneDescription.getZoneNode());
            }
            // if the player is still moving in the same zone
            else if(destZone==this)
            {
                
                SimpleEntry<IPlayer,String> playerInDest= coordinatesToPlayer.putIfAbsent(destCoordinates,new SimpleEntry<IPlayer,String>(player,playerId));
                
                //if no other player in the cell
                if(playerInDest==null){
                    playersToCoordinates.put(playerId, destCoordinates);
                    coordinatesToPlayer.remove(coordinates);
                    
                    broadcastPlayerCoords(playerId,destCoordinates);
                    sendHelloToClosePlayers(player,playerId,destCoordinates);
                    
                    return new ZoneResponse("", true, zoneDescription.getZoneNode());
                }

                try {
                    //test if player in the destination  crashed
                    playerInDest.getKey().getId();
                } catch (RemoteException e) {
                    unRegisterPlayerInternal(playerInDest.getValue());
                    //retry moving player again
                    return movePlayer(player,direction);
                }
                return new ZoneResponse("Coordinates occupied by another player", false, zoneDescription.getZoneNode());
            }
            // the player wants to move to another zone
            else{
                ZoneResponse destZoneResponse = destZone.registerPlayer(player, destCoordinates);
                if(destZoneResponse.isSuccess())
                    unRegisterPlayerInternal(playerId);
                return destZoneResponse;
            }
        }
        
    }

    
    
}
