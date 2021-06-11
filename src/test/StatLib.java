package test;
import java.lang.Math;

public class StatLib {
	// simple average
	public static float avg(float[] x) {
		float sum = 0;
		for (int i=0; i < x.length; i++) {
			sum += x[i];
		}
		return sum/x.length; 
	}
	// returns the variance of x array
	public static float var(float[] x) {
		// First we will find the avg
		float local_avg = avg(x);
		float local_sum = 0;
		for (int i=0;i < x.length; i++) {
			local_sum+= Math.pow((x[i]-local_avg),2);	
		}
		return local_sum/x.length;
	}
	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y) {
		float avg_x = avg(x);
		float avg_y = avg(y);
		float cov = 0;
		for(int i=0; i<x.length;i++) {
			cov += (x[i]-avg_x)*(y[i]-avg_y);
		}
		return cov/x.length;
	}
	// returns the pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y) {
		float cov_xy = cov(x,y);
		float std_x = (float)Math.sqrt(var(x));
		float std_y = (float)Math.sqrt(var(y));
		return cov_xy/(std_x*std_y);
	}
	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points) {
		
		//init x and y
		float[] x= {};
		float[] y= {};
		for(int i=0; i<points.length;i++) {
			x = add_array(x.length,x,points[i].x);
		}
		for(int i=0; i<points.length;i++) {
			y = add_array(y.length,y,points[i].y);
		}
		
		// calc cov,var
		float cov_xy = cov(x,y);
		float var_x = var(x);
		float avg_x = avg(x);
		float avg_y = avg(y);
		
		// a,b for the line
		float a = cov_xy/var_x;
		float b = avg_y - a*avg_x;
		return new Line(a,b);
	}
	
	public static Line linear_reg_floats(float[] x,float[] y) {
		// calc cov,var
		float cov_xy = cov(x,y);
		float var_x = var(x);
		float avg_x = avg(x);
		float avg_y = avg(y);
		
		// a,b for the line
		float a = cov_xy/var_x;
		float b = avg_y - a*avg_x;
		return new Line(a,b);
	}
	
	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points) {
		Line f = linear_reg(points);
		return dev(p,f);
	}
	// returns the deviation between point p and the line
	public static float dev(Point p,Line l) {
		float l_x = l.f(p.x);
		return l_x - p.y;
	}
	
	
	
	
	//add to array
    public static float[] add_array(int n, float[] x, float f)
    {
        int i;
  
        // create a new array of size n+1
        float newarr[] = new float[n + 1];
  
        // insert the elements from
        // the old array into the new array
        // insert all elements till n
        // then insert x at n+1
        for (i = 0; i < n; i++)
            newarr[i] = x[i];
        newarr[n] = f;
        return newarr;
    }   
}
