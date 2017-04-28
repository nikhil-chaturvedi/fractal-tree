/**
 * Created by karthik.sharma98 on 24-04-2017.
 */
public class Branch {
    private float baseX;
    private float baseY;
    private float baseZ;

    private float tipX;
    private float tipY;
    private float tipZ;

    private float startradius;
    private float endradius;
    private Branch motherBranch;


    public Branch(float baseX, float baseY, float baseZ, float tipX, float tipY, float tipZ, float startradius, float endradius) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseZ = baseZ;
        this.tipX = tipX;
        this.tipY = tipY;
        this.tipZ = tipZ;
        this.startradius = startradius;
        this.endradius = endradius;
    }

    public void setMotherBranch(Branch motherBranch) {
        this.motherBranch = motherBranch;
    }

    public Branch getMotherBranch() {
        return motherBranch;
    }

    public float getBaseX() {
        return baseX;
    }

    public float getBaseY() {
        return baseY;
    }

    public float getBaseZ() {
        return baseZ;
    }

    public float getTipX() {
        return tipX;
    }

    public float getTipY() {
        return tipY;
    }

    public float getTipZ() {
        return tipZ;
    }

    public float getStartradius() {
        return startradius;
    }

    public float getEndradius() {
        return endradius;
    }

    public float getLength() {
        return (float) (Math.sqrt( (tipX-baseX)*(tipX-baseX) + (tipY-baseY)*(tipY-baseY) + (tipZ -baseZ)*(tipZ-baseZ)));
    }

    public static float getTipX(float angle, Branch prevBranch, float contractionRatio) {
        float u = prevBranch.getTipX() - prevBranch.getBaseX();
        float v = prevBranch.getTipY() - prevBranch.getBaseY();
        float w = prevBranch.getTipZ() - prevBranch.getBaseZ();
        float s = 1/((float) Math.sqrt(u*u + v*v));
        float t = (float) Math.sqrt(u*u + v*v + w*w);
        if (u ==0 && v ==0) {
            return (float) (prevBranch.getTipX() + contractionRatio * (u * Math.cos(angle * Math.PI / 180.0)));
        }
        return (float) (prevBranch.getTipX() + contractionRatio * (u * Math.cos(angle * Math.PI / 180.0) - s * t * v * Math.sin(angle * Math.PI / 180.0)));
    }

    public static float getTipY(float angle, Branch prevBranch, float contractionRatio) {
        float u = prevBranch.getTipX() - prevBranch.getBaseX();
        float v = prevBranch.getTipY() - prevBranch.getBaseY();
        float w = prevBranch.getTipZ() - prevBranch.getBaseZ();
        float s = 1/((float)Math.sqrt(u*u + v*v));
        float t = (float) Math.sqrt(u*u + v*v + w*w);
        if(u==0 && v ==0) {
            return (float) (prevBranch.getTipY() + contractionRatio*(u*Math.cos(angle * Math.PI / 180.0)));
        }
        return (float) (prevBranch.getTipY() + contractionRatio*(v*Math.cos(angle * Math.PI / 180.0) + s*t*u*Math.sin(angle * Math.PI / 180.0)));
    }

    public static float getTipZ(float angle, Branch prevBranch, float contractionRatio) {
        float w = prevBranch.getTipZ() - prevBranch.getBaseZ();
        return (float) (prevBranch.getTipZ() + contractionRatio*w*Math.cos(angle * Math.PI / 180.0));
    }

    public static float getMidTipZ(Branch prevBranch, float contractionRatio) {
        float w = prevBranch.getTipZ() - prevBranch. getBaseZ();
        return  (float) (prevBranch.getTipZ() + contractionRatio*(prevBranch.getTipZ()-prevBranch.getBaseZ()));
    }


    public static float getMidTipX(Branch prevBranch, float contractionRatio) {
        float w = prevBranch.getTipX() - prevBranch. getBaseX();
        return  (float) (prevBranch.getTipX() + contractionRatio*(prevBranch.getTipX()-prevBranch.getBaseX()));
    }

    public static float getMidTipY(Branch prevBranch, float contractionRatio) {
        float w = prevBranch.getTipY() - prevBranch. getBaseY();
        return  (float) (prevBranch.getTipY() + contractionRatio*(prevBranch.getTipY()-prevBranch.getBaseY()));
    }
}
