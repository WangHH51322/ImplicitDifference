package top.codechap.model.valve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.Element;
import top.codechap.model.node.Node;

/**
 * @author CodeChap
 * @date 2021-06-03 16:07
 * @description Valve
 */
@Data
@AllArgsConstructor
//@NoArgsConstructor
public abstract class Valve extends Element {
//    private Integer numb;
//
//    private Integer startNumb;  //阀门起点编号
//    private Integer endNumb;  //阀门终编号
//
//    private Node startNode;
//    private Node endNode;
//
//    private Integer[] QNumb;    //系数矩阵中,阀门Q所在的列编号
//    private Integer[] HNumb;    //系数矩阵中,阀门H所在的列编号
}
