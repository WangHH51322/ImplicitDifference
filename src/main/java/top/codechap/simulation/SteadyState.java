package top.codechap.simulation;

import lombok.Data;
import net.jamu.matrix.Matrices;
import net.jamu.matrix.MatrixD;
import top.codechap.Element;
import top.codechap.constant.Constant;
import top.codechap.equations.FixedFunction;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.model.valve.RegulatingValve;
import top.codechap.utils.Excel2Network;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-04 15:28
 * @description SteadyState
 */
public class SteadyState {

    private Integer times;  //迭代次数=总时长/时步
    private FixedFunction fixedFunction;    //系数矩阵
    private double[] b; //右端向量
    private double[] x; //未知向量
    private double[][] CoefficientMatrix;   //系数矩阵默认系数
    private double[] Qn;    //全线流量
    private List<double[]> Qout;    //全线输出流量
    private List<double[]> ElementQout; //全部原件内流量,以出口流量为准
    private double[] Hn;    //全线压力
    private List<double[]> Hout;    //全线输出压力
    private List<double[]> NodeHout;    //连接点随时间变化的压力
    private NetWork netWork;    //管网结构

    public SteadyState(FixedFunction fixedFunction) {
        this.fixedFunction = fixedFunction;
        init();
    }

    private void init() {
        netWork = fixedFunction.getNetWork();
        fixedFunction.GenerateCoefficientMatrix();  //生成系数矩阵
        try {
            CoefficientMatrix = fixedFunction.CompleteCoefficientMatrix();  //根据默认的Qn,Hn初值,补齐系数矩阵和边界条件;生成b
        } catch (Exception e) {
            e.printStackTrace();
        }
        x = new double[netWork.getMatrixSize()];    //给x赋初值
        Qn = new double[netWork.getQnSize()];
        Hn = new double[netWork.getHnSize()];
        Qout = new ArrayList<>();
        Hout = new ArrayList<>();
        NodeHout = new ArrayList<>();

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
            for (int i = 0; i < x.length; i++) {
                x[i] = matX.get(i,0);
            }
            for (int i = 0; i < Qn.length; i++) {
                Qn[i] = matX.get(i,0);
            }
            for (int i = 0; i < Hn.length; i++) {
                Hn[i] = matX.get(i+Qn.length,0);
            }

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

        /*将最后一个次时步计算的结果整理,对应到元件和节点中,*/
        List<LongPipe> longPipes = netWork.getLongPipes();
        List<ShortPipe> shortPipes = netWork.getShortPipes();
        List<RegulatingValve> regValves = netWork.getRegValves();
        List<Node> nodes = netWork.getNodes();
        if (longPipes.size() != 0) {
            for (LongPipe longPipe : longPipes) {
                SeparateQnHnIntoNodes(longPipe, nodes);
            }
        }
        if (shortPipes.size() != 0) {
            for (ShortPipe shortPipe : shortPipes) {
                SeparateQnHnIntoNodes(shortPipe, nodes);
            }
        }
        if (regValves.size() != 0) {
            for (RegulatingValve regValve : regValves) {
                SeparateQnHnIntoNodes(regValve, nodes);
            }
        }

        /*根据Hout,抽取出NodeHout*/
        for (int i = 0; i < Hout.size(); i++) {
            double[] hOut = Hout.get(i);
            double[] NodeH = new double[nodes.size()];
            for (int j = 0; j < nodes.size(); j++) {
                Node node = nodes.get(j);
                Integer connectionType = node.nodeConnectionType();
                double inH = 0.0;
                Element element = null;
                switch (connectionType) {
                    case 10 :   //与一个元件相连,作为元件入口;
                    case 200 :  //200 与两个元件相连,作为两个元件的入口;
                    case 210 :  //与两个元件相连,作为一入一出;
                    case 3000 : //3000 与三个元件相连,作为三个元件的入口;
                    case 3100 : //3100 与三个元件相连,作为二入一出;
                    case 3110 : //3110 与三个元件相连,作为一入二出;
                        element = node.getOutElements().get(0);
                        inH = element.getFirstHn(hOut) * fixedFunction.getOil().getRou() * Constant.G;
                        break;
                    case 11 :   //与一个元件相连,作为元件出口;
                    case 211 :  //与两个元件相连,作为两个元件的出口;
                    case 3111 :  //与三个元件相连,作为三个元件的出口;
                        element = node.getInElements().get(0);
                        inH = element.getLastHn(Hn)  * fixedFunction.getOil().getRou() * Constant.G;
                        break;
                }
                NodeH[j] = inH;
            }
            NodeHout.add(NodeH);
        }

        /*结果不收敛*/
        if (calculateTimes == times) {
            System.out.println("=+++++++++++++++++++=");
            System.out.println("已经迭代" + calculateTimes + "次,迭代次数已达上限,结果仍未收敛");
        }
    }

    /*将最后一个次时步计算的结果整理,对应到元件和节点中,*/
    private void SeparateQnHnIntoNodes(Element element, List<Node> nodes) {
        Integer startNumb = element.getStartNumb();
        Node startNode = null;
        for (Node node : nodes) {
            int numb = node.getNumb();
            if (numb == startNumb) {
                startNode = node;
                break;
            }
        }
        Integer startNodeType = startNode.getType();
        if (startNodeType == 0) { //入口节点
            startNode.setFlow(element.getFirstQn(Qn));
        }
        if (startNodeType == 1 || startNodeType == 2) {   //出口节点
            startNode.setPressure(element.getFirstHn(Hn) * fixedFunction.getOil().getRou() * Constant.G);
        }

        Integer endNumb = element.getEndNumb();
        Node endNode = null;
        for (Node node : nodes) {
            int numb = node.getNumb();
            if (numb == endNumb) {
                endNode = node;
                break;
            }
        }
        Integer endNodeType = endNode.getType();
        if (endNodeType == 0) { //入口节点
            endNode.setFlow(element.getLastQn(Qn));
        }
        if (endNodeType == 1 || endNodeType == 2) {   //出口节点,中间节点
            endNode.setPressure(element.getLastHn(Hn)  * fixedFunction.getOil().getRou() * Constant.G);
        }
    }

    public List<double[]> getElementQout() {
        return ElementQout;
    }

    public void setElementQout(List<double[]> elementQout) {
        ElementQout = elementQout;
    }

    public List<double[]> getNodeHout() {
        return NodeHout;
    }

    public void setNodeHout(List<double[]> nodeHout) {
        NodeHout = nodeHout;
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
