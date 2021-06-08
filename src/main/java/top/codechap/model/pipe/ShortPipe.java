package top.codechap.model.pipe;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public List<Integer> getFirstHNumbs() {
        List<Integer> numbs = new ArrayList<>();
        numbs.add(getFirstHNumb());
        return numbs;
    }

    @Override
    public Integer getLastHNumb() {
        return getHNumb()[1];
    }

    @Override
    public List<Integer> getLastHNumbs() {
        List<Integer> numbs = new ArrayList<>();
        numbs.add(getLastHNumb());
        return numbs;
    }

    @Override
    public List<Integer> getFirstHRealNumb() {
        List<Integer> numbs = new ArrayList<>();
        numbs.add(getHRealNumb()[0]);
        return numbs;
    }

    @Override
    public List<Integer> getLastHRealNumb() {
        List<Integer> numbs = new ArrayList<>();
        numbs.add(getHRealNumb()[1]);
        return numbs;
    }

    @Override
    public List<Double> getStartCoefficient() {
        List<Double> coefficient = new ArrayList<>();
        coefficient.add(1.00);
        return coefficient;
    }

    @Override
    public List<Double> getEndCoefficient() {
        List<Double> coefficient = new ArrayList<>();
        coefficient.add(1.00);
        return coefficient;
    }

    @Override
    public Double getFirstQn(Double[] Qn) {
        return Qn[getFirstQNumb()];
    }

    @Override
    public Double getFirstHn(Double[] Hn) {
        return Hn[getFirstHRealNumb().get(0)];
    }

    @Override
    public Double getLastQn(Double[] Qn) {
        return Qn[getLastQNumb()];
    }

    @Override
    public Double getLastHn(Double[] Hn) {
        return Hn[getLastHRealNumb().get(0)];
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
