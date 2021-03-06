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
public class AviationKerosene {
    private Double rou;
    private Double temperature;
    private Double K;
    private Double viscosity;

    public AviationKerosene() {
        this.rou = 800.00;
        this.K = 1360000000.00;
        this.viscosity = 2.5 * Math.pow(10,-6);
    }
}
