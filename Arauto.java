package arauto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.mobilerobots.Aria.ArUtil;
import com.mobilerobots.Aria.Aria;



public class Arauto {
	static {
    try {
        System.loadLibrary("AriaJava");
    } catch (UnsatisfiedLinkError e) {
		//System.err.println("Native code library libAriaJava failed to load. Make sure that its directory is in your library path; See javaExamples/README.txt and the chapter on Dynamic Linking Problems in the SWIG Java documentation (http://www.swig.org) for help.\n" + e);
		System.exit(1);
    }
  }

	static Node destino;

	static int[][] nearby = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
	//static PriorityQueue<Node> fronteira = new PriorityQueue<Node>(new HeapComparator());
	static List<Node> fronteira = new ArrayList<Node>();
	static List<Node> nosVisitados = new ArrayList<Node>();
	static List<Node> caminho = new ArrayList<Node>();
		

	public static Node arauto(List<Node> sensorReadingList) {
		
		ordenaLista(fronteira);
		
		System.out.println("HERALD: Ordena lista funfando");
		
		Node atual = null;
		Node vizinho;
		
		for (Node n : sensorReadingList) {
			System.out.println("###NODE SENSOR:"+n.x+", "+n.y);
		}
		
		for (Node n : fronteira) {
			System.out.println("###FRONTEIRA:"+n.x+", "+n.y+", "+n.f);
		}
		
		
		if (!sensorReadingList.isEmpty()) {

			System.out.println("HERALD: sensorReadingList nao ta vazio");
			atual = fronteira.remove(0);

			System.out.println("HERALD: Remove o atual da fronteira");
			nosVisitados.add(atual);

			System.out.println("HERALD: adiciona o atual aos visitados");
			
			System.out.println();

			if (atual.equals(destino)) {
				caminho.add(atual);

				System.out.println("HERALD: Destino encontrado");
			}
			else {
				for (int[] i : nearby) {
					
					vizinho = new Node(atual.x + i[0], atual.y + i[1]);
					vizinho.g = atual.g + getDistancia(atual, vizinho);
					vizinho.f = vizinho.g + getDistancia(vizinho, destino);
					
					System.out.println(nosVisitados.contains(vizinho));
					if (nosVisitados.contains(vizinho)){
						continue;
					}
					
					if (!fronteira.contains(vizinho)) {
						fronteira.add(vizinho);
					}
				}

				System.out.println("HERALD: Entrou no else");
			}
		}
		System.out.println(atual);
		return atual;
	}
	
	private static void ordenaLista(List<Node> lista){
		
		Collections.sort(lista, new Comparator<Node>() {
	        @Override
	        public int compare(Node a, Node b)
	        {
	        	if (a.f > b.f) {
	        		return 1;
	        	}
	        	else if(a.f < b.f) {
	        		return -1;
	        	}
	        	else {
	        		return 0;
	        	}
	        }
	    });
	}

	private static double getDistancia(Node a, Node b) {
		return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
	}
	
	public static Node convertePoseNode(ArPose pose) {
		return new Node((int)(pose.getX()/1000), (int)(pose.getY()/1000));
	}
	
	public static ArPose converteNodePose(Node node) {
		return new ArPose(node.x*1000, node.y*1000);
	}
	
	public static Node convertePoseTimeNode(ArPoseWithTime pose) {
		return new Node((int)(pose.getX()/1000), (int)(pose.getY()/1000));
	}


	public static void main(String args[]) {
		String coordenadaRobo = "1000 1000 0";
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
		
		fronteira.add(convertePoseNode(new ArPose(Double.parseDouble(coordenadasRobo[0]), Double.parseDouble(coordenadasRobo[1]))));
		
		
		ArActionStallRecover recover = new ArActionStallRecover();
		ArActionBumpers bumpers = new ArActionBumpers();
		ArActionAvoidFront avoidFront = new ArActionAvoidFront("avoid front obstacles", 525, 200, 5, true);
		ArActionAvoidSide avoidSide = new ArActionAvoidSide("Avoid side", 150, 2);
		ArActionGoto actionGoto = new ArActionGoto();
		
		robot.addAction(recover, 100);
		robot.addAction(bumpers, 80);
		robot.addAction(avoidFront, 60);
		robot.addAction(avoidSide, 40);
		robot.addAction(actionGoto, 20);
		
		actionGoto.setGoal(new ArPose(Double.parseDouble(coordenadasDestino[0]), Double.parseDouble(coordenadasDestino[1])));
		destino = convertePoseNode(new ArPose(Double.parseDouble(coordenadasDestino[0]), Double.parseDouble(coordenadasDestino[1])));
		
		if(!Aria.parseArgs()){
			Aria.logOptions();
			Aria.exit();
			System.out.println("logou");
		}

		if (!conn.connectRobot(robot)){
			//System.err.println("Could not connect to robot, exiting.\n");
			System.exit(1);
			System.out.println("conectou robô");
		}
		
		robot.runAsync(true);
		robot.lock();
		
		robot.enableMotors();
		System.out.println("motores ligados");
		
		robot.unlock();
		List<Node> sensorReadingList;
		Node novoDestino;
		

		ArPoseWithTimeVector denes = sonar.getCurrentBufferAsVector();
		
		while(Aria.getRunning()) {
			robot.lock();
			
			System.out.println("-- TOPE --");
			
			if (isCondicaoVitoria(robot)) {
				System.out.println("CHEGOU!!!");
				Aria.exit();
			}
			
			sensorReadingList = getReadings(sonar);
			novoDestino = arauto(sensorReadingList);
			
			if(novoDestino!= null) {
				System.out.println("DESTINO: "+novoDestino.x+", "+novoDestino.y);
			}

			System.out.println("arauto funfando");
			if (novoDestino != null) {
				actionGoto.setGoal(converteNodePose(novoDestino));
			}
			

			System.out.println("SetGoal funfando");
			
			robot.unlock();
			ArUtil.sleep(3000);
		}
		
		Aria.exit();
	}
	
	private static boolean isCondicaoVitoria(ArRobot robot) {
		return convertePoseNode(robot.getPose()).x == destino.x && convertePoseNode(robot.getPose()).y == destino.y;
	}

	public static List<Node> getReadings(ArSonarDevice sonar) {
		List<Node> sensorReadingList = new ArrayList<Node>();
		ArPoseWithTimeVector apwtv = sonar.getCurrentBufferAsVector();
		System.out.println(apwtv.isEmpty());
		for(int i = 0; i < apwtv.size(); i++) {
			sensorReadingList.add(convertePoseTimeNode(apwtv.get(i)));
		}
		System.out.println("getReadings funfando");
		return sensorReadingList;
	}
}
