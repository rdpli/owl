// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class MerrienHermiteSubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    HermiteSubdivision hermiteSubdivision = MerrienHermiteSubdivision.string(control);
    Tensor iterate = hermiteSubdivision.iterate();
    Tensor expect = Tensors.fromString("{{0, 0}, {1/2, 3/2}, {1, 0}, {5/8, -5/4}, {0, -1}, {-1/8, 1/4}, {0, 0}}");
    assertEquals(iterate, expect);
    ExactTensorQ.require(iterate);
    iterate = hermiteSubdivision.iterate();
    // System.out.println(iterate);
    String string = "{{0, 0}, {5/32, 9/8}, {1/2, 3/2}, {27/32, 9/8}, {1, 0}, {57/64, -13/16}, {5/8, -5/4}, {19/64, -21/16}, {0, -1}, {-9/64, -3/16}, {-1/8, 1/4}, {-3/64, 5/16}, {0, 0}}";
    assertEquals(iterate, Tensors.fromString(string));
    ExactTensorQ.require(iterate);
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hermiteSubdivision = MerrienHermiteSubdivision.cyclic(control);
    Tensor iterate = hermiteSubdivision.iterate();
    Tensor expect = Tensors.fromString("{{0, 0}, {1/2, 3/2}, {1, 0}, {5/8, -5/4}, {0, -1}, {-1/2, -3/4}, {-1/2, 1}, {-1/8, 1/2}}");
    assertEquals(iterate, expect);
    ExactTensorQ.require(iterate);
  }
}
