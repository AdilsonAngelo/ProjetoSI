#include "Aria.h"

int main(int argc, char **argv)
{
  Aria::init();
  ArArgumentParser parser(&argc, argv);
  parser.loadDefaultArguments();
  ArSimpleConnector simpleConnector(&parser);
  ArRobot robot;
  ArSonarDevice sonar;
  ArAnalogGyro gyro(&robot);
  robot.addRangeDevice(&sonar);

  // Make a key handler, so that escape will shut down the program
  // cleanly
  ArKeyHandler keyHandler;
  Aria::setKeyHandler(&keyHandler);
  robot.attachKeyHandler(&keyHandler);
  printf("You may press escape to exit\n");

  // Collision avoidance actions at higher priority
  ArActionStallRecover recover;
  ArActionBumpers bumpers;
  ArActionAvoidFront avoidFrontNear("Avoid Front Near", 100, 10);
  ArActionAvoidFront avoidFrontFar;ArActionConstantVelocity constantVelocity("Constant Velocity", 400);
  robot.addAction(&recover, 100);
  robot.addAction(&bumpers, 75);
  robot.addAction(&avoidFrontNear, 50);
  robot.addAction(&avoidFrontFar, 49);

  // Goto action at lower priority
  ArActionGoto gotoPoseAction("goto");
  robot.addAction(&gotoPoseAction, 30);

  // Stop action at lower priority, so the robot stops if it has no goal
  ArActionStop stopAction("stop");
  robot.addAction(&stopAction, 20);

  // Parse all command line arguments
  if (!Aria::parseArgs() || !parser.checkHelpAndWarnUnparsed())
  {
    Aria::logOptions();
    exit(1);
  }

  // Connect to the robot
  if (!simpleConnector.connectRobot(&robot))
  {
    printf("Could not connect to robot... exiting\n");
    Aria::exit(1);
  }

  robot.moveTo(ArPose(595, 1258, 0));
  ArLog::log(ArLog::Normal, "simpleMotionCommands: Pose=(%.2f,%.2f)",
  robot.getX(), robot.getY());
  robot.runAsync(true);

  // turn on the motors, turn off amigobot sounds
  robot.enableMotors();
  robot.comInt(ArCommands::SOUNDTOG, 0);

  const int duration = 30000; //msec
  ArLog::log(ArLog::Normal, "Going to four goals in turn for %d seconds, then cancelling goal and exiting.", duration/1000);

  bool first = true;
  int goalNum = 0;
  ArTime start;
  start.setToNow();
  while (Aria::getRunning())
  {
    robot.lock();
    ArLog::log(ArLog::Normal, "simpleMotionCommands: Pose=(%.2f,%.2f,%.2f)",
    robot.getX(), robot.getY());
    robot.unlock();

    robot.lock();

    // Choose a new goal if this is the first loop iteration, or if we
    // achieved the previous goal.
    if (gotoPoseAction.haveAchievedGoal())
    {
      break;
    }

      gotoPoseAction.setGoal(ArPose(1003, 11098, 180));

    robot.unlock();
    ArUtil::sleep(100);
  }

  // Robot disconnected or time elapsed, shut down
  Aria::shutdown();
  return 0;
}
