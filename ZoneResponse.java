import java.io.Serializable;

public class ZoneResponse implements Serializable {
    private String message;
    private boolean success;
    private ZoneDescription zoneDescription;

    public ZoneResponse(String message, boolean success,ZoneDescription zoneDescription) {
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

    public ZoneDescription getZoneDescription() {
        return zoneDescription;
    }
    
}
