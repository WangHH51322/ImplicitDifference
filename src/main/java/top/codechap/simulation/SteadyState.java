package top.codechap.simulation;

import lombok.Data;
import net.jamu.matrix.Matrices;
import net.jamu.matrix.MatrixD;
import top.codechap.constant.Constant;
import top.codechap.equations.FixedFunction;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.utils.Excel2Network;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-04 15:28
 * @description SteadyState
 */
public class SteadyState {

    private Integer times;
    private FixedFunction fixedFunction;
    private double[] b;
    private double[] x;
    private double[][] CoefficientMatrix;
    private double[] Qn;
    private List<double[]> Qout;
    private double[] Hn;
    private List<double[]> Hout;

    public SteadyState(FixedFunction fixedFunction) {
        this.fixedFunction = fixedFunction;
        init();
    }

    private void init() {
        fixedFunction.GenerateCoefficientMatrix();  //生成系数矩阵
        try {
            CoefficientMatrix = fixedFunction.CompleteCoefficientMatrix();  //根据默认的Qn,Hn初值,补齐系数矩阵和边界条件;生成b
        } catch (Exception e) {
            e.printStackTrace();
        }
        x = new double[fixedFunction.getNetWork().getMatrixSize()];    //给x赋初值

        Qn = new double[fixedFunction.getNetWork().getQnSize()];
        Hn = new double[fixedFunction.getNetWork().getHnSize()];
        Qout = new ArrayList<>();
        Hout = new ArrayList<>();

        for (int i = 0; i < Qn.length; i++) {
            x[i] = fixedFunction.getInitQ0();
        }
        for (int i = 0; i < Hn.length; i++) {
            x[i + fixedFunction.getQn().length] = fixedFunction.getInitH0();
        }
        b = fixedFunction.getB();
        Qout.add(fixedFunction.getQn());    //将初始值添加进List
        Hout.add(fixedFunction.getHn());    //将初始值添加进List
    }

    public void run() throws Exception {
        int calculateTimes = 0;
        while (calculateTimes < times) {
            /*根据初值进行迭代计算*/
            long startTime = System.nanoTime();
            MatrixD matA = Matrices.fromJaggedArrayD(CoefficientMatrix);
            MatrixD matB = Matrices.colVectorD(b);
            MatrixD matX = Matrices.colVectorD(x);
            matX = matA.solve(matB, matX);
            long endTime = System.nanoTime();
            System.out.println("第"+ (calculateTimes + 1) + "次直接求解计算时间: " + (endTime - startTime) + "纳秒");
            System.out.println("第"+ (calculateTimes + 1) + "次直接求解计算时间: " + (endTime - startTime) / 1000000 + "毫秒");

            /*将计算结果赋值给下一次迭代*/
//            System.out.println("matX:");
            for (int i = 0; i < x.length; i++) {
                x[i] = matX.get(i,0);
//                System.out.print(matX.get(i,0) + " ");
            }
//            System.out.println();

//            System.out.println("Qn:");
            for (int i = 0; i < Qn.length; i++) {
                Qn[i] = matX.get(i,0);
//                System.out.print(Qn[i] + " ");
            }
//            System.out.println();

//            System.out.println("Hn:");
            for (int i = 0; i < Hn.length; i++) {
                Hn[i] = matX.get(i+Qn.length,0);
//                System.out.print(Hn[i] + " ");
            }
//            System.out.println();

            double[] qOut = new double[Qn.length];
            System.arraycopy(Qn, 0, qOut, 0, Qn.length);
            Qout.add(qOut);   //将每一次迭代的结果输出,用于展示
            double[] hOut = new double[Hn.length];
            System.arraycopy(Hn, 0, hOut, 0, Hn.length);
            Hout.add(hOut);
            /*将上一次迭代的结果作为初值,用来生成新的系数矩阵*/
            fixedFunction.setQn(Qn);
            fixedFunction.setHn(Hn);
            CoefficientMatrix = fixedFunction.CompleteCoefficientMatrix();
            b = fixedFunction.getB();
            for (int i = 0; i < x.length; i++) {
                x[i] = matX.get(i,0);
            }
            calculateTimes ++;

            /*判断是否需要跳出循环*/
//            double[] QnBefore = Qout.get(Qout.size() - 2);
//            System.out.println();
//            System.out.println("Qout.length = " + Qout.size());
//            System.out.println("QnBefore:");
//            for (int i = 0; i < QnBefore.length; i++) {
//                System.out.print(QnBefore[i] + " ");
//            }
            boolean needToEnd = true;
            double[] QnBefore = Qout.get(Qout.size() - 2);
            for (int i = 0; i < Qn.length; i++) {
                if (Math.abs(Qn[i] - QnBefore[i]) > 0.00001){
                    needToEnd = false;
                }
            }
            double[] HnBefore = Hout.get(Hout.size() - 2);
            for (int i = 0; i < Hn.length; i++) {
                if (Math.abs(Hn[i] - HnBefore[i]) > 0.001){
                    needToEnd = false;
                }
            }
            if (needToEnd) {
                System.out.println("=+++++++++++++++++++=");
                System.out.println("迭代收敛,迭代了" + calculateTimes + "次" );
                break;
            }
        }
        if (calculateTimes == times) {
            System.out.println("=+++++++++++++++++++=");
            System.out.println("已经迭代" + calculateTimes + "次,迭代次数已达上限,结果仍未收敛");
        }
    }

    public double[] getB() {
        return b;
    }

    public void setB(double[] b) {
        this.b = b;
    }

    public double[][] getCoefficientMatrix() {
        return CoefficientMatrix;
    }

    public void setCoefficientMatrix(double[][] coefficientMatrix) {
        CoefficientMatrix = coefficientMatrix;
    }

    public List<double[]> getQout() {
        return Qout;
    }

    public void setQout(List<double[]> qout) {
        Qout = qout;
    }

    public List<double[]> getHout() {
        return Hout;
    }

    public void setHout(List<double[]> hout) {
        Hout = hout;
    }

    public FixedFunction getFixedFunction() {
        return fixedFunction;
    }

    public void setFixedFunction(FixedFunction fixedFunction) {
        this.fixedFunction = fixedFunction;
    }

    public void setTimes(Double steadyStateTime) {
        double times = steadyStateTime / Constant.STEADY_STATE_T;
        this.times = (int) times;
    }
}
