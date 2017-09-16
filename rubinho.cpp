
#include <Aria.h>
#include <math.h>

#define objX 0.55;
#define objY 12;


/* run this program using the console pauser or add your own getch, system("pause") or input loop */


//Estados Setados como Variaveis Globais
double pxState[100000];
double pyState[100000];
double sonarMemory[100000];

int i = 0;
int isonar = 0;


void insertsonarState(double x){
	sonarMemory[isonar] = x;
	isonar++;
}

void insertState(double x, double y){
pxState[i] = x;
pyState[i] = y;
i++;
}

bool checkState(double x, double y){
int j = 0;
for(j; j<i; j++){
if((pxState[i] == x) && (pyState[i] == y)) return true;
else return false;
}
}

double getDistance(double x1, double y1, double x2, double y2){
	double result =  sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
	return result;
}

int main(int argc, char** argv) {
   Aria::init();
  
  // Parte da conexao do robo
  ArArgumentParser argParser(&argc, argv);
  argParser.loadDefaultArguments();
  ArRobot robot;
  ArRobotConnector robotConnector(&argParser, &robot);
  
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
  robot.runAsync(true);
 robot.enableMotors();
ArActionConstantVelocity constantVelocity("Constant Velocity", 5000);
robot.addAction(&constantVelocity, 50);

	while(true){
		ArUtil::sleep(3000);
		robot.lock();
		ArLog::log(ArLog::Normal, "...");
    ArLog::log(ArLog::Normal, "simpleMotionCommands: Pose=(%.2f,%.2f)",
    robot.getX(), robot.getY());
    robot.unlock(); 
		robot.lock();
  	robot.setRotVel(0);
  	robot.setVel(150);
  	robot.unlock(); 
	}

  /*ArKeyHandler keyHandler;
  Aria::setKeyHandler(&keyHandler);
  robot.attachKeyHandler(&keyHandler);
  ArSonarDevice sonar;
  robot.addRangeDevice(&sonar);
  robot.runAsync(true);
  robot.enableMotors();

  ArActionGoto gotoPoseAction("goto");
  robot.addAction(&gotoPoseAction, 5);
  
  gotoPoseAction.setGoal(ArPose(13000,3000));
  
  
 
  //Declaracao de acoes
  ArActionStallRecover recover;
  ArActionBumpers bumpers;
  ArActionAvoidFront avoidFrontNear("Avoid Front Near", 225, 0);
  ArActionAvoidFront avoidFrontFar("Avoid Front Near", 600, 0);
  ArActionConstantVelocity constantVelocity("Constant Velocity", 5000);
  
  //Execucao de acoes
  robot.addAction(&recover, 100);
  robot.addAction(&bumpers, 75);
  robot.addAction(&avoidFrontNear, 50);
  robot.addAction(&avoidFrontFar, 49);

  gotoPoseAction.setGoal(ArPose(13000,3000));

  insertState(robot.getX(), robot.getY());

  if(checkState(robot.getX(), robot.getY())){
	ArActionAvoidFront avoidPerto("Avoid Front Near", -140, 0);
	ArActionAvoidFront avoidLonge("Avoid Front Near", -700, 0);
	ArActionConstantVelocity constantv("Constant Velocity", 400);
	robot.addAction(&recover, 100);
	robot.addAction(&bumpers, 75);
	robot.addAction(&avoidPerto, 50);
	robot.addAction(&avoidLonge, 49);
   }
 
  */
  
  robot.waitForRunExit();
  
  Aria::exit(0);
  return 0;
}

  
