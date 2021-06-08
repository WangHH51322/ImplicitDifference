package top.codechap;

import javafx.scene.chart.PieChart;
import net.jamu.matrix.Matrices;
import net.jamu.matrix.MatrixD;
import sun.security.mscapi.CPublicKey;
import top.codechap.equations.FixedFunction;
import top.codechap.model.liquid.AviationKerosene;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.Pipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.model.valve.RegulatingValve;
import top.codechap.simulation.SteadyState;
import top.codechap.utils.Excel2Network;
import top.codechap.utils.ExcelInput;
import top.codechap.utils.ExcelOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CodeChap
 * @date 2021-06-03 14:33
 * @description test
 */
public class test {
    public static void main(String[] args) throws Exception {
        test08();
     }

    private static void test08() throws Exception {
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData002.xlsx";
        Excel2Network excel2Network = new Excel2Network(fileName);
        NetWork netWork = excel2Network.getNetWork();
        netWork.init();

        AviationKerosene oil = new AviationKerosene();
        FixedFunction fixedFunction = new FixedFunction(netWork,oil);

        SteadyState steadyState = new SteadyState(fixedFunction);
        steadyState.setTimes(200 * 3600.00);
        steadyState.run();
        double[][] coefficientMatrix = steadyState.getCoefficientMatrix();
        double[] b = steadyState.getB();
        double[][] bb = new double[b.length][1];
        for (int i = 0; i < b.length; i++) {
            bb[i][0] = b[i];
        }
        List<double[]> Q = steadyState.getQout();
        double[][] Qout = new double[Q.size()][Q.get(0).length];
        for (int i = 0; i < Qout.length; i++) {
            for (int j = 0; j < Qout[i].length; j++) {
                Qout[i][j] = Q.get(i)[j];
            }
        }
        List<double[]> H = steadyState.getHout();
        double[][] Hout = new double[H.size()][H.get(0).length];
        for (int i = 0; i < Hout.length; i++) {
            for (int j = 0; j < Hout[i].length; j++) {
                Hout[i][j] = H.get(i)[j];
            }
        }

        ExcelOutput excelOutput = new ExcelOutput();
        excelOutput.setFileName("C:\\Users\\WangHH\\Desktop\\OutputData002.xlsx");
        Map<String,double[][]> inputData = new HashMap<>();
        inputData.put("系数矩阵",coefficientMatrix);
        inputData.put("b",bb);
        inputData.put("Qout",Qout);
        inputData.put("Hout",Hout);
        excelOutput.setInputData(inputData);
        excelOutput.writeTwoData2Excel();
    }

    private static void test06() throws Exception {
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData002.xlsx";
        Excel2Network excel2Network = new Excel2Network(fileName);
        NetWork netWork = excel2Network.getNetWork();
        netWork.init();

        AviationKerosene oil = new AviationKerosene();
        FixedFunction fixedFunction = new FixedFunction(netWork,oil);
        double[][] doubles = fixedFunction.GenerateCoefficientMatrix();
        double[][] matrix;
        matrix = fixedFunction.CompleteCoefficientMatrix();
        System.out.println("matrix: ");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println(" ");
        }
        double[] b = fixedFunction.getB();
        double[][] bb = new double[b.length][1];
        for (int i = 0; i < b.length; i++) {
            bb[i][0] = b[i];
        }

        ExcelOutput excelOutput = new ExcelOutput();
        excelOutput.setFileName("C:\\Users\\WangHH\\Desktop\\OutputData002.xlsx");
        Map<String,double[][]> inputData = new HashMap<>();
        inputData.put("系数矩阵",matrix);
        inputData.put("b",bb);
        excelOutput.setInputData(inputData);
        excelOutput.writeTwoData2Excel();
    }

    private static void test05() {
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData2.xlsx";
        Excel2Network excel2Network = new Excel2Network(fileName);
        NetWork netWork = excel2Network.getNetWork();
        List<LongPipe> longPipes = netWork.getLongPipes();
        List<ShortPipe> shortPipes = netWork.getShortPipes();
        List<RegulatingValve> regValves = netWork.getRegValves();
        int sum = 0;
        for (LongPipe longPipe : longPipes) {
            sum += longPipe.getSegments();
        }
        sum = 2 * sum + longPipes.size() + 4 * (shortPipes.size() + regValves.size());
        System.out.println("sum = " + sum);
    }

    private static void test04() {
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData002.xlsx";
        Excel2Network excel2Network = new Excel2Network(fileName);
        List<Node> nodes = excel2Network.getNodes();
        System.out.println("nodes: ");
        for (Node node : nodes) {
            System.out.println(node);
        }
        System.out.println("longPipes: ");
        List<LongPipe> longPipes = excel2Network.getLongPipes();
        for (LongPipe longPipe : longPipes) {
            System.out.println(longPipe);
        }
        System.out.println("shortPipes: ");
        List<ShortPipe> shortPipes = excel2Network.getShortPipes();
        for (ShortPipe shortPipe : shortPipes) {
            System.out.println(shortPipe);
        }
        System.out.println("valves: ");
        List<RegulatingValve> regValves = excel2Network.getRegValves();
        for (RegulatingValve valve : regValves) {
            System.out.println(valve);
        }
    }

    private static void test03() {
        List<LongPipe> pipes = new ArrayList<>();

        LongPipe pipe1 = new LongPipe();
        pipe1.setNumb(10001);
        pipe1.setLength(22.00);
        pipe1.setStartNumb(1);
        pipe1.setEndNumb(2);
        pipes.add(pipe1);
        LongPipe pipe2 = new LongPipe();
        pipe2.setNumb(10002);
        pipe2.setLength(26.00);
        pipe2.setStartNumb(2);
        pipe2.setEndNumb(3);
        pipes.add(pipe2);
        LongPipe pipe3 = new LongPipe();
        pipe3.setNumb(10003);
        pipe3.setLength(29.00);
        pipe3.setStartNumb(3);
        pipe3.setEndNumb(4);
        pipes.add(pipe3);

        List<ShortPipe> shortPipes = new ArrayList<>();
        ShortPipe shortPipe = new ShortPipe();
        shortPipe.setNumb(20001);
        shortPipe.setLength(18.00);
        shortPipe.setStartNumb(4);
        shortPipe.setEndNumb(5);
        shortPipes.add(shortPipe);

        List<RegulatingValve> valves = new ArrayList<>();
        RegulatingValve valve = new RegulatingValve();
        valve.setNumb(30001);
        valve.setStartNumb(5);
        valve.setEndNumb(6);
        valves.add(valve);

        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Node node = new Node();
            node.setNumb(i + 1);
            nodes.add(node);
        }

        NetWork netWork = new NetWork();
        netWork.setLongPipes(pipes);
        netWork.setShortPipes(shortPipes);
        netWork.setRegValves(valves);
        netWork.setNodes(nodes);
        netWork.init();

        for (LongPipe pipe : pipes) {
            System.out.println("pipe.node:");
            Node startNode = pipe.getStartNode();
            System.out.print(startNode.getNumb() + "  ");
            Node endNode = pipe.getEndNode();
            System.out.print(endNode.getNumb() + "  ");
            System.out.println(" ");
        }

        for (ShortPipe shortPipe1 : shortPipes) {
            System.out.println("shortPipe1.node:");
            Node startNode = shortPipe1.getStartNode();
            System.out.print(startNode.getNumb() + "  ");
            Node endNode = shortPipe1.getEndNode();
            System.out.print(endNode.getNumb() + "  ");
            System.out.println(" ");
        }
        for (RegulatingValve valve1 : valves) {
            System.out.println("valve1.node:");
            Node startNode = valve1.getStartNode();
            System.out.print(startNode.getNumb() + "  ");
            Node endNode = valve1.getEndNode();
            System.out.print(endNode.getNumb() + "  ");
            System.out.println(" ");
        }
        for (Node node : nodes) {
            System.out.println("node:");
            System.out.println("node.getConnectionType() = " + node.nodeConnectionType());
            for (Element element : node.getElements()) {
                System.out.println("element.Numb = " + element.getNumb());
            }
            for (Element inElement : node.getInElements()) {
                System.out.println("inElement.Numb = " + inElement.getNumb());
            }
            for (Element outElement : node.getOutElements()) {
                System.out.println("outElement.Numb = " + outElement.getNumb());
            }
        }
    }

    private static void test02() {
//        List<LongPipe> pipes = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            LongPipe pipe = new LongPipe();
//            pipe.setLength(20.00 + i * 6);
//            pipes.add(pipe);
//        }
//        List<ShortPipe> pipes1 = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            ShortPipe pipe = new ShortPipe();
//            pipe.setLength(18.00);
//            pipes1.add(pipe);
//        }
//        List<RegulatingValve> valves = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            RegulatingValve valve = new RegulatingValve();
//            valves.add(valve);
//        }


//        NetWork netWork = new NetWork();
//        netWork.setLongPipes(pipes);
//        netWork.setShortPipes(pipes1);
//        netWork.setRegValves(valves);
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData002.xlsx";
        Excel2Network excel2Network = new Excel2Network(fileName);
        NetWork netWork = excel2Network.getNetWork();
        netWork.init();
        List<LongPipe> longPipes = netWork.getLongPipes();
        List<ShortPipe> shortPipes = netWork.getShortPipes();
        List<RegulatingValve> regValves = netWork.getRegValves();
        List<Node> nodes = netWork.getNodes();
        for (Node node : nodes) {
            Integer numb = node.getNumb();
            System.out.println("节点" + numb + "的连接类型为 : " + node.nodeConnectionType());
        }
        for (LongPipe pipe : longPipes) {
            System.out.println("longPipe:");
            for (int i = 0; i < pipe.getQNumb().length; i++) {
                System.out.print(pipe.getQNumb()[i] + " ");
            }
            System.out.println(" ");
            for (int i = 0; i < pipe.getHNumb().length; i++) {
                System.out.print(pipe.getHNumb()[i] + " ");
            }
            System.out.println(" ");
        }
        for (ShortPipe shortPipe : shortPipes) {
            System.out.println("shortPipe:");
            for (int i = 0; i < shortPipe.getQNumb().length; i++) {
                System.out.print(shortPipe.getQNumb()[i] + " ");
            }
            System.out.println(" ");
            for (int i = 0; i < shortPipe.getHNumb().length; i++) {
                System.out.print(shortPipe.getHNumb()[i] + " ");
            }
            System.out.println(" ");
        }
        for (RegulatingValve valve : regValves) {
            System.out.println("valve:");
            for (int i = 0; i < valve.getQNumb().length; i++) {
                System.out.print(valve.getQNumb()[i] + " ");
            }
            System.out.println(" ");
            for (int i = 0; i < valve.getHNumb().length; i++) {
                System.out.print(valve.getHNumb()[i] + " ");
            }
            System.out.println(" ");
        }
        System.out.println("=++++++++++++++++++++++++++++=");
        for (LongPipe pipe : longPipes) {
            System.out.println("longPipe:");
            for (int i = 0; i < pipe.getMomentumNumb().length; i++) {
                System.out.print(pipe.getMomentumNumb()[i] + " ");
            }
            System.out.println(" ");
            System.out.println("OutBoundaryConditionNumb: " + pipe.getOutBoundaryConditionNumb());
            System.out.println("InBoundaryConditionNumb: " + pipe.getInBoundaryConditionNumb());
            for (int i = 0; i < pipe.getMotionNumb().length; i++) {
                System.out.print(pipe.getMotionNumb()[i] + " ");
            }
            System.out.println(" ");
        }
        for (ShortPipe shortPipe : shortPipes) {
            System.out.println("shortPipe:");
            for (int i = 0; i < shortPipe.getMomentumNumb().length; i++) {
                System.out.print(shortPipe.getMomentumNumb()[i] + " ");
            }
            System.out.println(" ");
            System.out.println("OutBoundaryConditionNumb: " + shortPipe.getOutBoundaryConditionNumb());
            System.out.println("InBoundaryConditionNumb: " + shortPipe.getInBoundaryConditionNumb());
            for (int i = 0; i < shortPipe.getMotionNumb().length; i++) {
                System.out.print(shortPipe.getMotionNumb()[i] + " ");
            }
            System.out.println(" ");
        }
        for (RegulatingValve valve : regValves) {
            System.out.println("valve:");
            for (int i = 0; i < valve.getMomentumNumb().length; i++) {
                System.out.print(valve.getMomentumNumb()[i] + " ");
            }
            System.out.println(" ");
            System.out.println("OutBoundaryConditionNumb: " + valve.getOutBoundaryConditionNumb());
            System.out.println("InBoundaryConditionNumb: " + valve.getInBoundaryConditionNumb());
            for (int i = 0; i < valve.getMotionNumb().length; i++) {
                System.out.print(valve.getMotionNumb()[i] + " ");
            }
            System.out.println(" ");
        }

    }

    private static void test01() {
        System.out.println(16.0/(Math.PI)/(Math.PI));

        Pipe pipe = new LongPipe();
        pipe.setLength(21.0);
        System.out.println("pipe.getSegments() = " + pipe.getSegments());
        System.out.println("pipe.lastSegLength() = " + pipe.lastSegLength());
        System.out.println("allSegLength[i] = ");
        Double[] allSegLength = pipe.getAllSegLength();
        for (int i = 0; i < allSegLength.length; i++) {
            System.out.print(allSegLength[i] + "__");
        }
    }

    private static void test07() {
        int n = 1800;
        MatrixD A = Matrices.randomUniformD(n, n);
        MatrixD B = Matrices.randomUniformD(n, 1);
        MatrixD x = Matrices.createD(n, 1);
//        A.lud();
        x = A.solve(B, x);
        System.out.print(x + "_");
    }
}
