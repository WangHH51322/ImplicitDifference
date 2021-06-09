package top.codechap.model.node;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-03 13:13
 * @description Node
 */
@Data
@AllArgsConstructor
public class Node {

    private Integer numb;   //节点编号
    private Double flow;    //节点流量
    private Double pressure;    //节点压力
    private Boolean flag;   //节点是否使用过流量平衡方程

    private Integer type;   //0 入口 ,   1 出口,   2 中间节点
    private Integer connectionType; //10 与一个元件相连,作为元件入口;     11 与一个元件相连,作为元件出口;
                                    //200 与两个元件相连,作为两个元件的入口;   210 与两个元件相连,作为一入一出;  211 与两个元件相连,作为两个元件的出口;
    //3000 与三个元件相连,作为三个元件的入口;   3100 与三个元件相连,作为二入一出;    3110 与三个元件相连,作为一入二出;    3111 与三个元件相连,作为三个元件的出口;

    private List<Integer> connectionQNumb;  //系数矩阵中,节点所连接的元件,Q所在的列编号
    private List<Integer> connectionHNumb;  //系数矩阵中,节点所连接的元件,H所在的列编号

    private List<Element> elements; //系数矩阵中,与节点所连接的所有元件
    private List<Element> inElements;   //系数矩阵中,作为元件入口的节点的元件,此节点是元件的出口,作为元件的终点
    private List<Element> outElements;   //系数矩阵中,作为元件出口的节点的元件,此节点是元件的入口,作为元件的起点

    public Node() {
        this.elements = new ArrayList<>();
        this.inElements = new ArrayList<>();
        this.outElements = new ArrayList<>();
        this.flag = false;
    }

    public Boolean isUsed() {
        return flag;
    }


    public void addElements(Element element) {
        elements.add(element);
    }
    public void addInElements(Element element) {
        inElements.add(element);
    }
    public void addOutElements(Element element) {
        outElements.add(element);
    }


    public Integer nodeConnectionType() {   //返回节点连接类型
        if (elements.size() == 1) {
            if (inElements.size() == 1) {
                return 11;
            } else {
                return 10;
            }
        } else if (elements.size() == 2) {
            if (inElements.size() == 2) {
                return 211;
            } else if (inElements.size() == 1) {
                return 210;
            } else {
                return 200;
            }
        } else if (elements.size() == 3) {
            if (inElements.size() == 3) {
                return 3111;
            } else if (inElements.size() == 2) {
                return 3110;
            } else if (inElements.size() == 1) {
                return 3100;
            } else {
                return 3000;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "nodeNumb: " + numb + "  " +
                "nodeType: " + type + "  " +
                "nodeFlow: " + flow + "  " +
                "nodePressure: " + pressure + "  ";
    }
}
