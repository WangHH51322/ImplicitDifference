package top.codechap.model.pipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.codechap.constant.Constant;

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
public class LongPipe extends Pipe{

    @Override
    public Integer getSegments() {
        int segments;
        int n = (int) Math.floor(getLength() / Constant.SEGMENT_LENGTH);
        double lastSegLength = getLength() - n * Constant.SEGMENT_LENGTH;
        if (lastSegLength < Constant.SEGMENT_LENGTH / 2) {
            segments = n;
        } else {
            segments = n + 1;
        }
        return segments;
    }

    @Override
    public Double lastSegLength() {
        int n = (int) Math.floor(getLength() / Constant.SEGMENT_LENGTH);
        double lastSegLength = getLength() - n * Constant.SEGMENT_LENGTH;
        if (lastSegLength < Constant.SEGMENT_LENGTH / 2) {
            lastSegLength = Constant.SEGMENT_LENGTH + lastSegLength;
        }
        return lastSegLength;
    }

    @Override
    public Double[] getAllSegLength() {
        Double[] segmentsLength = new Double[getSegments()];
        Arrays.fill(segmentsLength, Constant.SEGMENT_LENGTH);
        segmentsLength[segmentsLength.length-1] = lastSegLength();
        return segmentsLength;
    }

    @Override
    public Integer getFirstQNumb() {
        Integer[] qNumb = getQNumb();
        Integer first = qNumb[0];
        return first;
    }

    @Override
    public Integer getLastQNumb() {
        Integer[] qNumb = getQNumb();
        Integer last = qNumb[qNumb.length - 1];
        return last;
    }

    @Override
    public Integer getFirstHNumb() {
        Integer[] hNumb = getHNumb();
        Integer first = hNumb[0];
        return first;
    }

    @Override
    public List<Integer> getFirstHNumbs() {
        List<Integer> numbs = new ArrayList<>();
        Integer[] hNumb = getHNumb();
        Integer first = hNumb[0];
        Integer second = first + 1;
        numbs.add(first);
        numbs.add(second);
        return numbs;
    }

    @Override
    public Integer getLastHNumb() {
        Integer[] hNumb = getHNumb();
        Integer last = hNumb[hNumb.length - 1];
        return last;
    }

    @Override
    public List<Integer> getLastHNumbs() {
        List<Integer> numbs = new ArrayList<>();
        Integer[] hNumb = getHNumb();
        Integer last = hNumb[hNumb.length - 1];
        Integer lastBefore = last - 1;
        numbs.add(lastBefore);
        numbs.add(last);
        return numbs;
    }

    @Override
    public List<Integer> getFirstHRealNumb() {
        List<Integer> numbs = new ArrayList<>();
        Integer[] hRealNumb = getHRealNumb();
        Integer first = hRealNumb[0];
        Integer second = first + 1;
        numbs.add(first);
        numbs.add(second);
        return numbs;
    }

    @Override
    public List<Integer> getLastHRealNumb() {
        List<Integer> numbs = new ArrayList<>();
        Integer[] hRealNumb = getHRealNumb();
        Integer last = hRealNumb[hRealNumb.length - 1];
        Integer lastBefore = last - 1;
        numbs.add(lastBefore);
        numbs.add(last);
        return numbs;
    }

    @Override
    public List<Double> getStartCoefficient() {
        List<Double> coefficient = new ArrayList<>();
        coefficient.add(1.5);
        coefficient.add(-0.5);
        return coefficient;
    }

    @Override
    public List<Double> getEndCoefficient() {
        List<Double> coefficient = new ArrayList<>();
        Double coefficientOne = - lastSegLength() / (Constant.SEGMENT_LENGTH + lastSegLength());
        Double coefficientTwo = (Constant.SEGMENT_LENGTH + 2*lastSegLength()) / (Constant.SEGMENT_LENGTH + lastSegLength());
        coefficient.add(coefficientOne);
        coefficient.add(coefficientTwo);
        return coefficient;
    }


    @Override
    public Double getFirstQn(double[] Qn) {
        return Qn[getFirstQNumb()];
    }

    @Override
    public Double getFirstHn(double[] Hn) {
        List<Integer> firstHRealNumb = getFirstHRealNumb();
        Double H2 = Hn[firstHRealNumb.get(0)];
        Double H3 = Hn[firstHRealNumb.get(1)];
        List<Double> startCoefficient = getStartCoefficient();
        Double H1 = startCoefficient.get(0)*H2 + startCoefficient.get(1) * H3;
        return H1;
    }

    @Override
    public Double getLastQn(double[] Qn) {
        return Qn[getLastQNumb()];
    }

    @Override
    public Double getLastHn(double[] Hn) {
        List<Integer> lastHRealNumb = getLastHRealNumb();
        Double HLastBeforeTwo = Hn[lastHRealNumb.get(0)];
        Double HLastBeforeOne = Hn[lastHRealNumb.get(1)];
        List<Double> endCoefficient = getEndCoefficient();
        Double HLast = endCoefficient.get(0) * HLastBeforeTwo + endCoefficient.get(1) * HLastBeforeOne;
        return HLast;
    }

    @Override
    public Integer getLastMomentumNumb() {
        Integer[] momentumNumb = getMomentumNumb();
        Integer last = momentumNumb[momentumNumb.length - 1];
        return last;
    }

    @Override
    public Integer getLastMotionNumb() {
        Integer[] motionNumb = getMotionNumb();
        Integer last = motionNumb[motionNumb.length - 1];
        return last;
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
