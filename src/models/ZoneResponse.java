package models;
import java.io.Serializable;

import interfaces.IZoneNodePlayer;

public class ZoneResponse implements Serializable {
    private String message;
    private boolean success;
    private ZoneDescription<IZoneNodePlayer> zoneDescription;

    public ZoneResponse(String message, boolean success,ZoneDescription<IZoneNodePlayer> zoneDescription) {
        this.message = message;
        this.success = success;
        this.zoneDescription = zoneDescription;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public ZoneDescription<IZoneNodePlayer> getZoneDescription() {
        return zoneDescription;
    }
    
}
