package top.codechap.model.valve;

/**
 * @author CodeChap
 * @date 2021-06-03 16:11
 * @description RegulatingValve
 */
public class RegulatingValve extends Valve{
    @Override
    public Integer getFirstQNumb() {
        return getQNumb()[0];
    }

    @Override
    public Integer getLastQNumb() {
        return getQNumb()[1];
    }

    @Override
    public Integer getFirstHNumb() {
        return getHNumb()[0];
    }

    @Override
    public Integer getLastHNumb() {
        return getHNumb()[1];
    }

    @Override
    public Integer getLastMomentumNumb() {
        return getMomentumNumb()[0];
    }

    @Override
    public Integer getLastMotionNumb() {
        return getMotionNumb()[0];
    }

    @Override
    public String toString() {
        return "valveNumb: " + getNumb() + "  " +
                "valveStartNumb: " + getStartNumb() + "  " +
                "valveEndNumb: " + getEndNumb() + "  ";
    }
}
