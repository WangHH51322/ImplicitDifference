package top.codechap.model.pipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.codechap.constant.Constant;

import java.util.Arrays;

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
    public Integer getLastHNumb() {
        Integer[] hNumb = getHNumb();
        Integer last = hNumb[hNumb.length - 1];
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
