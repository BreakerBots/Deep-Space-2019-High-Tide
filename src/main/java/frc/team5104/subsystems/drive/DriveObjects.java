/*BreakerBots Robotics Team 2019*/
package frc.team5104.subsystems.drive;

import frc.team5104.main.Constants;

public class DriveObjects {
	/** A simple class for sending/saving drivetrain movement signals. */
	public static class DriveSignal {
		// Robot Drive Signal Variables
		public double leftSpeed;
		public double rightSpeed;
		public DriveUnit unit;
		
		/**
		 * Creates a Robot Drive Signal with the specified speeds in percentOutput
		 * @param leftSpeed  Percent output (-1 to 1) for the left  motors of the drive train to run
		 * @param rightSpeed Percent output (-1 to 1) for the right motors of the drive train to run
		 */
		public DriveSignal(double leftSpeed, double rightSpeed) {
			this(leftSpeed, rightSpeed, DriveUnit.percentOutput);
		}
		
		/**
		 * Creates a Robot Drive Signal with the specified speed in specified unit
		 * @param leftSpeed  Speed for the left  motors of the drive train to run
		 * @param rightSpeed Speed for the right motors of the drive train to run
		 * @param unit The unit for the left & right motor speeds to be in (percentOutput or feetPerSecond)
		 */
		public DriveSignal(double leftSpeed, double rightSpeed, DriveUnit unit) {
			this.leftSpeed = leftSpeed;
			this.rightSpeed = rightSpeed;
			this.unit = unit;
		}
		
		public String toString() {
			return  "l: " + leftSpeed + ", " +
					"r: " + rightSpeed;
		}
	}
	
	/**
	 * Unit that the left & right speeds can be in.
	 * feetPerSecond: uses PID directly on the talons to achieve the specified velocity
	 * percentOutput: directly set the percent output on the talons [from -1 (full speed reverse) to 1 (full speed forward)]
	 * voltage: similar to percentOutput but instead of -1 to 1 its -currentVoltage to currentVoltage (usually ~12V)
	 */
	public static enum DriveUnit {
		feetPerSecond,
		percentOutput,
		voltage
	}
	
	/** A simple class for sending/saving encoder values */
	public static class DriveEncoders {
		/** raw is raw encoder ticks; revs is wheel revolutions; feet is feet traveled */
		public double leftPositionRaw, rightPositionRaw, leftPositionRevs, rightPositionRevs, 
					  leftVelocityRaw, rightVelocityRaw, leftVelocityRevs, rightVelocityRevs;
		public DriveEncoders(double leftPositionRaw, double rightPositionRaw, double leftPositionRevs, double rightPositionRevs, 
				double leftVelocityRaw, double rightVelocityRaw, double leftVelocityRevs, double rightVelocityRevs) {
			this.leftPositionRaw = leftPositionRaw;
			this.rightPositionRaw = rightPositionRaw;
			this.leftPositionRevs = leftPositionRevs;
			this.rightPositionRevs = rightPositionRevs;
			this.leftVelocityRaw = leftVelocityRaw;
			this.rightVelocityRaw = rightVelocityRaw;
			this.leftVelocityRevs = leftVelocityRevs;
			this.rightVelocityRevs = rightVelocityRevs;
		}
		public String toString() { return  "al: " + leftPositionRevs + ", ar: " + rightPositionRevs + ", rl: " + leftPositionRaw + ", rr: " + rightPositionRaw; }
	}
	
	/** A simple class for sending/saving robot positions. */
	public static class RobotPosition {
		public double x, y;
		private double t;
		public RobotPosition(double x, double y, double theta) {
			this.x = x;
			this.y = y;
			this.t = theta;
		}
		
		public void set(double x, double y, double theta) {
			this.x = x;
			this.y = y;
			this.t = theta;
		}
		
	    public double getTheta() {
	        return t/* % (Math.PI * 2.0)*/;
	    }
		
	    public void setTheta(double value) {
	    	this.t = value;
	    }
	    
	    public void addX(double by) {
	    	this.x += by;
	    }
	    
	    public void addY(double by) {
	    	this.y += by;
	    }
	    
		public String toString() {
			return  "x: " + String.format("%.2f", x) + ", " +
					"y: " + String.format("%.2f", y);
		}
	}
	
	/** Handlers unit conversions common in a drive train */
	public static class DriveUnits {
		// Ticks and Wheel Revs
		public static double ticksToWheelRevolutions(double ticks) {
			return ticks / Constants.DRIVE_TICKS_PER_REVOLUTION;
		}
		public static double wheelRevolutionsToTicks(double revs) {
			return revs * Constants.DRIVE_TICKS_PER_REVOLUTION;
		}
		
		// Feet and Wheel Revs
		public static double feetToWheelRevolutions(double feet) {
			return feet / (Constants.DRIVE_WHEEL_DIAMETER * Math.PI);
		}
		public static double wheelRevolutionsToFeet(double revs) {
			return revs * (Constants.DRIVE_WHEEL_DIAMETER * Math.PI);
		}
		
		// Feet and Ticks
		public static double ticksToFeet(double ticks) {
			double r = ticksToWheelRevolutions(ticks);
				   r = wheelRevolutionsToFeet(r);
			return r;
		}
		public static double feetToTicks(double feet) {
			double r = feetToWheelRevolutions(feet);
				   r = wheelRevolutionsToTicks(r);
			return r;
		}
		
		// feet/second and talon velocity (ticks/100ms)
		public static double talonVelToFeetPerSecond(double talonVel) {
			return ticksToFeet(talonVel) * 10.0;
		}
		public static double feetPerSecondToTalonVel(double feetPerSecond) {
			return feetToTicks(feetPerSecond) / 10.0;
		}
	}
}
