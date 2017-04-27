/**
 * Created by karthik.sharma98 on 24-04-2017.
 */
public class Controller_nonunif {

    private double x;
    private double y;
    private double z;
    private double factor;

    public Controller_nonunif(double x, double y, double z, double factor) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.factor = factor;
    }

    public double dx(double xi, double yi, double zi) {
        return (factor * (x - xi)/Math.sqrt((x-xi)*(x-xi) + (y-yi)*(y-yi) + (z-zi)*(z-zi)));
    }
    public double dy(double xi, double yi, double zi) {
        return (factor * (y - yi)/Math.sqrt((x-xi)*(x-xi) + (y-yi)*(y-yi) + (z-zi)*(z-zi)));
    }
    public double dz(double xi, double yi, double zi) {
        return (factor * (z - zi)/Math.sqrt((x-xi)*(x-xi) + (y-yi)*(y-yi) + (z-zi)*(z-zi)));
    }

}
