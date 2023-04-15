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
 
    private String getIdFromCoords(Coordinates coordinates)
    {
        for (HashMap.Entry<String,Coordinates> entry : otherZonePlayers.entrySet()) {
            if (entry.getValue().equals(coordinates)) {
                return entry.getKey();
            }
        }
        return null;
    }
    

    private void DrawMap(ZoneDescription zoneDescription)
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush();

        int matrixSize = zoneDescription.getMatrixSize();
        int xBound=zoneDescription.getxBound();
        int xBase=zoneDescription.getxBase();
        int yBase=zoneDescription.getyBase();
        int yBound=zoneDescription.getyBound();

        for(int i=0;i<matrixSize;i++){
            for(int j=0;j<matrixSize;j++)
            {
                if(i>=yBase && i<yBound && j>=xBase && j<xBound)
                {
                    String playerId= getIdFromCoords(new Coordinates(j, i));
                    if(playerId==null)
                    {
                        System.out.print(TerminalColors.ANSI_CYAN_BACKGROUND+"   "+TerminalColors.ANSI_RESET);
                    }
                    else if(playerId.equals(id))
                    {
                        System.out.print(TerminalColors.ANSI_GREEN_BACKGROUND+" "+id.charAt(0)+" "+TerminalColors.ANSI_RESET);
                    }else {
                        System.out.print(TerminalColors.ANSI_RED_BACKGROUND+" "+playerId.charAt(0)+" "+TerminalColors.ANSI_RESET);
                    }
                }else{
                    System.out.print(TerminalColors.ANSI_BLACK_BACKGROUND +"   "+TerminalColors.ANSI_RESET);
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
