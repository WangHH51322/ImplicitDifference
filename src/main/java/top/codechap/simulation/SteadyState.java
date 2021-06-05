package top.codechap.simulation;

import lombok.Data;
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
@Data
public class SteadyState {
    private Integer times;

    public void run() {
        for (int i = 0; i < times; i++) {

        }
    }
}
