public class ZoneNeighbors {

    private IZoneNode leftZone;
    private IZoneNode rightZone;
    private IZoneNode topZone;
    private IZoneNode bottomZone;
    public ZoneNeighbors(IZoneNode leftZone, IZoneNode rightZone, IZoneNode topZone, IZoneNode bottomZone) {
        this.leftZone = leftZone;
        this.rightZone = rightZone;
        this.topZone = topZone;
        this.bottomZone = bottomZone;
    }

    public IZoneNode getLeftZone() {
        return leftZone;
    }
    public void setLeftZone(IZoneNode leftZone) {
        this.leftZone = leftZone;
    }
    public IZoneNode getRightZone() {
        return rightZone;
    }
    public void setRightZone(IZoneNode rightZone) {
        this.rightZone = rightZone;
    }
    public IZoneNode getTopZone() {
        return topZone;
    }
    public void setTopZone(IZoneNode topZone) {
        this.topZone = topZone;
    }
    public IZoneNode getBottomZone() {
        return bottomZone;
    }
    public void setBottomZone(IZoneNode bottomZone) {
        this.bottomZone = bottomZone;
    }
    
    
}
