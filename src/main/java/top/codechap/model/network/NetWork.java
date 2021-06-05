package top.codechap.model.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.Element;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.Pipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.model.valve.RegulatingValve;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-03 16:06
 * @description NetWork
 */
@Data
@AllArgsConstructor
public class NetWork {

    private List<LongPipe> longPipes;
    private List<ShortPipe> shortPipes;
    private List<Node> nodes;
    private List<RegulatingValve> regValves;
    private Integer matrixSize;
    private Integer halfSize;

    public NetWork() {
        this.longPipes = new ArrayList<>();
        this.shortPipes = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.regValves = new ArrayList<>();
    }

    public NetWork(List<LongPipe> longPipes, List<ShortPipe> shortPipes, List<Node> nodes, List<RegulatingValve> regValves) {
        this.longPipes = longPipes;
        this.shortPipes = shortPipes;
        this.nodes = nodes;
        this.regValves = regValves;
    }

    public void init() {
        CalculateMatrixSize();
        CalculateQAndHNumb();
        ConnectElementAndNode();
    }

    private void ConnectElementAndNode() {   //根据元件的起终点编号将其与对应的节点关联起来;
        if (longPipes.size() != 0) {
            for (LongPipe longPipe : longPipes) {
                Integer startNumb = longPipe.getStartNumb();
                Node startNode = nodes.get(startNumb - 1);
                startNode.addElements(longPipe);
                startNode.addOutElements(longPipe);
                longPipe.setStartNode(startNode);

                Integer endNumb = longPipe.getEndNumb();
                Node endNode = nodes.get(endNumb - 1);
                endNode.addElements(longPipe);
                endNode.addInElements(longPipe);
                longPipe.setEndNode(endNode);
            }
        }
        if (shortPipes.size() != 0) {
            for (ShortPipe shortPipe : shortPipes) {
                Integer startNumb = shortPipe.getStartNumb();
                Node startNode = nodes.get(startNumb - 1);
                startNode.addElements(shortPipe);
                startNode.addOutElements(shortPipe);
                shortPipe.setStartNode(startNode);

                Integer endNumb = shortPipe.getEndNumb();
                Node endNode = nodes.get(endNumb - 1);
                endNode.addElements(shortPipe);
                endNode.addInElements(shortPipe);
                shortPipe.setEndNode(endNode);
            }
        }
        if (regValves.size() != 0) {
            for (RegulatingValve regValve : regValves) {
                Integer startNumb = regValve.getStartNumb();
                Node startNode = nodes.get(startNumb - 1);
                startNode.addElements(regValve);
                startNode.addOutElements(regValve);
                regValve.setStartNode(startNode);

                Integer endNumb = regValve.getEndNumb();
                Node endNode = nodes.get(endNumb - 1);
                endNode.addElements(regValve);
                endNode.addInElements(regValve);
                regValve.setEndNode(endNode);
            }
        }
    }

//    private void extracted(List<LongPipe> elements) {
//        for (Element element : elements) {
//            Integer startNumb = element.getStartNumb();
//            Node startNode = nodes.get(startNumb - 1);
//            startNode.addElements(element);
//            startNode.addOutElements(element);
//            element.setStartNode(startNode);
//
//            Integer endNumb = element.getEndNumb();
//            Node endNode = nodes.get(endNumb - 1);
//            endNode.addElements(element);
//            endNode.addInElements(element);
//            element.setEndNode(endNode);
//        }
//    }

    private void CalculateMatrixSize() { //计算整个系数矩阵大小,和halfSize大小
        int sum = 0;
        int halfSize;
        for (LongPipe longPipe : longPipes) {
            sum += longPipe.getSegments();
        }
        halfSize = sum + longPipes.size() + 2 * (shortPipes.size() + regValves.size());
        sum = 2 * sum + longPipes.size() + 4 * (shortPipes.size() + regValves.size());
        this.halfSize = halfSize;
        this.matrixSize = sum;
    }

    private void CalculateQAndHNumb() {  //给元件中的QNumb和HNumb属性值赋值
        if (longPipes.size() != 0) {    //给长管段中的QNumb和HNumb属性值赋值
            UpdateLongPipeQHNumb();
        }

        if (shortPipes.size() != 0) {   //给短管段中的QNumb和HNumb属性值赋值
            UpdateShortPipeQHNumb();
        }

        if (regValves.size() != 0) {    //给阀门中的QNumb和HNumb属性值赋值
            UpdateRegValveQHNumb();
        }
    }

    private void UpdateRegValveQHNumb() {   //给阀门中的QNumb和HNumb属性值赋值
        Integer startQNumb = 0;
        Integer startHNumb = halfSize;
        if (shortPipes.size() != 0) { //此时管网内有短管道
            ShortPipe lastShortPipe = shortPipes.get(shortPipes.size() - 1);
            startQNumb = 1 + lastShortPipe.getLastQNumb();   //获取到最后一根短管段的最末端分段的QNumb编号
            startHNumb = 1 + lastShortPipe.getLastHNumb();   //获取到最后一根短管段的最末端分段的HNumb编号
        } else if (longPipes.size() != 0) {    //此时管网内有长管段但没有短管道
            LongPipe lastLongPipe = longPipes.get(longPipes.size() - 1);
            startQNumb = 1 + lastLongPipe.getLastQNumb();    //获取到最后一根长管段的最末端分段的QNumb编号
            startHNumb = 1 + lastLongPipe.getLastHNumb();   //获取到最后一根长管段的最末端分段的HNumb编号
        }
        for (int i = 0; i < regValves.size(); i++) {
            RegulatingValve valve = regValves.get(i);
            Integer[] QNumb = new Integer[2];
            Integer[] HNumb = new Integer[2];

            if (i == 0) {   //给valve中的QNumb属性值赋值
                QNumb[0] = startQNumb;
                HNumb[0] = startHNumb;
            } else {
                RegulatingValve valveBefore = regValves.get(i - 1);
                Integer addQNumb = valveBefore.getLastQNumb();
                QNumb[0] = addQNumb + 1;
                Integer addHNumb = valveBefore.getLastHNumb();
                HNumb[0] = addHNumb + 1;
            }
            QNumb[1] = QNumb[0] + 1;
            HNumb[1] = HNumb[0] + 1;
            valve.setQNumb(QNumb);
            valve.setHNumb(HNumb);
        }
    }

    private void UpdateShortPipeQHNumb() {  //给短管段中的QNumb和HNumb属性值赋值
        Integer startQNumb = 0;
        Integer startHNumb = halfSize;
        if (longPipes.size() != 0) {    //此时管网内有长管段
            LongPipe lastLongPipe = longPipes.get(longPipes.size() - 1);
            startQNumb = 1 + lastLongPipe.getLastQNumb();    //获取到最后一根长管段的最末端分段的QNumb编号
            startHNumb = 1 + lastLongPipe.getLastHNumb();   //获取到最后一根长管段的最末端分段的HNumb编号
        }
        for (int i = 0; i < shortPipes.size(); i++) {
            ShortPipe shortPipe = shortPipes.get(i);
            Integer[] QNumb = new Integer[2];
            Integer[] HNumb = new Integer[2];

            if (i == 0) {   //给短管段中的QNumb属性值赋值
                QNumb[0] = startQNumb;
                HNumb[0] = startHNumb;
            } else {
                ShortPipe shortPipeBefore = shortPipes.get(i - 1);
                Integer addQNumb = shortPipeBefore.getLastQNumb();
                QNumb[0] = addQNumb + 1;
                Integer addHNumb = shortPipeBefore.getLastHNumb();
                HNumb[0] = addHNumb + 1;
            }
            QNumb[1] = QNumb[0] + 1;
            HNumb[1] = HNumb[0] + 1;
            shortPipe.setQNumb(QNumb);
            shortPipe.setHNumb(HNumb);
        }
    }

    private void UpdateLongPipeQHNumb() {   //给长管段中的QNumb和HNumb属性值赋值
        for (int i = 0; i < longPipes.size(); i++) {
            LongPipe longPipe = longPipes.get(i);
            Integer[] QNumb = new Integer[longPipe.getSegments() + 1];
            Integer[] HNumb = new Integer[longPipe.getSegments()];

            if (i == 0) {
                for (int j = 0; j < QNumb.length; j++) {    //给管段中的QNumb属性值赋值
                    QNumb[j] = j;
                }
            } else {
                LongPipe longPipeBefore = longPipes.get(i - 1);
                Integer add = longPipeBefore.getLastQNumb();
                for (int j = 0; j < QNumb.length; j++) {    //给管段中的QNumb属性值赋值
                    QNumb[j] = (j+1) + add;
                }
            }
            longPipe.setQNumb(QNumb);

            if (i == 0) {
                for (int j = 0; j < HNumb.length; j++) {    //给管段中的HNumb属性值赋值
                    HNumb[j] = halfSize + j;
                }
            } else {
                LongPipe longPipeBefore = longPipes.get(i - 1);
                Integer add = longPipeBefore.getLastHNumb();
                for (int j = 0; j < HNumb.length; j++) {    //给管段中的HNumb属性值赋值
                    HNumb[j] = (j+1) + add;
                }
            }
            longPipe.setHNumb(HNumb);
        }
    }

}
