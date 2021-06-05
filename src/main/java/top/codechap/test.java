package top.codechap;

import javafx.scene.chart.PieChart;
import sun.security.mscapi.CPublicKey;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.Pipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.model.valve.RegulatingValve;
import top.codechap.utils.Excel2Network;
import top.codechap.utils.ExcelInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CodeChap
 * @date 2021-06-03 14:33
 * @description test
 */
public class test {
    public static void main(String[] args) {
//        test04();
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
//    double[][] doubles = new double[11827][11827];
    }

    private static void test04() {
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData.xlsx";
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
        List<LongPipe> pipes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LongPipe pipe = new LongPipe();
            pipe.setLength(20.00 + i * 6);
            pipes.add(pipe);
        }
        List<ShortPipe> pipes1 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ShortPipe pipe = new ShortPipe();
            pipe.setLength(18.00);
            pipes1.add(pipe);
        }
        List<RegulatingValve> valves = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            RegulatingValve valve = new RegulatingValve();
            valves.add(valve);
        }


        NetWork netWork = new NetWork();
        netWork.setLongPipes(pipes);
        netWork.setShortPipes(pipes1);
        netWork.setRegValves(valves);
        netWork.init();
        for (LongPipe pipe : pipes) {
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

        for (ShortPipe shortPipe : pipes1) {
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
        for (RegulatingValve valve : valves) {
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
}
