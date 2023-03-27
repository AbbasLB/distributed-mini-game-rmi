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

IEntryNode: 
- (XBase,YBase,XBound,YBound) registerZone(IZoneNode zoneNode)
- (IZoneNode,MESSAGE,bool) registerPlayer(IPlayer player,int X,int Y)

IZoneNode:
- void linkNeighbors(IZoneNode left,IZoneNode top,IZoneNode right,IZoneNode bottom)
//register player in zone, send update to all other players, and give the player coordinates of the other players
// check if say hello
- (MESSAGE,bool) registerPlayer(IPlayer player,int X,int Y)

//send position change to other players
- void unRegisterPlayer(IPlayer player)

//should call updateMap on all players including sender and sends hello to neighboring players(4 neighbors)
- (IZoneNode,MESSAGE,bool) movePlayer(IPlayer player,Direction direction) 

IPlayer:
- string getId()

- void updateMap(List<(playerId,int x,int y)> players,bool zoneChanged)
//notify players when another player disconnects or unregister
- void removeAnotherPlayer(string playerId)
- void sayHello(string playerId)


- handled player disconnection
- handled player crashing
