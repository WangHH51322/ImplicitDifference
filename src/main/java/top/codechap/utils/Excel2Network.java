package top.codechap.utils;

import lombok.Data;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.model.valve.RegulatingValve;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CodeChap
 * @date 2021-06-04 14:28
 * @description Excel2Network
 */
@Data
public class Excel2Network {

    private List<LongPipe> longPipes;
    private List<ShortPipe> shortPipes;
    private List<Node> nodes;
    private List<RegulatingValve> regValves;
    private String fileName;

    public Excel2Network(String fileName) {
        this.fileName = fileName;
        Map map = readExcel();
        getPipesDataFromExcel(map);
        getValvesDataFromExcel(map);
        getNodesDataFromExcel(map);
    }

    public NetWork getNetWork() {
        NetWork netWork = new NetWork(longPipes, shortPipes, nodes, regValves);
        return netWork;
    }

    private Map readExcel() {
        ExcelInput excelInput = new ExcelInput(fileName);
        return excelInput.read();
    }

    private void getPipesDataFromExcel(Map read) {
        List<List<String>> pipes = (List<List<String>>) read.get("pipes");
        List<LongPipe> longPipes = new ArrayList<>();
        List<ShortPipe> shortPipes = new ArrayList<>();
        for (List<String> pipe : pipes) {
            Double pipeNumbDouble = Double.parseDouble(pipe.get(0));
            Integer pipeNumb = pipeNumbDouble.intValue();
            Double pipeStartNumbDouble = Double.parseDouble(pipe.get(1));
            Integer pipeStartNumb = pipeStartNumbDouble.intValue();
            Double pipeEndNumbDouble = Double.parseDouble(pipe.get(2));
            Integer pipeEndNumb = pipeEndNumbDouble.intValue();
            Double pipeLength = Double.parseDouble(pipe.get(3));
            Double pipeOutsideDiameter = Double.parseDouble(pipe.get(4));
            Double pipeThickness = Double.parseDouble(pipe.get(5));
            Double pipeRoughness = Double.parseDouble(pipe.get(6));
            Double pipeE = Double.parseDouble(pipe.get(7));
            Double pipeC = Double.parseDouble(pipe.get(8));
            if (pipeLength >= 20.00) {
                LongPipe longPipe = new LongPipe();
                longPipe.setNumb(pipeNumb);
                longPipe.setStartNumb(pipeStartNumb);
                longPipe.setEndNumb(pipeEndNumb);
                longPipe.setLength(pipeLength);
                longPipe.setOutsideDiameter(pipeOutsideDiameter);
                longPipe.setThickness(pipeThickness);
                longPipe.setRoughness(pipeRoughness);
                longPipe.setE(pipeE);
                longPipe.setC(pipeC);
                longPipes.add(longPipe);
            } else {
                ShortPipe shortPipe = new ShortPipe();
                shortPipe.setNumb(pipeNumb);
                shortPipe.setStartNumb(pipeStartNumb);
                shortPipe.setEndNumb(pipeEndNumb);
                shortPipe.setLength(pipeLength);
                shortPipe.setOutsideDiameter(pipeOutsideDiameter);
                shortPipe.setThickness(pipeThickness);
                shortPipe.setRoughness(pipeRoughness);
                shortPipe.setE(pipeE);
                shortPipe.setC(pipeC);
                shortPipes.add(shortPipe);
            }
        }
        this.longPipes = longPipes;
        this.shortPipes = shortPipes;
    }

    private void getValvesDataFromExcel(Map read) {
        List<List<String>> valves = (List<List<String>>) read.get("valves");
        List<RegulatingValve> regValves = new ArrayList<>();
        for (List<String> valve : valves) {
            Double valveNumbDouble = Double.parseDouble(valve.get(0));
            Integer valveNumb = valveNumbDouble.intValue();
            Double valveStartNumbDouble = Double.parseDouble(valve.get(1));
            Integer valveStartNumb= valveStartNumbDouble.intValue();
            Double valveEndNumbDouble = Double.parseDouble(valve.get(2));
            Integer valveEndNumb = valveEndNumbDouble.intValue();

            RegulatingValve regValve = new RegulatingValve();
            regValve.setNumb(valveNumb);
            regValve.setStartNumb(valveStartNumb);
            regValve.setEndNumb(valveEndNumb);
            regValves.add(regValve);
        }
        this.regValves = regValves;
    }

    private void getNodesDataFromExcel(Map read) {
        List<List<String>> nodes = (List<List<String>>) read.get("nodes");
        List<Node> nodeLists = new ArrayList<>();
        for (List<String> node : nodes) {
            Double nodeNumbDouble = Double.parseDouble(node.get(0));
            Integer nodeNumb = nodeNumbDouble.intValue();
            Double nodeTypeDouble = Double.parseDouble(node.get(1));
            Integer nodeType = nodeTypeDouble.intValue();
            Double nodeFlow = Double.parseDouble(node.get(2));
            Double nodePressure = Double.parseDouble(node.get(3));

            Node nodeList = new Node();
            nodeList.setNumb(nodeNumb);
            nodeList.setType(nodeType);
            nodeList.setFlow(nodeFlow);
            nodeList.setPressure(nodePressure);
            nodeLists.add(nodeList);
        }
        this.nodes = nodeLists;
    }
}
