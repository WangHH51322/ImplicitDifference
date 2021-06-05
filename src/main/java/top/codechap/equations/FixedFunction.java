package top.codechap.equations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.constant.Constant;
import top.codechap.model.liquid.AviationKerosene;
import top.codechap.model.network.NetWork;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.Pipe;

/**
 * @author CodeChap
 * @date 2021-06-03 15:02
 * @description FixedFunction
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedFunction {

    public static void main(String[] args) {
        Pipe pipe = new LongPipe();
        pipe.setLength(22.00);
        pipe.setC(1.00);
        pipe.setE(206.9 * 1000000000);
        pipe.setOutsideDiameter(0.6);
        pipe.setThickness(0.12);

        AviationKerosene oil = new AviationKerosene();
        oil.setK(13600.00 * 100000);
        oil.setRou(875.00);

        FixedFunction fixedFunction = new FixedFunction();
        fixedFunction.CalculateCoefficient(pipe,oil);

        System.out.println("fixedFunction.getH_Motion() = " + fixedFunction.getH_Motion());
        System.out.println("fixedFunction.getQ_Momentum() = " + fixedFunction.getQ_Momentum());
        Double a = fixedFunction.CalculateA(pipe, oil);
        System.out.println("a = " + a);
    }

//    private LongPipe pipe;
    private Double H_Motion;    //管段运动方程中H的系数
    private Double H_Motion_Last;    //管段运动方程中H的系数
    private Double Q_Momentum;  //管段动量方程中Q的系数
    private Double Q_Momentum_Last;  //管段动量方程中Q的系数
    private NetWork netWork;

    public FixedFunction(NetWork netWork) {
        this.netWork = netWork;
    }

    public void CalculateCoefficient(Pipe pipe, AviationKerosene oil) {
        double a = CalculateA(pipe, oil);
        double H_Motion = Constant.G * pipe.area() * Constant.STEADY_STATE_T / Constant.SEGMENT_LENGTH;
        double Q_Momentum = a * a * Constant.STEADY_STATE_T / (Constant.G * pipe.area() * Constant.SEGMENT_LENGTH);

        this.H_Motion = H_Motion;
        this.Q_Momentum = Q_Momentum;
    }

    public Double CalculateA (Pipe pipe, AviationKerosene oil) { //计算管道内的波速
        double result;
        result = (oil.getK()/oil.getRou()) / (1 + pipe.getC() * (oil.getK()*pipe.insideDiameter()) / (pipe.getE())*pipe.getThickness());
        result = Math.pow(result,0.5);
        return result;
    }
}
