package top.codechap.model.pipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.codechap.Element;
import top.codechap.model.node.Node;

/**
 * @author CodeChap
 * @date 2021-06-03 13:11
 * @description pipe
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Pipe extends Element {

//    private Integer numb;   //管段编号
//    private Integer startNumb;  //管段起点编号
//    private Integer endNumb;  //管段终编号
    private Double length;  //管段长度
    private Double outsideDiameter; //外径
    private Double thickness;   //壁厚
    private Double roughness;   //粗糙度
    private Double E;   //管材弹性模量
    private Double C;
////    private Double segLength;   //分段长度

//    private Node startNode; //管段起点节点
//    private Node endNode;   //管段终点节点
//
//    private Integer[] QNumb;    //系数矩阵中,管段Q所在的列编号
//    private Integer[] HNumb;    //系数矩阵中,管段H所在的列编号

    public Double insideDiameter() {    //管段内径
        return outsideDiameter - 2*thickness;
    }
    public Double area() {  //管段横截面积
        return Math.PI * insideDiameter() * insideDiameter() / 4;
    }

    public abstract Integer getSegments();  //管段分段数
    public abstract Double lastSegLength(); //最后一个分段的长度
    public abstract Double[] getAllSegLength();    //获取管段各分段的长度
}
