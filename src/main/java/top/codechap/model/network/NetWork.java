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
    private Integer QnSize;
    private Integer HnSize;
    private Integer HnRealSize;

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
        CalculateQHAndMMNumb();
        ConnectElementAndNode();
    }

    private void ConnectElementAndNode() {   //根据元件的起终点编号将其与对应的节点关联起来;
        if (longPipes.size() != 0) {
            for (LongPipe longPipe : longPipes) {
                ConnectEAndN(longPipe);
            }
        }
        if (shortPipes.size() != 0) {
            for (ShortPipe shortPipe : shortPipes) {
                ConnectEAndN(shortPipe);
            }
        }
        if (regValves.size() != 0) {
            for (RegulatingValve regValve : regValves) {
                ConnectEAndN(regValve);
            }
        }
    }

    private void CalculateMatrixSize() { //计算整个系数矩阵大小,和halfSize大小
        int sum = 0;
        for (LongPipe longPipe : longPipes) {
            sum += longPipe.getSegments();
        }
        halfSize = sum + longPipes.size() + 2 * (shortPipes.size() + regValves.size());
        QnSize = sum + longPipes.size() + 2 * (shortPipes.size() + regValves.size());
        HnRealSize = sum + 2 * (longPipes.size() + shortPipes.size() + regValves.size());
        HnSize = sum + 2 * (shortPipes.size() + regValves.size());
        sum = QnSize + HnSize;
        matrixSize = sum;
    }

    private void CalculateQHAndMMNumb() {  //给元件中的QNumb,HNumb;MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        if (longPipes.size() != 0) {
            UpdateLongPipeQHNumb(); //给长管段中的QNumb和HNumb属性值赋值
            UpdateLongPipeMMNumb(); //给长管段中的MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        }

        if (shortPipes.size() != 0) {
            UpdateShortPipeQHNumb();    //给短管段中的QNumb和HNumb属性值赋值
            UpdateShortPipeMMNumb();    //给短管段中的MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        }

        if (regValves.size() != 0) {
            UpdateRegValveQHNumb(); //给阀门中的QNumb和HNumb属性值赋值
            UpdateRegValveMMNumb(); //给阀门中的MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        }
    }

    private void ConnectEAndN(Element element) {
        Integer startNumb = element.getStartNumb();
        Node startNode = nodes.get(startNumb - 1);
        startNode.addElements(element);
        startNode.addOutElements(element);
        element.setStartNode(startNode);

        Integer endNumb = element.getEndNumb();
        Node endNode = nodes.get(endNumb - 1);
        endNode.addElements(element);
        endNode.addInElements(element);
        element.setEndNode(endNode);
    }

    private void UpdateRegValveMMNumb() {   //给阀门中的MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        Integer startMomentumNumb = 0;
        Integer startMotionNumb = halfSize;
        if (shortPipes.size() != 0) {    //此时管网内有短管段
            ShortPipe lastShortPipe = shortPipes.get(shortPipes.size() - 1);
            startMomentumNumb = 1 + lastShortPipe.getOutBoundaryConditionNumb();    //获取到最后一根短管段的最末端分段的outBoundaryConditionNumb编号
            startMotionNumb = 2 + lastShortPipe.getLastMotionNumb();   //获取到最后一根短管段的最末端分段的MotionNumb编号
        } else if (longPipes.size() != 0) {    //此时管网内有长管段但没有短管道
            LongPipe lastLongPipe = longPipes.get(longPipes.size() - 1);
            startMomentumNumb = 1 + lastLongPipe.getOutBoundaryConditionNumb();    //获取到最后一根长管段的最末端分段的outBoundaryConditionNumb编号
            startMotionNumb = 2 + lastLongPipe.getLastMotionNumb();   //获取到最后一根长管段的最末端分段的MotionNumb编号
        }

        for (int i = 0; i < regValves.size(); i++) {
            RegulatingValve valve = regValves.get(i);
            Integer[] MomentumNumb = new Integer[1];
            Integer outBoundaryConditionNumb;
            Integer[] MotionNumb = new Integer[1];
            Integer inBoundaryConditionNumb;
            if (i == 0) {
                MomentumNumb[0] = startMomentumNumb;
                MotionNumb[0] = startMotionNumb;
            } else {
                RegulatingValve regValveBefore = regValves.get(i - 1);
                Integer addMomentumNumb = regValveBefore.getOutBoundaryConditionNumb();
                MomentumNumb[0] = addMomentumNumb + 1;
                Integer addMotionNumb = regValveBefore.getLastMotionNumb();
                MotionNumb[0] = addMotionNumb + 2;
            }
            outBoundaryConditionNumb = MomentumNumb[0] + 1;
            inBoundaryConditionNumb = MotionNumb[0] - 1;
            valve.setMomentumNumb(MomentumNumb);
            valve.setMotionNumb(MotionNumb);
            valve.setOutBoundaryConditionNumb(outBoundaryConditionNumb);
            valve.setInBoundaryConditionNumb(inBoundaryConditionNumb);
        }
    }

    private void UpdateShortPipeMMNumb() {  //给短管段中的MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        Integer startMomentumNumb = 0;
        Integer startMotionNumb = halfSize;
        if (longPipes.size() != 0) {    //此时管网内有长管段
            LongPipe lastLongPipe = longPipes.get(longPipes.size() - 1);
            startMomentumNumb = 1 + lastLongPipe.getOutBoundaryConditionNumb();    //获取到最后一根长管段的最末端分段的outBoundaryConditionNumb编号
            startMotionNumb = 2 + lastLongPipe.getLastMotionNumb();   //获取到最后一根长管段的最末端分段的MotionNumb编号
        }
        for (int i = 0; i < shortPipes.size(); i++) {
            ShortPipe shortPipe = shortPipes.get(i);
            Integer[] MomentumNumb = new Integer[1];
            Integer outBoundaryConditionNumb;
            Integer[] MotionNumb = new Integer[1];
            Integer inBoundaryConditionNumb;
            if (i == 0) {
                MomentumNumb[0] = startMomentumNumb;
                MotionNumb[0] = startMotionNumb;
            } else {
                ShortPipe shortPipeBefore = shortPipes.get(i - 1);
                Integer addMomentumNumb = shortPipeBefore.getOutBoundaryConditionNumb();
                MomentumNumb[0] = addMomentumNumb + 1;
                Integer addMotionNumb = shortPipeBefore.getLastMotionNumb();
                MotionNumb[0] = addMotionNumb + 2;
            }
            outBoundaryConditionNumb = MomentumNumb[0] + 1;
            inBoundaryConditionNumb = MotionNumb[0] - 1;
            shortPipe.setMomentumNumb(MomentumNumb);
            shortPipe.setMotionNumb(MotionNumb);
            shortPipe.setOutBoundaryConditionNumb(outBoundaryConditionNumb);
            shortPipe.setInBoundaryConditionNumb(inBoundaryConditionNumb);
        }
    }

    private void UpdateLongPipeMMNumb() {   //给长管段中的MomentumNumb,MotionNumb,outBoundaryConditionNumb,inBoundaryConditionNumb属性赋值
        for (int i = 0; i < longPipes.size(); i++) {
            LongPipe longPipe = longPipes.get(i);
            Integer[] MomentumNumb = new Integer[longPipe.getSegments()];
            Integer outBoundaryConditionNumb;
            Integer[] MotionNumb = new Integer[longPipe.getSegments() - 1];
            Integer inBoundaryConditionNumb;
            if (i == 0) {
                for (int j = 0; j < MomentumNumb.length; j++) {
                    MomentumNumb[j] = j;
                }
            } else {
                LongPipe longPipeBefore = longPipes.get(i - 1);
                Integer add = longPipeBefore.getOutBoundaryConditionNumb();
                for (int j = 0; j < MomentumNumb.length; j++) {
                    MomentumNumb[j] = (j+1) + add;
                }
            }
            outBoundaryConditionNumb = MomentumNumb[MomentumNumb.length - 1] + 1;
            longPipe.setMomentumNumb(MomentumNumb);
            longPipe.setOutBoundaryConditionNumb(outBoundaryConditionNumb);

            if (i == 0) {
                for (int j = 0; j < MotionNumb.length; j++) {
                    MotionNumb[j] = halfSize + j + 1;
                }
            } else {
                LongPipe longPipeBefore = longPipes.get(i - 1);
                Integer add = longPipeBefore.getLastMotionNumb();
                for (int j = 0; j < MotionNumb.length; j++) {
                    MotionNumb[j] = (j+1) + (add+1) ;
                }
            }
            inBoundaryConditionNumb = MotionNumb[0] - 1;
            longPipe.setMotionNumb(MotionNumb);
            longPipe.setInBoundaryConditionNumb(inBoundaryConditionNumb);
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
            Integer[] HRealNumb = new Integer[2];

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
            for (int j = 0; j < HNumb.length; j++) {    //给管段中的HRealNumb属性值赋值
                HRealNumb[j] = HNumb[j] - halfSize;
            }
            valve.setHRealNumb(HRealNumb);
            valve.setQNumb(QNumb);
            valve.setHNumb(HNumb);
        }
    }

    private void UpdateShortPipeQHNumb()  {  //给短管段中的QNumb和HNumb属性值赋值
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
            Integer[] HRealNumb = new Integer[2];

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
            for (int j = 0; j < HNumb.length; j++) {    //给管段中的HRealNumb属性值赋值
                HRealNumb[j] = HNumb[j] - halfSize;
            }
            shortPipe.setHRealNumb(HRealNumb);
            shortPipe.setQNumb(QNumb);
            shortPipe.setHNumb(HNumb);
        }
    }

    private void UpdateLongPipeQHNumb() {   //给长管段中的QNumb和HNumb属性值赋值
        for (int i = 0; i < longPipes.size(); i++) {
            LongPipe longPipe = longPipes.get(i);
            Integer[] QNumb = new Integer[longPipe.getSegments() + 1];
            Integer[] HNumb = new Integer[longPipe.getSegments()];
            Integer[] HRealNumb = new Integer[longPipe.getSegments()];

            if (i == 0) {
                for (int j = 0; j < QNumb.length; j++) {    //给第一根管段中的QNumb属性值赋值
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
                    HRealNumb[j] = j;
                }
            } else {
                LongPipe longPipeBefore = longPipes.get(i - 1);
                Integer add = longPipeBefore.getLastHNumb();
                for (int j = 0; j < HNumb.length; j++) {    //给管段中的HNumb属性值赋值
                    HNumb[j] = (j+1) + add;
                }
            }
            for (int j = 0; j < HNumb.length; j++) {    //给管段中的HRealNumb属性值赋值
                HRealNumb[j] = HNumb[j] - halfSize;
            }
            longPipe.setHRealNumb(HRealNumb);
            longPipe.setHNumb(HNumb);

        }
    }

}
