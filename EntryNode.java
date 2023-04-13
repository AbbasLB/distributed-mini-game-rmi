import java.io.Serializable;
import java.rmi.RemoteException;

public class EntryNode implements IEntryNode,Serializable {

    private boolean zonesReady;
    private int matrixSize;
    private int splitSize;
    private ZoneDescription[][] zones;
    private int nodesRegistered;

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
                zones[i][j]=new ZoneDescription(null, xBase,yBase, xBase+singleNodeDim, yBase+singleNodeDim, matrixSize);
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
                ZoneDescription zoneDesc=zones[i][j];
                ZoneDescription leftZoneDesc=null;
                ZoneDescription rightZoneDesc=null;
                ZoneDescription topZoneDesc=null;
                ZoneDescription bottomZoneDesc=null;
                if(j+1<splitSize)
                    rightZoneDesc=zones[i][j+1];
                if(i+1<splitSize)
                    bottomZoneDesc=zones[i+1][j];
                if(i-1>=0)
                    topZoneDesc=zones[i-1][j];
                if(j-1>=0)
                    leftZoneDesc=zones[i][j-1];
                try{
                    zoneDesc.getZoneNode().linkNeighbors(new ZoneNeighbors(leftZoneDesc.getZoneNode(), rightZoneDesc.getZoneNode(), topZoneDesc.getZoneNode(), bottomZoneDesc.getZoneNode()));
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
                

    }
    @Override
    public ZoneDescription registerZone(IZoneNode zoneNode) throws RemoteException {

        for(int i=0;i<splitSize;i++){
            for(int j=0;j<splitSize;j++){
                ZoneDescription zoneDesc=zones[i][j];
                if(zoneDesc.getZoneNode()==null){
                    zoneDesc.setZoneNode(zoneNode);
                    nodesRegistered++;
                    //maybe we should move it to another function after return
                    if(nodesRegistered==splitSize*splitSize)
                        LinkNeighbors();
                    return zoneDesc;
                }
            }
        }
        return null;
    }

    private ZoneDescription getZoneDescByCoord(Coordinates playerCoordinates)
    {
        for(int i=0;i<splitSize;i++){
            for(int j=0;j<splitSize;j++){
                ZoneDescription zoneDesc=zones[i][j];
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
        ZoneDescription zoneDesc=getZoneDescByCoord(playerCoordinates);
        return zoneDesc.getZoneNode().registerPlayer(player, playerCoordinates);
    }
    
}
