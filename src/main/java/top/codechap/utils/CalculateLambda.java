package top.codechap.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/10/6 15:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateLambda implements Serializable {

    private double lambda;
    private double m;
    private double beta;

    public double calculate(double flow, double diameter, double viscosity, double roughness) {
        double epsilon = 2 * roughness / diameter;//相对粗糙度
        double reynoldsNum = 4 * flow / (Math.PI * diameter * viscosity);
        double reynoldsNum1 = 59.5 / (Math.pow(epsilon, 8.0 / 7));
        double reynoldsNum2 = (665 - 765 * Math.log10(epsilon)) / epsilon;
        double aRe;
        if (reynoldsNum < 2000) {
            beta=4.15;
            m=1;
        } else if (reynoldsNum < reynoldsNum1) {
            beta=0.0246;
            m=0.25;
        } else if (reynoldsNum < reynoldsNum2) {
            aRe = Math.pow(10, 0.127 * Math.log10(epsilon / 2) - 0.627);
            beta=0.0802 * aRe;
            m=0.123;
        } else {
            beta=0.0826 * 0.11 * Math.pow(epsilon / 2, 0.25);
            m=0;
        }
        //Colebrook公式
        if (flow <1e-3) {
            lambda = 0.01;
//            System.out.println("lambda = " + lambda);
            return getBeta() * Math.pow(viscosity, getM()) / Math.pow(diameter, 5 - getM());
        } else {
            double lambda = 0.01;
            double newLambda = -1;
            while (true) {
                newLambda = 1 / Math.pow(2 * Math.log10(2.51 / (reynoldsNum * Math.sqrt(lambda)) + 0.269 * roughness / diameter), 2);
                if (Math.abs(newLambda - lambda) < 1e-6) {
                    break;
                }
                lambda = newLambda;
            }
            this.lambda = lambda;
//            System.out.println("lambda = " + lambda);
            double velocity = 4 * flow / (Math.PI * diameter * diameter);
            return newLambda * velocity * Math.abs(velocity) / (2 * 9.8 * diameter * flow * Math.pow(Math.abs(flow), 1 - getM()));
        }
    }
}
