import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * Created by m on 17.04.15.
 */
public class TriangleTest {
	@Test
	public void createTest() throws Exception {
		Graph G = new Graph();
		G.addEdge(1, 2, 1);
		G.addEdge(2, 3, 2);
		G.addEdge(1, 3, 3);

		System.out.println(G.getTriangles());
	}
}
