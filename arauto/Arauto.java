package arauto;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.mobilerobots.Aria.ArActionAvoidFront;
import com.mobilerobots.Aria.ArActionAvoidSide;
import com.mobilerobots.Aria.ArActionBumpers;
import com.mobilerobots.Aria.ArActionGoto;
import com.mobilerobots.Aria.ArActionStallRecover;
import com.mobilerobots.Aria.ArPose;
import com.mobilerobots.Aria.ArPoseWithTime;
import com.mobilerobots.Aria.ArPoseWithTimeVector;
import com.mobilerobots.Aria.ArRobot;
import com.mobilerobots.Aria.ArSimpleConnector;
import com.mobilerobots.Aria.ArSonarDevice;
import com.mobilerobots.Aria.Aria;

public class Arauto {

	static Node destino;

	static int[][] nearby = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
	static PriorityQueue<Node> fronteira = new PriorityQueue<>(new HeapComparator());
	static List<Node> nosVisitados = new ArrayList<Node>();
	static List<Node> caminho = new ArrayList<Node>();

	public static Node arauto(List<Node> sensorReadingList) {

		Node atual = null;
		Node vizinho;
		if (!sensorReadingList.isEmpty()) {
			atual = fronteira.poll();

			if (atual == destino) {
				caminho.add(atual);
				nosVisitados.add(atual);
			}
			else {
				for (int[] i : nearby) {
					vizinho = new Node(atual.x + i[0], atual.y + i[1]);
					vizinho.g = atual.g + getDistancia(atual, vizinho);
					vizinho.f = vizinho.g + getDistancia(vizinho, destino);
					if (!(nosVisitados.contains(vizinho) || fronteira.contains(vizinho))) {
						fronteira.add(vizinho);
					}
				}
			}
		}
		return atual;
	}

	private static double getDistancia(Node a, Node b) {
		return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
	}
	
	public static Node convertePoseNode(ArPose pose) {
		return new Node((int)(pose.getX()/100), (int)(pose.getY()/100));
	}
	
	public static ArPose converteNodePose(Node node) {
		return new ArPose(node.x*100, node.y*100);
	}
	
	public static Node convertePoseTimeNode(ArPoseWithTime pose) {
		return new Node((int)(pose.getX()/100), (int)(pose.getY()/100));
	}


	public static void main(String[] args) {
		String coordenadaRobo = "2352 5731 12.1";
		String coordenadaDestino = "9000 9000";
		
		/*if (args.length > 0) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(args[0]));
				coordenadaRobo = br.readLine();
				coordenadaDestino = br.readLine();
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		String[] coordenadasRobo = coordenadaRobo.split(" ");
		String[] coordenadasDestino = coordenadaDestino.split(" ");
		
		Aria.init();
		ArRobot robot = new ArRobot();
		ArSimpleConnector conn = new ArSimpleConnector(args);
		ArSonarDevice sonar = new ArSonarDevice();
		robot.addRangeDevice(sonar);
		
		robot.moveTo(new ArPose(Double.parseDouble(coordenadasRobo[0]), Double.parseDouble(coordenadasRobo[1])));
		
		
		ArActionStallRecover recover = new ArActionStallRecover();
		ArActionBumpers bumpers = new ArActionBumpers();
		ArActionAvoidFront avoidFront = new ArActionAvoidFront();
		ArActionAvoidSide avoidSide = new ArActionAvoidSide();
		ArActionGoto actionGoto = new ArActionGoto();
		
		robot.addAction(recover, 100);
		robot.addAction(bumpers, 80);
		robot.addAction(avoidFront, 60);
		robot.addAction(avoidSide, 40);
		robot.addAction(actionGoto, 20);
		
		actionGoto.setGoal(new ArPose(Double.parseDouble(coordenadasDestino[0]), Double.parseDouble(coordenadasDestino[1])));
		
		if(!Aria.parseArgs()){
			Aria.logOptions();
			Aria.exit(1);
		}

		if (!conn.connectRobot(robot)){
			System.err.println("Could not connect to robot, exiting.\n");
			System.exit(1);
		}
		
		robot.runAsync(true);
		
		List<Node> sensorReadingList;
		Node novoDestino;
		while(Aria.getRunning()) {
			if (actionGoto.haveAchievedGoal()) {
				Aria.exit();
			}
			
			robot.lock();
			
			sensorReadingList = new ArrayList<Node>();
			ArPoseWithTimeVector apwtv = sonar.getCurrentBufferAsVector();
			for(int i = 0; i < apwtv.size(); i++) {
				sensorReadingList.add(convertePoseTimeNode(apwtv.get(i)));
			}
			
			novoDestino = arauto(sensorReadingList);
			
			if (novoDestino != null) {
				actionGoto.setGoal(converteNodePose(novoDestino));
			}
			
		}
	}
}
