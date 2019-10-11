/*BreakerBots Robotics Team 2019*/
package frc.team5104.subsystems.drive;

import frc.team5104.subsystems.drive.DriveObjects.DriveSignal;
import frc.team5104.subsystems.drive.DriveObjects.DriveUnit;
import frc.team5104.util.Deadband;
import frc.team5104.util.Deadband.deadbandType;

/** A class for better handling of the robot's drive train (PLAZZZ MAKE BETTEER) */
public class DriveHelperV2 {
	
	//Constants
	private static final double WHEEL_NON_LINEARITY_HIGH_GEAR = 0.01;
	private static final double WHEEL_NON_LINEARITY_LOW_GEAR = 0.5;
	private static final double NEGATIVE_INTERTIA_SCALAR_HIGH_GEAR = 0.0;
	private static final double NEGATIVE_INTERTIA_THRESHOLD_LOW_GEAR = 0.65;
	private static final double NEGATIVE_INTERTIA_TURN_SCALAR_LOW_GEAR = 3.0;
	private static final double NEGATIVE_INTERTIA_CLOSE_SCALAR_LOW_GEAR = 3.0;
	private static final double NEGATIVE_INTERTIA_FAR_SCALAR_LOW_GEAR = 4.0;
	private static final double SENSITIVITY_HIGH_GEAR = 0.6;
	private static final double SENSITIVITY_LOW_GEAR = 0.625;
	
	private static final double TURN_DEADBAND = 0.08;
	private static final double FORWARD_DEADBAND = 0.01;
	
	private static final double RIGHT_ACCOUNT_FORWARD = 1.000;
	private static final double RIGHT_ACCOUNT_REVERSE = 1.000;
	private static final double LEFT_ACCOUNT_FORWARD  = 1.000;
	private static final double LEFT_ACCOUNT_REVERSE  = 1.000;
	
	private static final double MIN_SPEED_HIGH_GEAR_FORWARD = 0;
	private static final double MIN_SPEED_HIGH_GEAR_TURN = 0;
	private static final double MIN_SPEED_LOW_GEAR_FORWARD = 0;
	private static final double MIN_SPEED_LOW_GEAR_TURN = 0;
	
	//Variables
	private static double oldForward = 0.0;
	private static double quickStopAccumlator = 0.0;
	private static double negativeInertiaAccumlator = 0.0;
	
	/** Calculates and left and right speed (in volts) for the robot depending on input variables */
	public static DriveSignal get(double turn, double forward, boolean isHighGear) {
		//deadbands
		turn = Deadband.get(turn, TURN_DEADBAND, deadbandType.slopeAdjustment);
		forward = Deadband.get(forward, FORWARD_DEADBAND, deadbandType.slopeAdjustment);
		
		//cool boi effects
		double negInertia = forward - oldForward;
		oldForward = forward;
		double wheelNonLinearity;
		if (isHighGear) {
			wheelNonLinearity = WHEEL_NON_LINEARITY_HIGH_GEAR;
			final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
		} else {
			wheelNonLinearity = WHEEL_NON_LINEARITY_LOW_GEAR;
			final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
		}
		double leftSpeed, rightSpeed, overPower;
		double sensitivity;
		double angularPower;
		double linearPower;
		double negInertiaScalar;
		if (isHighGear) {
			negInertiaScalar = NEGATIVE_INTERTIA_SCALAR_HIGH_GEAR;
			sensitivity = SENSITIVITY_HIGH_GEAR;
		}
		else {
			if (forward * negInertia > 0) negInertiaScalar = NEGATIVE_INTERTIA_TURN_SCALAR_LOW_GEAR;
			else {
				if (Math.abs(forward) > NEGATIVE_INTERTIA_THRESHOLD_LOW_GEAR) negInertiaScalar = NEGATIVE_INTERTIA_FAR_SCALAR_LOW_GEAR;
				else negInertiaScalar = NEGATIVE_INTERTIA_CLOSE_SCALAR_LOW_GEAR;
			}
			sensitivity = SENSITIVITY_LOW_GEAR;
		}
		double negInertiaPower = negInertia * negInertiaScalar;
		negativeInertiaAccumlator += negInertiaPower;
		forward += negativeInertiaAccumlator;
		if (negativeInertiaAccumlator > 1) negativeInertiaAccumlator -= 1;
		else if (negativeInertiaAccumlator < -1) negativeInertiaAccumlator += 1;
		else negativeInertiaAccumlator = 0;
		linearPower = turn;
		overPower = 0.0;
		angularPower = Math.abs(turn) * forward * sensitivity - quickStopAccumlator;
		if (quickStopAccumlator > 1) quickStopAccumlator -= 1;
		else if (quickStopAccumlator < -1) quickStopAccumlator += 1;
		else quickStopAccumlator = 0.0;
		rightSpeed = leftSpeed = linearPower;
		leftSpeed += angularPower;
		rightSpeed -= angularPower;
		if (leftSpeed > 1.0) {
			rightSpeed -= overPower * (leftSpeed - 1.0);
			leftSpeed = 1.0;
		} else if (rightSpeed > 1.0) {
			leftSpeed -= overPower * (rightSpeed - 1.0);
			rightSpeed = 1.0;
		} else if (leftSpeed < -1.0) {
			rightSpeed += overPower * (-1.0 - leftSpeed);
			leftSpeed = -1.0;
		} else if (rightSpeed < -1.0) {
			leftSpeed += overPower * (-1.0 - rightSpeed);
			rightSpeed = -1.0;
		}
		
		//created drive signal
		DriveSignal signal = new DriveSignal(
			leftSpeed * 12, 
			rightSpeed * 12, 
			DriveUnit.voltage
		);
		
		//drive straight & min speed
		signal = applyDriveStraight(signal);
		signal = applyMotorMinSpeed(signal, isHighGear);
		
		return signal;
	}
	
	private static DriveSignal applyDriveStraight(DriveSignal signal) {
		double leftMult = (signal.leftSpeed > 0 ? 
				LEFT_ACCOUNT_REVERSE : 
				LEFT_ACCOUNT_FORWARD
			);
		double rightMult = (signal.rightSpeed > 0 ? 
				RIGHT_ACCOUNT_REVERSE : 
				RIGHT_ACCOUNT_FORWARD
			);
		signal.leftSpeed = signal.leftSpeed * leftMult;
		signal.rightSpeed = signal.rightSpeed * rightMult;
		return signal;
	}

	private static DriveSignal applyMotorMinSpeed(DriveSignal signal, boolean inHighGear) {
		double turn = Math.abs(signal.leftSpeed - signal.rightSpeed) / 2;
		double biggerMax = (Math.abs(signal.leftSpeed) > Math.abs(signal.rightSpeed) ? Math.abs(signal.leftSpeed) : Math.abs(signal.rightSpeed));
		if (biggerMax != 0)
			turn = Math.abs(turn / biggerMax);
		double forward = 1 - turn;
		
		double minSpeed;
		if (inHighGear)
			minSpeed = (forward * (MIN_SPEED_HIGH_GEAR_FORWARD/12.0)) + (turn * (MIN_SPEED_HIGH_GEAR_TURN/12.0));
		else
			minSpeed = (forward * (MIN_SPEED_LOW_GEAR_FORWARD/12.0)) + (turn * (MIN_SPEED_LOW_GEAR_TURN/12.0));
		
		if (signal.leftSpeed != 0)
			signal.leftSpeed = signal.leftSpeed * (1 - minSpeed) + (signal.leftSpeed > 0 ? minSpeed : -minSpeed);
		if (signal.rightSpeed != 0)
			signal.rightSpeed = signal.rightSpeed * (1 - minSpeed) + (signal.rightSpeed > 0 ? minSpeed : -minSpeed);

		return signal;
	}
}
