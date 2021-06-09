package top.codechap.equations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.Element;
import top.codechap.constant.Constant;
import top.codechap.model.liquid.AviationKerosene;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.Pipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.model.valve.RegulatingValve;
import top.codechap.utils.CalculateLambda;

import java.util.Arrays;
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
    private double[][] CoefficientMatrix;
    private AviationKerosene oil;
    private Double initQ0;
    private Double initH0;
    private double[] Qn;
    private double[] Hn;
    private double[] b;

    private static final Double H_MOMENTUM = 1.00;  //管段动量方程中H的系数

    public FixedFunction(NetWork netWork, AviationKerosene oil) {
        this.netWork = netWork;
        this.oil = oil;
        initQ0 = 0.01;
        initH0 = 600000.00 / (oil.getRou() * Constant.G);
        init();
    }

    private void init() {
        Integer QnSize = netWork.getQnSize();
        Qn = new double[QnSize];
        Arrays.fill(Qn, initQ0);
        Integer HnSize = netWork.getHnSize();
        Hn = new double[HnSize];
        Arrays.fill(Hn, initH0);
        Integer matrixSize = netWork.getMatrixSize();
        b = new double[matrixSize];
    }

    public double[][] GenerateCoefficientMatrix() {
        Integer matrixSize = netWork.getMatrixSize();
        List<LongPipe> longPipes = netWork.getLongPipes();
        List<ShortPipe> shortPipes = netWork.getShortPipes();
        List<RegulatingValve> regValves = netWork.getRegValves();

        double[][] CoefficientMatrix = new double[matrixSize][matrixSize];  //初始化矩阵
        /**
         * 这部分的右端向量b,在CompleteCoefficientMatrix()方法中进行补齐
         */
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
        /**
         * 这部分的右端向量b,在CompleteCoefficientMatrix()方法中进行补齐
         */
        if (shortPipes.size() != 0) {
            for (int i = 0; i < shortPipes.size(); i++) {
                ShortPipe shortPipe = shortPipes.get(i);
                Integer[] momentumNumb = shortPipe.getMomentumNumb();   //短管段中动量方程行编号
                Integer[] motionNumb = shortPipe.getMotionNumb();   //短管段中运动方程行编号
                Integer[] qNumb = shortPipe.getQNumb(); //短管段中Q所在的列编号
                Integer[] hNumb = shortPipe.getHNumb(); //短管段中H所在的列编号
                CoefficientMatrix[momentumNumb[0]][qNumb[0]] = 1;
                CoefficientMatrix[momentumNumb[0]][qNumb[1]] = -1;
                b[momentumNumb[0]] = 0.0;   //短管前后流量始终一致
                CoefficientMatrix[motionNumb[0]][hNumb[0]] = 1;
                CoefficientMatrix[motionNumb[0]][hNumb[1]] = -1;
            }
        }
        if (regValves.size() != 0) {
            for (int i = 0; i < regValves.size(); i++) {
                RegulatingValve valve = regValves.get(i);
                Integer[] momentumNumb = valve.getMomentumNumb();   //阀门中动量方程行编号
                Integer[] motionNumb = valve.getMotionNumb();   //阀门中运动方程行编号
                Integer[] qNumb = valve.getQNumb(); //阀门中Q所在的列编号
                Integer[] hNumb = valve.getHNumb(); //阀门中H所在的列编号
                CoefficientMatrix[momentumNumb[0]][qNumb[0]] = 1;
                CoefficientMatrix[momentumNumb[0]][qNumb[1]] = -1;
                b[momentumNumb[0]] = 0.0;   //阀前后流量始终一致
                CoefficientMatrix[motionNumb[0]][hNumb[0]] = 1;
                CoefficientMatrix[motionNumb[0]][hNumb[1]] = -1;
            }
        }
        this.CoefficientMatrix = CoefficientMatrix;
        return CoefficientMatrix;
    }

    public double[][] CompleteCoefficientMatrix() throws Exception {
        List<LongPipe> longPipes = netWork.getLongPipes();
        List<ShortPipe> shortPipes = netWork.getShortPipes();
        List<RegulatingValve> regValves = netWork.getRegValves();
        CalculateLambda calculateLambda = new CalculateLambda();
        /*Complete运动方程中的Q的系数以及入口,出口边界条件*/
        for (int i = 0; i < longPipes.size(); i++) {
//            System.out.println("执行CompleteCoefficientMatrix()方法中的longPipes");
            LongPipe longPipe = longPipes.get(i);
            Integer firstQNumb = longPipe.getFirstQNumb();
            Double diameter = longPipe.insideDiameter();
            Double roughness = longPipe.getRoughness();
            Integer[] momentumNumb = longPipe.getMomentumNumb();    //长管段中动量方程行编号
            Integer[] motionNumb = longPipe.getMotionNumb();    //长管段中运动方程行编号
            Integer[] qNumb = longPipe.getQNumb();  //长管段中Q所在的列编号
            Integer[] hRealNumb = longPipe.getHRealNumb();  //长管段中HRealNumb所在的列编号
            /*长管段模组计算*/
            calculateLambda.calculate(Qn[firstQNumb],diameter,oil.getViscosity(),roughness);
            longPipe.setLambda(calculateLambda.getLambda());
            longPipe.setM(calculateLambda.getM());
            longPipe.setBeta(calculateLambda.getBeta());
            /*给运动方程中的Q的系数赋值*/
            for (int j = 0; j < motionNumb.length; j++) {
                Double tempValveA = longPipe.getLambda() * Constant.G * longPipe.area() * Constant.STEADY_STATE_T;
                Double tempValveB = Math.pow(Math.abs(Qn[qNumb[j+1]]),(1 - longPipe.getM()));
                CoefficientMatrix[motionNumb[j]][qNumb[j+1]] = (1 + tempValveA * tempValveB);
                /*给方程右端b赋值*/
                b[motionNumb[j]] = Qn[qNumb[j+1]];
            }
            /*给方程右端b赋值*/
            for (int j = 0; j < momentumNumb.length; j++) {
                b[momentumNumb[j]] = Hn[hRealNumb[j]];
            }
            /*元件出口定流,入口定压边界条件*/
            ExitBoundaryCondition(longPipe);
            EntranceBoundaryCondition(longPipe);
        }
        /*Complete阻力元件内部系数边界条件*/
        for (int i = 0; i < shortPipes.size(); i++) {
//            System.out.println("执行CompleteCoefficientMatrix()方法中的shortPipes");
            ShortPipe shortPipe = shortPipes.get(i);
            Integer firstQNumb = shortPipe.getFirstQNumb();
            Double diameter = shortPipe.insideDiameter();
            Double roughness = shortPipe.getRoughness();
            Integer[] motionNumb = shortPipe.getMotionNumb();    //短管段中运动方程行编号
            /*短管段模组计算*/
            calculateLambda.calculate(Qn[firstQNumb],diameter,oil.getViscosity(),roughness);
            shortPipe.setLambda(calculateLambda.getLambda());
            shortPipe.setM(calculateLambda.getM());
            shortPipe.setBeta(calculateLambda.getBeta());
            /*短管压降计算*/
            Double deltaH = shortPipe.CalculateHl(Qn[firstQNumb],oil.getViscosity());
//            System.out.println("deltaH" + deltaH);
            /*短管运动方程右端向量b*/
            b[motionNumb[0]] = deltaH;

            /*边界条件*/
            ExitBoundaryCondition(shortPipe);
            EntranceBoundaryCondition(shortPipe);
        }
        for (int i = 0; i < regValves.size(); i++) {
//            System.out.println("执行CompleteCoefficientMatrix()方法中的regValves");
            RegulatingValve valve = regValves.get(i);
            Integer firstQNumb = valve.getFirstQNumb();
            Integer[] motionNumb = valve.getMotionNumb();    //短管段中运动方程行编号
            /*阀门Cv计算*/
//            Double cv = valve.CalculateCv();
            Double cv = valve.getCv();
//            Double cv = 60.00;
            /*阀门压降计算*/
            Double deltaH = Math.pow(Qn[firstQNumb],2) / cv;
            /*阀门运动方程右端向量b*/
            b[motionNumb[0]] = deltaH;

            ExitBoundaryCondition(valve);
            EntranceBoundaryCondition(valve);
        }
//        System.out.println("b:");
//        for (int i = 0; i < b.length; i++) {
//            System.out.print(b[i] + "_");
//        }
        return CoefficientMatrix;
    }

    private void EntranceBoundaryCondition(Element element) throws Exception {
//        System.out.println("执行入口边界条件方法");
        Node startNode = element.getStartNode();    //元件入口节点
        Integer nodeType = startNode.getType(); //元件入口节点的类型
        List<Element> inElements = startNode.getInElements(); //获取与这个节点相连,并且作为元件出口的其他元件
        List<Element> outElements = startNode.getOutElements(); //获取与这个节点相连,并且作为元件入口的其他元件
        Integer connectionType = startNode.nodeConnectionType();  //查看节点的连接类型

        Integer inNumb = element.getInBoundaryConditionNumb();  //元件入口边界条件所在行编号
        Integer firstQNumb = element.getFirstQNumb();   //元件入口QNumb所在列编号
        List<Integer> firstHNumbsOfElement = element.getFirstHNumbs();    //获取元件第一个或两个HNumb所在列编号
        List<Double> startCoefficientOfElement = element.getStartCoefficient(); //获取元件第一个或两个HNumb对应的系数
        switch(connectionType) {
            case 10 :   //与一个元件相连,作为元件入口;
                for (int i = 0; i < firstHNumbsOfElement.size(); i++) {
                    CoefficientMatrix[inNumb][firstHNumbsOfElement.get(i)] = startCoefficientOfElement.get(i);  //入口压力平衡
                }
                if (nodeType == 2) {    //startNode是盲段
                    b[inNumb] = 0;
                } else if (nodeType == 0) { //startNode是管网入口
                    b[inNumb] = startNode.getPressure() / (oil.getRou() * Constant.G);
                } else {
                    throw new RuntimeException("节点" + startNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题");
                }
                break;
            case 210 :  //与两个元件相连,作为一入一出;此时是元件入口
                Element oneOf210 = inElements.get(0);    //获取 与这个节点相连,并且作为元件出口的另一个元件
                Integer oneOutNumbOf210 = oneOf210.getOutBoundaryConditionNumb();    //one210元件出口边界条件所在行编号
                List<Integer> lastHNumbsOf210 = oneOf210.getLastHNumbs();    //获取oneOf210元件最后一个或两个HNumb所在列编号
                List<Double> endCoefficientOf210 = oneOf210.getEndCoefficient(); //获取oneOf210元件最后一个或两个HNumb对应的系数
                for (int i = 0; i < firstHNumbsOfElement.size(); i++) {
                    CoefficientMatrix[inNumb][firstHNumbsOfElement.get(i)] = startCoefficientOfElement.get(i);  //入口压力平衡
                }
                for (int i = 0; i < lastHNumbsOf210.size(); i++) {
                    CoefficientMatrix[inNumb][lastHNumbsOf210.get(i)] = - endCoefficientOf210.get(i);  //入口压力平衡
                }
                if (nodeType == 2) {    //endNode是中间节点
                    b[inNumb] = 0.00;
                } else if (nodeType == 1) { //endNode是管网出口
                    b[inNumb] = 0.00;
                } else {
                    throw new RuntimeException("节点" + startNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题");
                }
                break;
            case 200 :  //与两个元件相连,作为双入口;此时是元件入口
                Element oneOf200 = outElements.get(0);  //获取 与这个节点相连,并且作为元件入口的另一个元件
                if (oneOf200.getNumb() == element.getNumb()) {
                    oneOf200 = inElements.get(1);
                }
                if (!startNode.isUsed()) {    //判断入口是否曾使用过节点流量平衡条件,如果否,则可以使用节点流量平衡条件
                    Integer oneInNumbOf211 = oneOf200.getInBoundaryConditionNumb();    //one元件入口边界条件所在行编号
                    Integer oneFirstQNumbOf211 = oneOf200.getFirstQNumb(); //one元件入口点Q的列编号
                    CoefficientMatrix[inNumb][firstQNumb] = - 1;  //入口流量平衡
                    CoefficientMatrix[inNumb][oneFirstQNumbOf211] = - 1;  //入口流量平衡
                    if (nodeType == 2) {    //endNode是中间节点
                        b[inNumb] = 0.00;
                    } else {
                        throw new RuntimeException("节点" + startNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题");
                    }
                    startNode.setFlag(true);  //此节点已使用过节点流量平衡条件,将flag更改为true
                } else {    //改用入口压力平衡方程
                    for (int i = 0; i < firstHNumbsOfElement.size(); i++) {
                        CoefficientMatrix[inNumb][firstHNumbsOfElement.get(i)] = startCoefficientOfElement.get(i);  //入口压力平衡
                    }
                    if (nodeType == 2) {
                        List<Integer> firstHNumbsOf200 = oneOf200.getFirstHNumbs();
                        List<Double> startCoefficientOf200 = oneOf200.getStartCoefficient();
                        for (int i = 0; i < firstHNumbsOf200.size(); i++) {
                            CoefficientMatrix[inNumb][firstHNumbsOf200.get(i)] = - startCoefficientOf200.get(i);  //oneOf200入口压力平衡
                        }
                        b[inNumb] = 0.00;
                    } else if (nodeType == 0){
                        b[inNumb] = startNode.getPressure();
                    } else {
                        throw new RuntimeException("节点" + startNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题");
                    }
                }
                break;
            case 3110 : //与三个元件相连,作为一入二出;此时是元件的入口
            case 3100 : //与三个元件相连,作为二入一出;此时是元件的入口
                for (int i = 0; i < firstHNumbsOfElement.size(); i++) {
                    CoefficientMatrix[inNumb][firstHNumbsOfElement.get(i)] = startCoefficientOfElement.get(i);  //入口压力平衡
                }
                Element oneOf3110 = inElements.get(0);  //获取 与这个节点相连,并且作为元件出口的任意一个元件
                List<Integer> lastHNumbsOf3110 = oneOf3110.getLastHNumbs();    //获取oneOf3110元件最后一个或两个HNumb所在列编号
                List<Double> endCoefficientOf3110 = oneOf3110.getEndCoefficient(); //获取oneOf3110元件最后一个或两个HNumb对应的系数
                for (int i = 0; i < lastHNumbsOf3110.size(); i++) {
                    CoefficientMatrix[inNumb][lastHNumbsOf3110.get(i)] = - endCoefficientOf3110.get(i);  //入口压力平衡
                }
                if (nodeType == 2) {    //startNode是中间节点
                    b[inNumb] = 0.00;
                } else {
                    throw new RuntimeException("节点" + startNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题");
                }
                break;
            case 3000 :
                List<Element> elements = startNode.getElements();
                Element element0 = elements.get(0);
                Element element1 = elements.get(1);
                Element element2 = elements.get(2);
                List<Integer> firstHNumbOfElement0 = element0.getFirstHNumbs();    //element0入口HNumbs所在列编号
                List<Double> startCoefficientOfElement0 = element0.getStartCoefficient();   ////element0入口HNumbs的系数
                if (element.getNumb() == element0.getNumb()) {   //element是三入口中的第一个,直接使用流量平衡条件
                    Integer firstQNumb1 = element1.getFirstQNumb();
                    Integer firstQNumb2 = element2.getFirstQNumb();
                    CoefficientMatrix[inNumb][firstQNumb] = - 1;  //element入口流量平衡
                    CoefficientMatrix[inNumb][firstQNumb1] = - 1;  //element1入口流量平衡
                    CoefficientMatrix[inNumb][firstQNumb2] = - 1;  //element2入口流量平衡
                } else if (element.getNumb() == element1.getNumb() || element.getNumb() == element2.getNumb()) {   //element是三入口中的第二,三个,直接使用压力平衡条件
                    for (int i = 0; i < firstHNumbsOfElement.size(); i++) {
                        CoefficientMatrix[inNumb][firstHNumbsOfElement.get(i)] = startCoefficientOfElement.get(i);  //element入口压力平衡
                    }
                    for (int i = 0; i < firstHNumbOfElement0.size(); i++) {
                        CoefficientMatrix[inNumb][firstHNumbOfElement0.get(i)] = startCoefficientOfElement0.get(i);  //element入口压力平衡
                    }
                } else {
                    throw new RuntimeException("节点" + startNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题,是三入口");
                }
                b[inNumb] = 0.00;
                break;
        }
    }

    private void ExitBoundaryCondition(Element element) throws Exception {
//        System.out.println("执行出口边界条件方法");
        Node endNode = element.getEndNode();    //元件出口节点边界条件
        Integer nodeType = endNode.getType();
        List<Element> elements = endNode.getElements(); //获取与这个节点相连的其他元件
        List<Element> inElements = endNode.getInElements(); //获取与这个节点相连,并且作为元件出口的其他元件
        List<Element> outElements = endNode.getOutElements(); //获取与这个节点相连,并且作为元件入口的其他元件
        Integer connectionType = endNode.nodeConnectionType();  //查看节点的连接类型

        Integer outNumb = element.getOutBoundaryConditionNumb();    //元件出口边界条件所在行编号
        Integer lastQNumb = element.getLastQNumb(); //元件出口点Q的列编号
        List<Integer> lastHNumbsOfElement = element.getLastHNumbs();    //获取元件最后一个或两个HNumb所在列编号
        List<Double> endCoefficientOfElement = element.getEndCoefficient(); //获取元件最后一个或两个HNumb对应的系数
        switch(connectionType) {
            case 11 :   //与一个元件相连,作为元件出口;
                CoefficientMatrix[outNumb][lastQNumb] = 1;  //出口流量平衡
                if (nodeType == 2) {    //endNode是盲段
                    b[outNumb] = 0;
                } else if (nodeType == 1) { //endNode是管网出口
                    b[outNumb] = endNode.getFlow();
                } else {
                    throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                }
//                System.out.println("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口");
                break;
            case 210 :  //与两个元件相连,作为一入一出;此时是元件出口
                Element oneOf210 = outElements.get(0);    //获取 与这个节点相连,并且作为元件入口的另一个元件
                Integer oneInNumbOf210 = oneOf210.getInBoundaryConditionNumb();    //one210元件入口边界条件所在行编号
                Integer oneFirstQNumbOf210 = oneOf210.getFirstQNumb(); //one210元件入口点Q的列编号
                CoefficientMatrix[outNumb][lastQNumb] = 1;  //出口定流
                CoefficientMatrix[outNumb][oneFirstQNumbOf210] = -1;  //出口流量平衡
                if (nodeType == 2) {    //endNode是中间节点
                    b[outNumb] = 0.00;
                } else if (nodeType == 1) { //endNode是管网出口
                    b[outNumb] = endNode.getFlow();
                } else {
                    throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                }
                break;
            case 211 :  //与两个元件相连,作为双出口;此时是元件出口
                Element oneOf211 = inElements.get(0);  //获取 与这个节点相连,并且作为元件出口的另一个元件
                if (oneOf211.getNumb() == element.getNumb()) {
                    oneOf211 = inElements.get(1);
                }
                if (!endNode.isUsed()) {    //判断出口是否曾使用过节点流量平衡条件,如果否,则可以继续使用节点流量平衡条件
                    Integer oneOutNumbOf211 = oneOf211.getOutBoundaryConditionNumb();    //one元件出口边界条件所在行编号
                    Integer oneLastQNumbOf211 = oneOf211.getLastQNumb(); //one元件出口点Q的列编号
                    CoefficientMatrix[outNumb][lastQNumb] = 1;  //出口流量平衡
                    CoefficientMatrix[outNumb][oneLastQNumbOf211] = 1;  //出口流量平衡
                    if (nodeType == 2) {    //endNode是中间节点
                        b[outNumb] = 0.00;
                    } else if (nodeType == 1) { //endNode是管网出口
                        b[outNumb] = endNode.getFlow();
                    } else {
                        throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                    }
                    endNode.setFlag(true);  //此节点已使用过节点流量平衡条件,将flag更改为true
                } else {    //改用压力平衡方程
                    for (int i = 0; i < lastHNumbsOfElement.size(); i++) {
                        CoefficientMatrix[outNumb][lastHNumbsOfElement.get(i)] = endCoefficientOfElement.get(i);  //element出口压力平衡
                    }
                    List<Integer> lastHNumbsOf211 = oneOf211.getLastHNumbs();
                    List<Double> endCoefficientOf211 = oneOf211.getEndCoefficient();
                    for (int i = 0; i < lastHNumbsOf211.size(); i++) {
                        CoefficientMatrix[outNumb][lastHNumbsOf211.get(i)] = - endCoefficientOf211.get(i);  //oneOf211出口压力平衡
                    }
                    if (nodeType == 2) {
                        b[outNumb] = 0.00;
                    } else {
                        throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                    }
                }
                break;
            case 3100 : //与三个元件相连,作为二入一出;此时是元件出口
                CoefficientMatrix[outNumb][lastQNumb] = 1;  //出口流量平衡
                for (int i = 0; i < outElements.size(); i++) {   //获取与这个节点相连,并且作为元件入口的两个元件
                    Element oneOf3100 = outElements.get(i);
                    Integer elementOneFirstQNumb = oneOf3100.getFirstQNumb(); //元件elementOne入口点Q的列编号
                    CoefficientMatrix[outNumb][elementOneFirstQNumb] = - 1;  //出口流量平衡
                }
                if (nodeType == 2) {
                    b[outNumb] = 0.00;
                } else {
                    throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                }
                break;
            case 3110 : //与三个元件相连,作为一入二出;此时是元件出口
//                System.out.println("endNode.getNumb() = " + endNode.getNumb());
                Element oneOf3110 = inElements.get(0);  //获取 与这个节点相连,并且作为元件出口的另一个元件
//                System.out.println("inElements.size() = " + inElements.size());
                Element twoOf3110 = outElements.get(0);  //获取 与这个节点相连,并且作为元件入口的另一个元件
//                System.out.println("outElements.size() = " + outElements.size());
                if (oneOf3110.getNumb() == element.getNumb()) {
                    oneOf3110 = inElements.get(1);
                }
                if (!endNode.isUsed()) {    //判断出口是否曾使用过节点流量平衡条件,如果否,则可以继续使用节点流量平衡条件
                    Integer oneOutNumbOf3110 = oneOf3110.getOutBoundaryConditionNumb();    //one元件出口边界条件所在行编号
                    Integer oneLastQNumbOf3110 = oneOf3110.getLastQNumb(); //one元件出口点Q的列编号
                    Integer twoInNumbOf3110 = twoOf3110.getInBoundaryConditionNumb();   //two元件入口边界条件所在行编号
                    Integer twoFirstQNumbOf3110 = twoOf3110.getFirstQNumb();    //two元件入口点Q的列编号
                    CoefficientMatrix[outNumb][lastQNumb] = 1;  //出口流量平衡
                    CoefficientMatrix[outNumb][oneLastQNumbOf3110] = 1;  //出口流量平衡
                    CoefficientMatrix[outNumb][twoFirstQNumbOf3110] = -1;  //出口流量平衡
                    if (nodeType == 2) {    //endNode是中间节点
                        b[outNumb] = 0.00;
                    } else {
                        throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                    }
                    endNode.setFlag(true);  //此节点已使用过节点流量平衡条件,将flag更改为true
                } else {    //改用压力平衡方程
                    for (int i = 0; i < lastHNumbsOfElement.size(); i++) {
                        CoefficientMatrix[outNumb][lastHNumbsOfElement.get(i)] = endCoefficientOfElement.get(i);  //element出口压力平衡
                    }
                    List<Integer> lastHNumbsOf3110 = oneOf3110.getLastHNumbs();
                    List<Double> endCoefficientOf3110 = oneOf3110.getEndCoefficient();
                    for (int i = 0; i < lastHNumbsOf3110.size(); i++) {
                        CoefficientMatrix[outNumb][lastHNumbsOf3110.get(i)] = - endCoefficientOf3110.get(i);  //oneOf211出口压力平衡
                    }
                    if (nodeType == 2) {
                        b[outNumb] = 0.00;
                    } else {
                        throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "出口存在问题");
                    }
                }
                break;
            case 3111 : //3111 与三个元件相连,作为三出口;此时是元件出口
                Element element0 = elements.get(0);
                Element element1 = elements.get(1);
                Element element2 = elements.get(2);
                List<Integer> lastHNumbOfElement0 = element0.getLastHNumbs();    //element0出口HNumbs所在列编号
                List<Double> endCoefficientOfElement0 = element0.getEndCoefficient();   ////element0出口HNumbs的系数
                if (element.getNumb() == element0.getNumb()) {   //element是三出口中的第一个,直接使用流量平衡条件
                    Integer lastQNumb1 = element1.getLastQNumb();
                    Integer lastQNumb2 = element2.getLastQNumb();
                    CoefficientMatrix[outNumb][lastQNumb] = 1;  //element出口流量平衡
                    CoefficientMatrix[outNumb][lastQNumb1] = 1;  //element1出口流量平衡
                    CoefficientMatrix[outNumb][lastQNumb2] = 1;  //element2出口流量平衡

                } else if (element.getNumb() == element1.getNumb() || element.getNumb() == element2.getNumb()) {   //element是三出口中的第二,三个,直接使用压力平衡条件
                    for (int i = 0; i < lastHNumbsOfElement.size(); i++) {
                        CoefficientMatrix[outNumb][lastHNumbsOfElement.get(i)] = endCoefficientOfElement.get(i);  //element出口压力平衡
                    }
                    for (int i = 0; i < lastHNumbOfElement0.size(); i++) {
                        CoefficientMatrix[outNumb][lastHNumbOfElement0.get(i)] = endCoefficientOfElement0.get(i);  //element0出口压力平衡
                    }
                } else {
                    throw new RuntimeException("节点" + endNode.getNumb() + "作为元件" + element.getNumb() + "入口存在问题,是三入口");
                }
                b[outNumb] = 0.00;
                break;
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
        System.out.println("压力波波速a: " + result);
        return result;
    }
}
