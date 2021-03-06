// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Numerical Recipes 3rd Edition Section 17.3.1 */
public class ModifiedMidpointIntegrator implements Integrator, Serializable {
  /** @param n strictly positive
   * @return */
  public static Integrator of(int n) {
    return new ModifiedMidpointIntegrator(Integers.requirePositive(n));
  }

  // ---
  private final int n;

  private ModifiedMidpointIntegrator(int n) {
    this.n = n;
  }

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x0, Scalar H) {
    Scalar h = H.divide(RealScalar.of(n));
    Tensor xm = x0.add(flow.at(x0).multiply(h)); // line identical with MidpointIntegrator
    for (int m = 1; m < n; ++m) {
      Scalar _2h = h.add(h);
      Tensor x1 = x0.add(flow.at(xm).multiply(_2h));
      x0 = xm;
      xm = x1;
    }
    return x0.add(flow.at(xm).multiply(h)); // TODO line almost identical with MidpointIntegrator !?
  }
}
