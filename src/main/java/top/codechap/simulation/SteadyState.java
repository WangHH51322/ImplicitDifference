package top.codechap.simulation;

import lombok.Data;
import top.codechap.constant.Constant;
import top.codechap.equations.FixedFunction;
import top.codechap.model.network.NetWork;
import top.codechap.model.node.Node;
import top.codechap.model.pipe.LongPipe;
import top.codechap.model.pipe.ShortPipe;
import top.codechap.utils.Excel2Network;

import java.util.List;

/**
 * @author CodeChap
 * @date 2021-06-04 15:28
 * @description SteadyState
 */
public class SteadyState {

    private Integer times;
    private double[] b;
    private double[] x;
    private double[][] CoefficientMatrix;


    public void run() {
        int calculateTimes = 0;
        while (calculateTimes <= times) {

        }
        for (int i = 0; i < times; i++) {


            //计算完成之后,需要使用fixFunction重新setHn与setQn,setB
        }
    }

    public void setTimes(Double steadyStateTime) {
        double times = steadyStateTime / Constant.STEADY_STATE_T;
        this.times = (int) times;
    }
}
