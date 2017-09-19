package arauto;

public class Node {
	int x, y;
	double f, g;
	Node pai;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Node(int x, int y, double f, double g) {
		this.x = x;
		this.y = y;
		this.f = f;
		this.g = g;
	}
	
	@Override
	public boolean equals(Object obj) {
		Node node = (Node) obj;
		return node.x == this.x && node.y == this.y;
	}

}
