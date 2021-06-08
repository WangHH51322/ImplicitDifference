package top.codechap;

import lombok.Data;
import top.codechap.model.node.Node;

import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-04 9:53
 * @description Element
 */
@Data
public abstract class Element {
    private Integer numb;   //元件编号
    private Integer startNumb;  //元件起点编号
    private Integer endNumb;  //元件终编号

    private Node startNode;
    private Node endNode;

    private Integer[] QNumb;    //系数矩阵中,元件Q所在的列编号,等同于Qn中的index
    private Integer[] HNumb;    //系数矩阵中,元件H所在的列编号
    private Integer[] HRealNumb;    //Hn中的index与HNumb中的列编号一一对应
    private Integer[] MomentumNumb;    //系数矩阵中,元件动量方程中的行编号
    private Integer outBoundaryConditionNumb;    //系数矩阵中,元件出口边界条件所在行编号
    private Integer[] MotionNumb;    //系数矩阵中,元件运动方程中的行编号
    private Integer inBoundaryConditionNumb;    //系数矩阵中,元件入口边界条件所在行编号

    public abstract Integer getFirstQNumb();    //获取QNumb中的第一个值
    public abstract Integer getLastQNumb();     //获取QNumb中的最后一个值
    public abstract Integer getFirstHNumb();    //获取HNumb中的第一个值
    public abstract List<Integer> getFirstHNumbs();    //获取HNumb中的第一个值
    public abstract Integer getLastHNumb();     //获取HNumb中的最后一个值
    public abstract List<Integer> getLastHNumbs();     //获取HNumb中的最后一个值
    public abstract List<Integer> getFirstHRealNumb();    //获取HNumb中的第一个值
    public abstract List<Integer> getLastHRealNumb();     //获取HNumb中的最后一个值

    public abstract List<Double> getStartCoefficient();     //长管段首端差分系数
    public abstract List<Double> getEndCoefficient();     //长管段末端差分系数

    public abstract Double getFirstQn(Double[] Qn);
    public abstract Double getFirstHn(Double[] Hn);
    public abstract Double getLastQn(Double[] Qn);
    public abstract Double getLastHn(Double[] Hn);

    public abstract Integer getLastMomentumNumb();     //获取MomentumNumb中的最后一个值
    public abstract Integer getLastMotionNumb();     //获取MomentumNumb中的最后一个值

    @Override
    public String toString() {
        return "elementNumb" + numb + "  " +
                "elementStartNumb" + startNumb + "  " +
                "elementEndNumb" + endNumb + "  " +
                "elementEndNumb" + endNumb + "  ";
    }
}
