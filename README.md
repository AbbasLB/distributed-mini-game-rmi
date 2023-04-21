# IDS_Game_Project


## we have 2 types of nodos:
- Entry Node 
- Zone Node

## Initialization:

- we create entry node with size of matrix mxm and number of nodes nxn
- create nxn zone nodes
1. Identification Phase: Each zone node talks to the entry node to get its associated zone and waits..
2. Linking Phase: Once all the zones are assigned, the entry node link each zone node to neighbors 
3. Starting Phase: Once all nodes are linked together, the entry will start accepting new players and it will 
    assign them to the corresponding zone node


IEntryNodePlayer:
- (IZoneNodePlayer,String Message,bool success) registerPlayer(IPlayer player,PlayerCoordinates coords)

IEntryNode exteds IEntryNodePlayer: 
- (XBase,YBase,XBound,YBound,matrixSize) registerZone(IZoneNode zoneNode)

IZoneNode:
    void linkNeighbors(ZoneNeighbors neighbors); ZoneNeigbors <=> class (leftNeighbor,rightNeighbor,topNeighbor,bottomNeigbor);
    void MarkAsReady();

IZoneNodePlayer:
    (IZoneNodePlayer,String Message,bool success) registerPlayer(IPlayer player,Coordinates playerCoordinates) ;
    void unRegisterPlayer(String playerId);
    (IZoneNodePlayer,String Message,bool success) movePlayer(IPlayer player,Direction direction) ;

IPlayer:
- string getId()
- void setId(string id)
- void receiveUpdate(List<(strinng playerId,int x,int y)> players,bool zoneChanged)
- void receiveMessage(String message)


- handled player disconnection
- handled player crashing
