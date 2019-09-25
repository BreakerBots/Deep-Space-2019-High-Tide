package frc.team5104.teleop;

import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.drive.DriveObjects.DriveSignal;
import frc.team5104.subsystems.drive.DriveObjects.DriveUnit;
import frc.team5104.subsystems.drive.DriveObjects.DriveUnits;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.TeleopController;

public class DriveAutoTune extends TeleopController {
	protected String getName() { return "Drive Auto Tune"; }
	
	private static final int DELAY_LENGTH = 2000;
	private static enum AutoTuneStage { FORWARD, REVERSE, TURN_RIGHT, TURN_LEFT };
	private AutoTuneStage currentStage;
	private double[] measuredSpeeds;
	private double speed;
	private boolean finished;
	private long delayStart;
	
	//Start
	protected void enabled() {
		speed = 0;
		measuredSpeeds = new double[4];
		finished = false;
		currentStage = AutoTuneStage.FORWARD;
		delayStart = System.currentTimeMillis() - DELAY_LENGTH;
		forwardInit(false);
	}
	
	//Main Loop
	protected void update() {
		if (finished) { Drive.stop(); return; }
		DriveSignal signal = new DriveSignal(0, 0, DriveUnit.voltage);
		if (System.currentTimeMillis() > delayStart + DELAY_LENGTH) {
			switch (currentStage) {
				case FORWARD: signal = getForwardSignal(false);
					break;
				case REVERSE: signal = getForwardSignal(true);
					break;
				case TURN_RIGHT: signal = getTurnSignal(true);
					break;
				case TURN_LEFT: signal = getTurnSignal(false);
					break;
			}
		}
		Drive.set(signal);
	}
	
	//Actual Stuffs
	private void nextStage() {
		delayStart = System.currentTimeMillis();
		
		switch (currentStage) {
			case FORWARD:
				measuredSpeeds[0] = speed;
				currentStage = AutoTuneStage.REVERSE;
				forwardInit(true);
				break;
			case REVERSE:
				measuredSpeeds[1] = speed;
				currentStage = AutoTuneStage.TURN_RIGHT;
				turnInit(true);
				break;
			case TURN_RIGHT:
				measuredSpeeds[2] = speed;
				currentStage = AutoTuneStage.TURN_LEFT;
				turnInit(false);
				break;
			case TURN_LEFT:
				measuredSpeeds[3] = speed;
				
				console.divider();
				console.log(c.DRIVE, "Measured Min-Speeds: ");
				console.log(c.DRIVE, " - Forward: " + BreakerMath.round((measuredSpeeds[0] + measuredSpeeds[1]) / 2.0, 2));
				console.log(c.DRIVE, " - Turn: " 	 + BreakerMath.round((measuredSpeeds[2] + measuredSpeeds[3]) / 2.0, 2));
				console.divider();
				
				finished = true;
				break;
		}
	}
	private void forwardInit(boolean reverse) {
		speed = 0;
		console.log(c.DRIVE, (reverse ? "Reverse" : "Forward") + " min-speed measure started");
	}
	private DriveSignal getForwardSignal(boolean reverse) {
		if ((Math.round((speed)*1000.0)/1000.0) % 0.1 == 0)
			console.log(c.DRIVE, (reverse ? "Reverse" : "Forward") + " min-speed set " + String.format("%.2f", speed));
		if (speed > 6) {
			nextStage();
			//console.log(c.TUNING, (reverse ? "Reverse" : "Forward") + " min-speed measure failed (exceeded max speed)");
		}
		if (Math.abs(DriveUnits.wheelRevolutionsToFeet(Drive.getEncoders().leftVelocityRevs)) > 0.3) {
			nextStage();
			//console.log(c.TUNING, (reverse ? "Reverse" : "Forward") + " min-speed measured at " + String.format("%.2f", speed));
		}
		speed += 0.005;
		return new DriveSignal(speed * (reverse ? -1 : 1), speed * (reverse ? -1 : 1), DriveUnit.voltage);
	}
	private void turnInit(boolean right) {
		speed = 0.8;
		console.log(c.DRIVE, "Turning " + (right ? "right" : "left") + " min-speed measure started");
	}
	private DriveSignal getTurnSignal(boolean right) {
		if ((Math.round((speed)*1000.0)/1000.0) % 0.1 == 0)
			console.log(c.DRIVE, "Turning min-speed set " + String.format("%.2f", speed));
		if (speed > 6) {
			nextStage();
			//console.log(c.TUNING, "Turning min-speed measure failed (exceeded max speed)");
		}
		if (Math.abs(DriveUnits.wheelRevolutionsToFeet(Drive.getEncoders().leftVelocityRevs)) > 0.3) {
			nextStage();
			//console.log(c.TUNING, "Turning min-speed measured at " + String.format("%.2f", speed));
		}
		speed += 0.005;
		return new DriveSignal(speed * (right ? 1 : -1), speed * (right ? -1 : 1), DriveUnit.voltage);
	}
}