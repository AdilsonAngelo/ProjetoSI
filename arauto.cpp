#include "Aria.h"

struct Node {
	int x, y;
	double f;
};

int getSensorX(int sensor, ArRobot *thisRobot)
{
	ArSensorReading* sonarReading;

	sonarReading = thisRobot->getSonarReading(sensor);

	return (int)((sonarReading->getX())/100);
}

int getSensorY(int sensor, ArRobot *thisRobot)
{
	ArSensorReading* sonarReading;

	sonarReading = thisRobot->getSonarReading(sensor);

	return (int)((sonarReading->getY())/100);
}

void penis(){
	
}

int main(int argc, char **argv)
{
	Aria::init();
	ArArgumentParser argParser(&argc, argv);
	argParser.loadDefaultArguments();
	ArRobot robot;
	ArRobotConnector robotConnector(&argParser, &robot);
	ArSonarDevice sonar;

	ArLog::log(ArLog::Normal, "simpleMotionCommands: Pose=(%.2f,%.2f)",
	robot.getX(), robot.getY());

	robot.addRangeDevice(&sonar);

	Node destino = {9000, 9000, 0};
	tamanhoNo = 510; //51cm
	tamanhoMapa = int(45 * 1000 / tileSize) // 45m de largura maxima;
	offsetMapa = tamanhoMapa / 2;
	int nosExplorados[tamanhoMapa, tamanhoMapa];
	

	
  if(!robotConnector.connectRobot())
  {
    ArLog::log(ArLog::Terse, "Could not connect to the robot.");
    if(argParser.checkHelpAndWarnUnparsed())
    {
        // -help not given, just exit.
        Aria::logOptions();
        Aria::exit(1);
        return 1;
    }
  }

  

  // Trigger argument parsing
  if (!Aria::parseArgs() || !argParser.checkHelpAndWarnUnparsed())
  {
    Aria::logOptions();
    Aria::exit(1);
    return 1;
  }

  

  Aria::exit(0);
  return 0;
}
