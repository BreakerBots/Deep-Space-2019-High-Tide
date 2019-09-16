/*BreakerBots Robotics Team 2019*/
package frc.team5104.module.drive;

import frc.team5104.main.Controls;
import frc.team5104.main.RobotState;
import frc.team5104.module.Module;
import frc.team5104.module.drive.DriveSignal.DriveUnit;
import frc.team5104.util.BezierCurve;
import frc.team5104.util.BezierCurveInterpolator;
import frc.team5104.util.Deadband;
import frc.team5104.util.Deadband.deadbandType;

/**
 * Handles drive control (included all augmentation from the driver to the robot)
 */
class DriveController extends Module.Controller {
	//Variables
	private static final BezierCurve _driveCurve = new BezierCurve(.2, 0, .2, 1);
	private static final double _driveCurveChange = 1.0;
	private static final BezierCurveInterpolator vTeleopLeftSpeed  = new BezierCurveInterpolator(_driveCurveChange, _driveCurve);
	private static final BezierCurveInterpolator vTeleopRightSpeed = new BezierCurveInterpolator(_driveCurveChange, _driveCurve);
	
	private static BezierCurve turnCurve = new BezierCurve(0.15, 0.7, 0.8, 0.225);//0, 0.4, 0.8, 0.4//0.15, 0.7, 0.8, 0.225
	private static double _turnCurveSpeedAdjust = 0.2;
	
	//Main Handle Function
	protected void update() {
		//Get inputs
		double turn = Controls.Drive.turn.getAxis();
		double forward = Controls.Drive.forward.getAxis() - Controls.Drive.reverse.getAxis();

		//Apply controller deadbands
		turn = -Deadband.get(turn,  0.08, deadbandType.slopeAdjustment);
		forward = Deadband.get(forward, 0.01, deadbandType.slopeAdjustment);
		
		//Apply bezier curve
		turnCurve.x1 = (1 - Math.abs(forward)) * (1 - _turnCurveSpeedAdjust) + _turnCurveSpeedAdjust;
		turn = turnCurve.getPoint(turn);
		
		//Apply inertia affect
		vTeleopLeftSpeed.setSetpoint(forward - turn);
		vTeleopRightSpeed.setSetpoint(forward + turn);
		DriveSignal signal = new DriveSignal(
			vTeleopLeftSpeed.update() * 12, 
			vTeleopRightSpeed.update() * 12, 
			DriveUnit.voltage
		);
		
		//Apply drive straight effects
		signal = applyDriveStraight(signal);
		
		//Apply min speed
		signal = applyMotorMinSpeed(signal, 1);
			
		//Set talon speeds
		DriveSystems.motors.set(signal);
	}

	//Stop The Subsystem
	protected void idle() {
		DriveSystems.motors.stop();
	}
	
	private static DriveSignal applyDriveStraight(DriveSignal signal) {
		double leftMult = (signal.leftSpeed > 0 ? 
				DriveConstants.WHEELACCOUNT_LEFT_REVERSE : 
				DriveConstants.WHEELACCOUNT_LEFT_FORWARD
			);
		double rightMult = (signal.rightSpeed > 0 ? 
				DriveConstants.WHEELACCOUNT_RIGHT_REVERSE : 
				DriveConstants.WHEELACCOUNT_RIGHT_FORWARD
			);
		signal.leftSpeed = signal.leftSpeed * leftMult;
		signal.rightSpeed = signal.rightSpeed * rightMult;
		return signal;
	}

	private static DriveSignal applyMotorMinSpeed(DriveSignal signal, double percentAffect) {
		double turn = Math.abs(signal.leftSpeed - signal.rightSpeed) / 2;
		double biggerMax = (Math.abs(signal.leftSpeed) > Math.abs(signal.rightSpeed) ? Math.abs(signal.leftSpeed) : Math.abs(signal.rightSpeed));
		if (biggerMax != 0)
			turn = Math.abs(turn / biggerMax);
		double forward = 1 - turn;
		
		double minSpeed;
		minSpeed = forward * (DriveConstants.MINSPEED_FORWARD/12.0) + turn * (DriveConstants.MINSPEED_TURN/12.0);
		
		minSpeed *= percentAffect;
		
		if (signal.leftSpeed != 0)
			signal.leftSpeed = signal.leftSpeed * (1 - minSpeed) + (signal.leftSpeed > 0 ? minSpeed : -minSpeed);
		if (signal.rightSpeed != 0)
			signal.rightSpeed = signal.rightSpeed * (1 - minSpeed) + (signal.rightSpeed > 0 ? minSpeed : -minSpeed);

		return signal;
	}
}
