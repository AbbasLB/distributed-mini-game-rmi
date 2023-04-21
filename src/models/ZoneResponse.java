package models;
import java.io.Serializable;

import interfaces.IZoneNodePlayer;

public class ZoneResponse implements Serializable {
    private String message;
    private boolean success;
    private IZoneNodePlayer zoneNode;

    public ZoneResponse(String message, boolean success,IZoneNodePlayer zoneNode) {
        this.message = message;
        this.success = success;
        this.zoneNode = zoneNode;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public IZoneNodePlayer getZoneNode() {
        return zoneNode;
    }
    
}
