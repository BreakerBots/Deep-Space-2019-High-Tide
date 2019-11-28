/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.util.BezierCurve;
import frc.team5104.util.Deadband;
import frc.team5104.util.XboxController;
import frc.team5104.util.XboxController.Axis;
import frc.team5104.util.XboxController.Button;
import frc.team5104.util.XboxController.Rumble;

/** All the controls for the robot */
public class Controls {
	public static XboxController driver = XboxController.create(0);
	public static XboxController operator = XboxController.create(1);
	
	//Main
	public static final Button IDLE = driver.getButton(Button.LIST);
	public static final Button TOGGLE_MANUAL = driver.getDoubleClickButton(Button.LIST, 500);
	public static final Button COMPRESSOR_TOGGLE = driver.getButton(Button.MENU);
	
	//Drive (in DriveController)
	public static final Axis DRIVE_TURN = driver.getAxis(Axis.LEFT_JOYSTICK_X, new Deadband(0.08), new BezierCurve(0.15, 0.7, 0.8, 0.225));
	public static final Axis DRIVE_FORWARD = driver.getAxis(Axis.RIGHT_TRIGGER, new Deadband(0.01));
	public static final Axis DRIVE_REVERSE = driver.getAxis(Axis.LEFT_TRIGGER, new Deadband(0.01));
	public static final Button TOGGLE_VISION = driver.getButton(Button.A);
	
	//IWE (in IWEController)
	public static final Button IWE_INTAKE = driver.getButton(Button.X);
	public static final Button IWE_PLACE_EJECT = driver.getButton(Button.B);
	public static final Button IWE_SWITCH_GAME_PIECE = driver.getButton(Button.Y);
	public static final Button IWE_HEIGHT_L1 = driver.getButton(Button.DIRECTION_PAD_DOWN);
	public static final Button IWE_HEIGHT_L2 = driver.getButton(Button.DIRECTION_PAD_RIGHT);
	public static final Button IWE_HEIGHT_L3 = driver.getButton(Button.DIRECTION_PAD_UP);
	public static final Button IWE_HEIGHT_SHIP = driver.getButton(Button.DIRECTION_PAD_LEFT);
	public static final Axis IWE_ELEVATOR_MANUAL = driver.getAxis(Axis.RIGHT_JOYSTICK_Y, new Deadband(0.08));
	public static final Axis IWE_WRIST_MANUAL = driver.getAxis(Axis.RIGHT_JOYSTICK_X, new Deadband(0.08));
	public static final Rumble IWE_INTAKE_RUMBLE = driver.getRumble(1, false, 500, 1);
	public static final Rumble IWE_EJECT_RUMBLE = driver.getRumble(1, false, 500);
	public static final Rumble IWE_SWITCH_HATCH_RUMBLE = driver.getRumble(1, false, 500);
	public static final Rumble IWE_SWITCH_CARGO_RUMBLE = driver.getRumble(1, true, 500);
	public static final Rumble IWE_SWITCH_HEIGHT_RUMBLE = driver.getRumble(1, true, 500);
	
	// Operator
	public static final Button IWE_HATCH_OP = operator.getButton(Button.X);
	public static final Button IWE_CARGO_OP = operator.getButton(Button.B);
	public static final Button IWE_HEIGHT_L1_OP = operator.getButton(Button.DIRECTION_PAD_DOWN);
	public static final Button IWE_HEIGHT_L2_OP = operator.getButton(Button.DIRECTION_PAD_RIGHT);
	public static final Button IWE_HEIGHT_L3_OP = operator.getButton(Button.DIRECTION_PAD_UP);
	public static final Button IWE_HEIGHT_SHIP_OP = operator.getButton(Button.DIRECTION_PAD_LEFT);
}
