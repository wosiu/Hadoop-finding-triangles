import org.junit.Test;

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
