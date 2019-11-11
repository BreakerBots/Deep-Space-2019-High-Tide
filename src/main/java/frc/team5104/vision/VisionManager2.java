package frc.team5104.vision;

import frc.team5104.util.BreakerPIDF;
import frc.team5104.util.WebappTuner.tunerInput;
import frc.team5104.vision.Limelight.LEDMode;
import frc.team5104.vision.Limelight.CamMode;

public class VisionManager2 {

	@tunerInput
	private static double TARGET_X = 2.2;
	@tunerInput
	private static double TURN_P = 0.3;
	@tunerInput
	private static double TURN_D = 0.5;
	@tunerInput
	private static double MIN_OUTPUT = -0.5;
	@tunerInput
	private static double MAX_OUTPUT = 0.5;
	
	private static BreakerPIDF controller = new BreakerPIDF(TURN_P, 0, MIN_OUTPUT, 0, TARGET_X, MIN_OUTPUT, MAX_OUTPUT);
	
	//Update
	public static double getNextTurnSignal() {
		if (Limelight.hasTarget()) {
			controller.setPIDF(TURN_P, 0, TURN_D, 0);
			controller.setSetpoint(TARGET_X);
			controller.setOutputRange(MIN_OUTPUT, MAX_OUTPUT);
			controller.calculate(Limelight.getTargetX());
			return controller.get();
		}
		else return 0.0;
	}

	//Start and End
	public static void end() {
		Limelight.setLEDMode(LEDMode.OFF);
		Limelight.setcamMode(CamMode.DRIVE);
	}
	public static void start() {
		Limelight.setLEDMode(LEDMode.ON);
		Limelight.setcamMode(CamMode.VISION);
	}
}