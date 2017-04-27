/**
 * Created by karthik.sharma98 on 24-04-2017.
 */
public class Controller_unif {
    private float dx;
    private float dy;
    private float dz;

    public Controller_unif(float dx, float dy, float dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public Controller_unif() {
        this.dx = 0;
        this.dy = 0;
        this.dz = 0;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public float getDz() {
        return dz;
    }
}
