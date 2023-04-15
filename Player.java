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

    //TODO: Delete this
    private void DrawMapOld(ZoneDescription zoneDescription)
    {
        System.out.println("Map:");
        System.out.println("zoneDescription:"+zoneDescription.getMatrixSize()+" "+zoneDescription.getxBase()+" "+zoneDescription.getyBase());
        for (HashMap.Entry<String, Coordinates> player : otherZonePlayers.entrySet())  {
            System.out.println(player.getKey()+" ("+player.getValue().getX()+","+player.getValue().getY()+")");
        }
    }
 
    private String getIdFromCoords(Coordinates coordinates)
    {
        for (HashMap.Entry<String,Coordinates> entry : otherZonePlayers.entrySet()) {
            if (entry.getValue().equals(coordinates)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    private void DrawMap(ZoneDescription zoneDescription)
    {
        int matrixSize = zoneDescription.getMatrixSize();
        int xBound=zoneDescription.getxBound();
        int xBase=zoneDescription.getxBase();
        int yBase=zoneDescription.getyBase();
        int yBound=zoneDescription.getyBound();

        for(int i=0;i<matrixSize;i++){
            for(int j=0;j<matrixSize;j++)
            {
                if(i>=xBase && i<xBound && j>=yBase && j<yBound)
                {
                    String playerId= getIdFromCoords(new Coordinates(i, j));
                    if(playerId==null)
                    {
                        System.out.print(ANSI_CYAN_BACKGROUND+"  "+ANSI_RESET);
                    }
                    else if(playerId.equals(id))
                    {
                        System.out.print(ANSI_GREEN_BACKGROUND+"  "+ANSI_RESET);
                    }else {
                        System.out.print(ANSI_RED_BACKGROUND+"  "+ANSI_RESET);
                    }
                }else{
                    System.out.print(ANSI_WHITE_BACKGROUND +"  "+ANSI_RESET);
                }
            }
            System.out.println();
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
