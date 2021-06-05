package top.codechap.model.pipe;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

/**
 * @author CodeChap
 * @date 2021-06-03 13:49
 * @description ShortPipe
 */
@Data
@AllArgsConstructor
public class ShortPipe extends Pipe{

    @Override
    public Integer getSegments() {
        return 1;
    }

    @Override
    public Double lastSegLength() {
        return getLength();
    }

    @Override
    public Double[] getAllSegLength() {
        Double[] segmentsLength = new Double[getSegments()];
        Arrays.fill(segmentsLength, getLength());
        return segmentsLength;
    }

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
        return "pipeNumb: " + getNumb() + "  " +
                "pipeStartNumb: " + getStartNumb() + "  " +
                "pipeEndNumb: " + getEndNumb() + "  " +
                "pipeLength: " + getLength() + "  " +
                "pipeOutsideDiameter: " + getOutsideDiameter() + "  " +
                "pipeThickness: " + getThickness() + "  " +
                "pipeRoughness: " + getRoughness() + "  " +
                "pipeE: " + getE() + "  " +
                "pipeC: " + getC() + "  ";
    }
}
