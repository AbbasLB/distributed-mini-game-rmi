public class ZoneResponse {
    private String message;
    private boolean success;
    private IZoneNode zone;
    private ZoneDescription zoneDescription;

    public ZoneResponse(String message, boolean success, IZoneNode zone,ZoneDescription zoneDescription) {
        this.message = message;
        this.success = success;
        this.zone = zone;
        this.zoneDescription = zoneDescription;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public IZoneNode getZone() {
        return zone;
    }
    public ZoneDescription getZoneDescription() {
        return zoneDescription;
    }
    
}
