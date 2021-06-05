package top.codechap.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author CodeChap
 * @date 2021-06-03 13:41
 * @description Constant
 */
@Data
@AllArgsConstructor
public class Constant {
    public static final Double G = 9.81;
    public static final Double SEGMENT_LENGTH = 8.00;   //默认的管段分段长度
    public static final Double PIPE_LENGTH = 20.00;   //默认的最小管段长度
    public static final Double STEADY_STATE_T = 10.00;   //稳态时步
}
