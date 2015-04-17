import java.util.*;

/**
 * Created by m on 17.04.15.
 */
public class Graph {

	Map<Integer, Vertex> G = new HashMap<Integer, Vertex>();

	public void addEdge(int u, int v, int order) {
		System.out.println("===========add Edge=============");
		System.out.println(u + " " + v + " " + order);
		Vertex V = G.get(u);
		if (V == null) {
			V = new Vertex();
			G.put(u, V);
		}
		switch(order) {
			case 1: V.firstLevel.add(v); break;
			case 2: V.secondLevel.add(v); break;
			case 3: V.thirdLevel.add(v); break;
		}
	}

	public List<String> getTriangles() {
		List <String> res = new ArrayList<String>();

		for( Map.Entry<Integer, Vertex> entry : G.entrySet() ) {
			int a = entry.getKey();
			Vertex aV = entry.getValue();
			List<Integer> aN = aV.firstLevel;
			for (int b : aN ) {
				Vertex bV = G.get(b);
				if (bV == null ){
					continue;
				}
				List<Integer> bN = bV.secondLevel;
				for (int c : bN) {
					if(aV.thirdLevel.contains(c)){
						res.add(a + " " + b + " " + c);
					}
				}
			}
		}
		return res;
	}

	class Vertex {
		// sasie	dzi bedacy poolaczeni krawedziami typu u v x
		List<Integer> firstLevel = new ArrayList();
		// x u v
		List<Integer> secondLevel = new ArrayList();
		// u x v (potrzebujemy tylko sprawdzac czy istnieje, nie iterujemy)
		Set<Integer> thirdLevel = new HashSet();
	}
}
