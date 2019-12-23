package frc.team5104.util.subsystem;

/** A snickers rapper of all the requirements of a subsystem. */
public abstract class Subsystem {
	
	/** Called periodically from the robot loop */
	protected abstract void update();
	
	/** Called when robots boots up; initialize devices here */
	protected abstract void init();
	
	/** Called whenever the robot becomes enabled or disabled */
	protected void reset() { }
	
	/** Called to stop this subsystem */
	protected abstract void stop();
	
	/** Returns if this subsystem is calibrated. Defaults to true unless overriden. */
	protected boolean isCalibrated() { return true; }
	
	//Other Versions
	/**  */
	public static abstract class IntakeSubsystem extends Subsystem {
		
	}
	
	/**  */
	public static abstract class ElevatorSubsystem extends Subsystem {
		
	}
	
	/**  */
	public static abstract class WristSubsystem extends Subsystem {
		
	}
	
	/**  */
	public static abstract class DriveSubsystem extends Subsystem {
		
	}
}