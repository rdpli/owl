// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Random;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum R2Demo {
  ;
  private static final Random RANDOM = new Random();

  public static void main(String[] args) {
    int wid = 7;
    Tensor min = Tensors.vector(0, 0);
    Tensor max = Tensors.vector(wid, wid);
    RrtsNodeCollection rrtsNodeCollection = new RrtsNodeCollections(RnRrtsNdType.INSTANCE, min, max);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.polygon1();
    // ---
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(min, max);
    for (int count = 0; count < 1000; ++count)
      rrts.insertAsNode(randomSampleInterface.randomSample(RANDOM), 15);
    System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.IDENTITY);
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(42, 456);
    owlyFrame.jFrame.setBounds(100, 100, 500, 500);
    owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
  }
}
