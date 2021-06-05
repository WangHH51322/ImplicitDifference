package top.codechap.model.liquid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CodeChap
 * @date 2021-06-03 15:14
 * @description AviationKerosene
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AviationKerosene {
    private Double rou;
    private Double temperature;
    private Double K;
}
