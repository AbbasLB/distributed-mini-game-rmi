package services;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import interfaces.*;
import models.*;

public class EntryNode implements IEntryNode,Serializable {

    private boolean zonesReady;
    private int matrixSize;
    private int splitSize;
    private ZoneDescription<IZoneNode>[][] zones;
    private int nodesRegistered;
    private int internalPlayerID = 1;
    private final Object internalPlayerIDLock = new Object();

    public EntryNode(int matrixSize,int splitSize)
    {
        this.matrixSize=matrixSize;
        this.splitSize=splitSize;
        nodesRegistered=0;
        zonesReady=false;
        initZonesMatrix();
    }
    private void initZonesMatrix()
    {
        if(splitSize>matrixSize)
            throw new UnsupportedOperationException("Matrix size should be bigger than split size.");
        zones=new ZoneDescription[splitSize][splitSize];
        int xBase,yBase=0,singleNodeDim=matrixSize/splitSize;
        for(int i=0;i<splitSize;i++){
            xBase=0;
            for(int j=0;j<splitSize;j++)
            {
                zones[i][j]=new ZoneDescription<IZoneNode>(null, xBase,yBase, xBase+singleNodeDim, yBase+singleNodeDim, matrixSize);
                xBase += singleNodeDim;
                if(j==splitSize-1)
                    zones[i][j].setxBound(matrixSize);
                if(i==splitSize-1)
                    zones[i][j].setyBound(matrixSize);
            }
            yBase += singleNodeDim;
        }
    }
    
    public void LinkNeighbors()
    {
        for(int i=0;i<splitSize;i++){
            for(int j=0;j<splitSize;j++){
                IZoneNode zone=zones[i][j].getZoneNode();
                IZoneNode leftZone=null;
                IZoneNode rightZone=null;
                IZoneNode topZone=null;
                IZoneNode bottomZone=null;
                if(j+1<splitSize)
                    rightZone=zones[i][j+1].getZoneNode();
                if(i+1<splitSize)
                    bottomZone=zones[i+1][j].getZoneNode();
                if(i-1>=0)
                    topZone=zones[i-1][j].getZoneNode();
                if(j-1>=0)
                    leftZone=zones[i][j-1].getZoneNode();
                try{
                    zone.linkNeighbors(new ZoneNeighbors(leftZone, rightZone, topZone, bottomZone));
                }
                catch(Exception e)
                {
                    System.err.println("Error linking neighbors :" + e) ;
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        for(int i=0;i<splitSize;i++)
            for(int j=0;j<splitSize;j++){
                try{
                    zones[i][j].getZoneNode().MarkAsReady();
                }
                catch(Exception e)
                {
                    System.err.println("Error Marking zones as ready :" + e) ;
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        zonesReady=true;
    }
    @Override
    public ZoneDescription<IZoneNode> registerZone(IZoneNode zoneNode) throws RemoteException {
        //only one zone can register at a time
        synchronized(this)
        {
            for(int i=0;i<splitSize;i++){
                for(int j=0;j<splitSize;j++){
                    ZoneDescription<IZoneNode> zoneDesc=zones[i][j];
                    if(zoneDesc.getZoneNode()==null){
                        zoneDesc.setZoneNode(zoneNode);
                        nodesRegistered++;
                        //maybe we should move it to another function after return
                        if(nodesRegistered==splitSize*splitSize)
                            LinkNeighbors();
                        System.out.println("New Zone Registered");
                        return zoneDesc;
                    }
                }
            }
            System.out.println("Max Zones Registered, couldn't register new zone");
            return null;
        }
    }
        

    private ZoneDescription<IZoneNode> getZoneDescByCoord(Coordinates playerCoordinates)
    {
        for(int i=0;i<splitSize;i++){
            for(int j=0;j<splitSize;j++){
                ZoneDescription<IZoneNode> zoneDesc=zones[i][j];
                if(playerCoordinates.getX() >= zoneDesc.getxBase() && playerCoordinates.getX() < zoneDesc.getxBound()
                && playerCoordinates.getY() >= zoneDesc.getyBase() && playerCoordinates.getY() < zoneDesc.getyBound())
                    return zoneDesc;   
            }
        }
        return null;
    }

    @Override
    public ZoneResponse registerPlayer(IPlayer player, Coordinates playerCoordinates) throws RemoteException {
        if(!zonesReady)
            return new ZoneResponse("Game not ready.",false, null);
        
        ZoneDescription<IZoneNode> zoneDesc=getZoneDescByCoord(playerCoordinates);
        if(zoneDesc==null)
            return new ZoneResponse("Coordinates out of bounds.",false, null);
        
        player.setId(player.getId()+"#"+internalPlayerID);
        synchronized(internalPlayerIDLock)
        {
            internalPlayerID++;
        }
        return zoneDesc.getZoneNode().registerPlayer(player, playerCoordinates);
    }
    
}
