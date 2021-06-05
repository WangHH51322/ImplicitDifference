package top.codechap.simulation;

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
    public static void main(String[] args) {

    }

    public void run() {
        String fileName = "C:\\Users\\WangHH\\Desktop\\InputData.xlsx";
        Excel2Network excel2Network = new Excel2Network(fileName);
        NetWork netWork = excel2Network.getNetWork();
        netWork.init();
        FixedFunction fixedFunction = new FixedFunction(netWork);
    }
}
