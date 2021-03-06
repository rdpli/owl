// code by jph
package ch.ethz.idsc.sophus.app.ob;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV1;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LieGroupFiltersTest extends TestCase {
  private static void _check(GokartPoseData gokartPoseData) {
    List<String> lines = gokartPoseData.list();
    Tensor control = gokartPoseData.getPose(lines.get(0), 250);
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieExponential lieExponential = geodesicDisplay.lieExponential();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    int radius = 7;
    Map<LieGroupFilters, Tensor> map = new EnumMap<>(LieGroupFilters.class);
    for (LieGroupFilters lieGroupFilters : LieGroupFilters.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          lieGroupFilters.supply(geodesicInterface, smoothingKernel, lieGroup, lieExponential, biinvariantMean);
      Tensor filtered = CenterFilter.of(tensorUnaryOperator, radius).apply(control);
      map.put(lieGroupFilters, filtered);
    }
    for (LieGroupFilters lieGroupFilters : LieGroupFilters.values()) {
      Tensor diff = map.get(lieGroupFilters).subtract(map.get(LieGroupFilters.BIINVARIANT_MEAN));
      diff.set(So2.MOD, Tensor.ALL, 2);
      Scalar norm = Norm.INFINITY.ofMatrix(diff);
      assertTrue(Chop._02.allZero(norm));
    }
  }

  public void testSimple() {
    _check(GokartPoseDataV1.INSTANCE);
    _check(GokartPoseDataV2.INSTANCE);
  }

  public void testTiming() {
    String name = "20190701T170957_06";
    Tensor control = GokartPoseDataV2.RACING_DAY.getPose(name, 1_000_000);
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieExponential lieExponential = geodesicDisplay.lieExponential();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    for (int radius : new int[] { 0, 10 }) {
      for (LieGroupFilters lieGroupFilters : LieGroupFilters.values()) {
        TensorUnaryOperator tensorUnaryOperator = //
            lieGroupFilters.supply(geodesicInterface, smoothingKernel, lieGroup, lieExponential, biinvariantMean);
        Timing timing = Timing.started();
        CenterFilter.of(tensorUnaryOperator, radius).apply(control);
        timing.stop();
        // System.out.println(lieGroupFilters+" "+timing.seconds());
      }
    }
  }
}
