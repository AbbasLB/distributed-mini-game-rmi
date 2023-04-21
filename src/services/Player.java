package services;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import interfaces.IPlayer;
import interfaces.IZoneNodePlayer;
import models.Coordinates;
import models.TerminalColors;
import models.ZoneDescription;

public class Player implements IPlayer, Serializable {

    private final Object uiDrawLock = new Object();
    private final Object messagesLock = new Object();
    private final Object playersLock = new Object();

    private static final int MESSAGES_TO_SHOW = 3;
    private int messagesCount =0;
    private LinkedList<String> messages=new LinkedList<>();

    private HashMap<String,Coordinates> otherZonePlayers = new HashMap<String,Coordinates>();

    ZoneDescription<IZoneNodePlayer> zoneDescription;
    private String id;

    public Player(String id)
    {
        this.id=id;
    }
 
    private String getIdFromCoords(Coordinates coordinates)
    {
        //find player id from coord O(n^2) can be optimized
        for (HashMap.Entry<String,Coordinates> entry : otherZonePlayers.entrySet()) {
            if (entry.getValue().equals(coordinates)) {
                return entry.getKey();
            }
        }
        return null;
    }
    

    private void DrawUI()
    {
        synchronized(uiDrawLock)
        {
            if(zoneDescription==null)
                return;
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
            synchronized(messagesLock)
            {
                for (String message : messages) 
                    System.out.println(message);
            }
            System.out.print("Move Direction(L,R,U,D) (Q Quit):");
        }
      
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void receiveUpdate(HashMap<String,Coordinates>  players, boolean zoneChanged,ZoneDescription<IZoneNodePlayer> zoneDescription) {
        synchronized(playersLock)
        {
            this.zoneDescription=zoneDescription;
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
        }
        DrawUI();
        
    }

   
            

    @Override
    public void receiveMessage(String message) {
        synchronized(messagesLock)
        {
            if(messages.size()==MESSAGES_TO_SHOW)
                messages.removeFirst();
            messagesCount++;
            messages.addLast("["+messagesCount+"]: "+message);
        }
        
        DrawUI();
        //System.out.println(message);
        //id + ": Hello " + playerId ;
    }
    
}
