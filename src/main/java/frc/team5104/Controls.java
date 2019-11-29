/*BreakerBots Robotics Team 2019*/
package frc.team5104;

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
	
	//Drive
	public static final Axis DRIVE_TURN = driver.getAxis(Axis.LEFT_JOYSTICK_X, new Deadband(0.08), new BezierCurve(0.15, 0.7, 0.8, 0.225));
	public static final Axis DRIVE_FORWARD = driver.getAxis(Axis.RIGHT_TRIGGER, new Deadband(0.01));
	public static final Axis DRIVE_REVERSE = driver.getAxis(Axis.LEFT_TRIGGER, new Deadband(0.01));
	public static final Button TOGGLE_VISION = driver.getButton(Button.A);
	
	//Compressor
	public static final Button COMPRESSOR_TOGGLE = driver.getButton(Button.MENU);
	
	//Superstructure
	public static final Button IDLE = driver.getButton(Button.LIST);
	public static final Button TOGGLE_MANUAL = driver.getDoubleClickButton(Button.LIST, 500);
	public static final Button INTAKE = driver.getButton(Button.X);
	public static final Button PLACE_EJECT = driver.getButton(Button.B);
	public static final Button SWITCH_GAME_PIECE = driver.getButton(Button.Y);
	public static final Button HEIGHT_L1 = driver.getButton(Button.DIRECTION_PAD_DOWN);
	public static final Button HEIGHT_L2 = driver.getButton(Button.DIRECTION_PAD_RIGHT);
	public static final Button HEIGHT_L3 = driver.getButton(Button.DIRECTION_PAD_UP);
	public static final Button HEIGHT_SHIP = driver.getButton(Button.DIRECTION_PAD_LEFT);
	public static final Axis ELEVATOR_MANUAL = driver.getAxis(Axis.RIGHT_JOYSTICK_Y, new Deadband(0.08));
	public static final Axis WRIST_MANUAL = driver.getAxis(Axis.RIGHT_JOYSTICK_X, new Deadband(0.08));
	public static final Rumble INTAKE_RUMBLE = driver.getRumble(1, false, 500, 1);
	public static final Rumble EJECT_RUMBLE = driver.getRumble(1, false, 500);
	public static final Rumble SWITCH_HATCH_RUMBLE = driver.getRumble(1, false, 500);
	public static final Rumble SWITCH_CARGO_RUMBLE = driver.getRumble(1, true, 500);
	public static final Rumble SWITCH_HEIGHT_RUMBLE = driver.getRumble(1, true, 500);
	public static final Button HATCH_OP = operator.getButton(Button.X);
	public static final Button CARGO_OP = operator.getButton(Button.B);
	public static final Button HEIGHT_L1_OP = operator.getButton(Button.DIRECTION_PAD_DOWN);
	public static final Button HEIGHT_L2_OP = operator.getButton(Button.DIRECTION_PAD_RIGHT);
	public static final Button HEIGHT_L3_OP = operator.getButton(Button.DIRECTION_PAD_UP);
	public static final Button HEIGHT_SHIP_OP = operator.getButton(Button.DIRECTION_PAD_LEFT);
}
