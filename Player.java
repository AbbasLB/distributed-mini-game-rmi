import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Player implements IPlayer, Serializable {

    private HashMap<String,Coordinates> otherZonePlayers = new HashMap<String,Coordinates>();
    private String id;

    public Player(String id)
    {
        this.id=id;
    }


    private void DrawMap(ZoneDescription zoneDescription)
    {
        System.out.println("Map:");
        System.out.println("zoneDescription:"+zoneDescription.getMatrixSize()+" "+zoneDescription.getxBase()+" "+zoneDescription.getyBase());
        for (HashMap.Entry<String, Coordinates> player : otherZonePlayers.entrySet())  {
            System.out.println(player.getKey()+" ("+player.getValue().getX()+","+player.getValue().getY()+")");
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void updateMap(HashMap<String,Coordinates>  players, boolean zoneChanged,ZoneDescription zoneDescription) {
        if(zoneChanged)
            otherZonePlayers.clear();
        for (HashMap.Entry<String, Coordinates> player : players.entrySet()) {
            //if coords are null, we remove player
            if(player.getValue()==null)
            {
                if(otherZonePlayers.containsKey(player.getKey()))
                    otherZonePlayers.remove(player.getKey());
            }
            else{
                otherZonePlayers.put(player.getKey(), player.getValue());
            }
        }
        DrawMap(zoneDescription);
    }

   
            

    @Override
    public void receiveMessage(String message) {
        System.out.println(message);
        //id + ": Hello " + playerId ;
    }
    
}
