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
  ArActionLimiterForwards limiterAction("speed limiter near", 300, 600, 250);
  ArActionLimiterForwards limiterFarAction("speed limiter far", 300, 1100, 400);
  ArActionLimiterTableSensor tableLimiterAction;
  robot.addAction(&tableLimiterAction, 100);
  robot.addAction(&limiterAction, 95);
  robot.addAction(&limiterFarAction, 90);

  // Goto action at lower priority
  ArActionGoto gotoPoseAction("goto");
  robot.addAction(&gotoPoseAction, 50);
  
  // Stop action at lower priority, so the robot stops if it has no goal
  ArActionStop stopAction("stop");
  robot.addAction(&stopAction, 40);

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
    if (first || gotoPoseAction.haveAchievedGoal())
    {
      first = false;
      goalNum++;
      if (goalNum > 4)
        goalNum = 1; // start again at goal #1

      // set our positions for the different goals
      if (goalNum == 1)
        gotoPoseAction.setGoal(ArPose(2500, 0));
      else if (goalNum == 2)
        gotoPoseAction.setGoal(ArPose(2500, 2500));
      else if (goalNum == 3)
        gotoPoseAction.setGoal(ArPose(0, 2500));
      else if (goalNum == 4)
        gotoPoseAction.setGoal(ArPose(0, 0));

      ArLog::log(ArLog::Normal, "Going to next goal at %.0f %.0f", 
            gotoPoseAction.getGoal().getX(), gotoPoseAction.getGoal().getY());
    }

    if(start.mSecSince() >= duration) {
      ArLog::log(ArLog::Normal, "%d seconds have elapsed. Cancelling current goal, waiting 3 seconds, and exiting.", duration/1000);
      gotoPoseAction.cancelGoal();
      robot.unlock();
      ArUtil::sleep(3000);
      break;
    }

    robot.unlock();
    ArUtil::sleep(100);
  }
  
  // Robot disconnected or time elapsed, shut down
  Aria::shutdown();
  return 0;
}
