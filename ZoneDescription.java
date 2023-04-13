public class ZoneDescription {
    private IZoneNode zoneNode;
    private int xBase;
    private int yBase;
    private int xBound;
    private int yBound;
    private int matrixSize;
    
    public ZoneDescription(IZoneNode zoneNode, int xBase, int yBase, int xBound, int yBound, int matrixSize) {
        this.zoneNode = zoneNode;
        this.xBase = xBase;
        this.yBase = yBase;
        this.xBound = xBound;
        this.yBound = yBound;
        this.matrixSize = matrixSize;
    }
    public int getxBase() {
        return xBase;
    }
    public int getyBase() {
        return yBase;
    }
    public int getxBound() {
        return xBound;
    }
    public int getyBound() {
        return yBound;
    }
    public int getMatrixSize() {
        return matrixSize;
    }
    public IZoneNode getZoneNode() {
        return zoneNode;
    }

    public void setZoneNode(IZoneNode zoneNode) {
        this.zoneNode = zoneNode;
    }
    public void setxBase(int xBase) {
        this.xBase = xBase;
    }
    public void setyBase(int yBase) {
        this.yBase = yBase;
    }
    public void setxBound(int xBound) {
        this.xBound = xBound;
    }
    public void setyBound(int yBound) {
        this.yBound = yBound;
    }
    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
    }
}
