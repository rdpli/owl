// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class Se2BarycenterDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2BarycenterDemo());
  }
}
