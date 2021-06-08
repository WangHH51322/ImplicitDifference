package top.codechap.utils;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/10/12 9:39
 */
public class CalculateCv {

    public static void main(String[] args) {

        double[] cv = new double[]{6000,4500,0};
        double[] time = new double[]{2,2.5,3.5};
        int times = 10;
        double dt = 0.5;
        double[] doubles = calculateCv(cv, time, times, dt);
        for (double aDouble : doubles) {
            System.out.println(aDouble);
        }

    }

    /**
     *
     * @param cv 随时间变化的cv值(由用户输入)
     * @param time 阀开始变化的时间(由用户输入) __ 在此处,cv值和变化时间应该是一一对应的,即"某一时刻,有某一个具体的cv值"
     *             然后在根据所值拟合其他时刻的cv值
     * @param times 总的模拟时步数量,返回计算的cv值是每一个times的cv值
     * @param dt  时间步长
     * @return
     */
    public static double[] calculateCv(double[] cv,double[] time,int times,double dt){

        //最终需要求得的是每一个时步的cv值
        double[] Cv = new double[times];
        /*首先判断拟合曲线的类型
        * 如果只给两个cv值,就进行线性拟合,
        * 超过两个值就拟合三阶函数
        * 只有一个值,很明显阀门常开*/
        int n = 0;
        if (cv.length == 1){
            for (int i = 0; i < Cv.length; i++) {
                Cv[i] = cv[0];
            }
            return Cv;
        }else if (cv.length == 2){
            n = 1;
            double[] coefficient = coefficient(n, cv, time);
            for (int i = 0; i < (int)(time[0]/dt); i++) {
                Cv[i] = cv[0];
            }
            for (int i = (int)(time[0]/dt); i < (int)(time[time.length-1]/dt); i++) {
                double t = (i+1)*dt;
                Cv[i] = coefficient[0] + coefficient[1]*t;
            }
            for (int i = (int)(time[time.length-1]/dt); i < times; i++) {
                Cv[i] = cv[cv.length - 1];
            }
        }else if (cv.length > 2){
            n = 3;
            double[] coefficient = coefficient(n, cv, time);
            for (int i = 0; i < (int)(time[0]/dt); i++) {
                Cv[i] = cv[0];
            }
            for (int i = (int)(time[0]/dt); i < (int)(time[time.length-1]/dt); i++) {
                double t = (i+1)*dt;
                Cv[i] = coefficient[0] + coefficient[1]*t + coefficient[2]*t*t + coefficient[3]*t*t*t;
            }
            for (int i = (int)(time[time.length-1]/dt); i < times; i++) {
                Cv[i] = cv[cv.length - 1];
            }
        }
        return Cv;
    }

    /**
     *
     * @param n 是拟合曲线的阶数,如果是线型拟合n=1,否则n=3
     * @param Cv Cv随时步变化,Cv相当于y,
     * @param times times相当于x,
     * @return
     */
    public static double[] coefficient(int n , double[] Cv , double[] times){

        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < Cv.length; i++) {
            obs.add(times[i], Cv[i]);
        }

        // Instantiate a n-degree polynomial fitter.
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(n);
        // Retrieve fitted parameters (coefficients of the polynomial function).
        final double[] coeff = fitter.fit(obs.toList());

        return coeff;
    }
}
