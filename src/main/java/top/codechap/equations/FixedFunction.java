package top.codechap.equations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.constant.Constant;
import top.codechap.model.liquid.AviationKerosene;
import top.codechap.model.network.NetWork;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.Pipe;

import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-03 15:02
 * @description FixedFunction
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedFunction {

    private Double H_Motion;    //管段运动方程中H的系数
    private Double H_Motion_Last;    //管段运动方程中H的系数
    private Double Q_Momentum;  //管段动量方程中Q的系数
    private Double Q_Momentum_Last;  //管段动量方程中Q的系数
    private NetWork netWork;

    private static final Double H_MOMENTUM = 1.00;  //管段动量方程中H的系数

    public FixedFunction(NetWork netWork) {
        this.netWork = netWork;
    }

    public double[][] GenerateCoefficientMatrix(AviationKerosene oil) {
        Integer matrixSize = netWork.getMatrixSize();
        List<LongPipe> longPipes = netWork.getLongPipes();

        double[][] CoefficientMatrix = new double[matrixSize][matrixSize];  //初始化矩阵
        if (longPipes.size() != 0) {
            for (int i = 0; i < longPipes.size(); i++) {
                LongPipe longPipe = longPipes.get(i);
                CalculateCoefficient(longPipe,oil); //计算系数
                Integer[] momentumNumb = longPipe.getMomentumNumb();    //长管段中动量方程行编号
                Integer[] motionNumb = longPipe.getMotionNumb();    //长管段中运动方程行编号
                Integer[] qNumb = longPipe.getQNumb();  //长管段中Q所在的列编号
                Integer[] hNumb = longPipe.getHNumb();  //长管段中H所在的列编号
                for (int j = 0; j < momentumNumb.length; j++) {    //给动量方程中的Q和H的系数赋值
                    if (j == momentumNumb.length - 1) {
                        CoefficientMatrix[momentumNumb[j]][qNumb[j]] = - Q_Momentum_Last;
                        CoefficientMatrix[momentumNumb[j]][qNumb[j+1]] = Q_Momentum_Last;
                    } else {
                        CoefficientMatrix[momentumNumb[j]][qNumb[j]] = - Q_Momentum;
                        CoefficientMatrix[momentumNumb[j]][qNumb[j+1]] = Q_Momentum;
                    }
                    CoefficientMatrix[momentumNumb[j]][hNumb[j]] = H_MOMENTUM;
                }
                for (int j = 0; j < motionNumb.length; j++) {   //给运动方程中的H的系数赋值
                    if (j == momentumNumb.length - 1) {
                        CoefficientMatrix[motionNumb[j]][hNumb[j]] = - H_Motion_Last;
                        CoefficientMatrix[motionNumb[j]][hNumb[j+1]] = H_Motion_Last;
                    } else {
                        CoefficientMatrix[motionNumb[j]][hNumb[j]] = - H_Motion;
                        CoefficientMatrix[motionNumb[j]][hNumb[j+1]] = H_Motion;
                    }
                }
            }
        }
        return CoefficientMatrix;
    }

    public void CompleteCoefficientMatrix(double[][] CoefficientMatrix,double[] Qn,double[] Hn) {
        List<LongPipe> longPipes = netWork.getLongPipes();
        for (LongPipe longPipe : longPipes) {

        }
    }

    private void CalculateCoefficient(Pipe pipe, AviationKerosene oil) {
        double a = CalculateA(pipe, oil);
        double H_Motion = Constant.G * pipe.area() * Constant.STEADY_STATE_T / Constant.SEGMENT_LENGTH;
        double H_Motion_Last = Constant.G * pipe.area() * Constant.STEADY_STATE_T / (pipe.lastSegLength() / 2 + Constant.SEGMENT_LENGTH / 2);
        double Q_Momentum = a * a * Constant.STEADY_STATE_T / (Constant.G * pipe.area() * Constant.SEGMENT_LENGTH);
        double Q_Momentum_Last = a * a * Constant.STEADY_STATE_T / (Constant.G * pipe.area() * (pipe.lastSegLength() / 2 + Constant.SEGMENT_LENGTH / 2)) ;

        this.H_Motion = H_Motion;
        this.H_Motion_Last = H_Motion_Last;
        this.Q_Momentum = Q_Momentum;
        this.Q_Momentum_Last = Q_Momentum_Last;
    }

    private Double CalculateA (Pipe pipe, AviationKerosene oil) { //计算管道内的波速
        double result;
        result = (oil.getK()/oil.getRou()) / (1 + pipe.getC() * (oil.getK()*pipe.insideDiameter()) / (pipe.getE())*pipe.getThickness());
        result = Math.pow(result,0.5);
        return result;
    }
}
