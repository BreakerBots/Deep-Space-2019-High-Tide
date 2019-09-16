/*BreakerBots Robotics Team 2019*/
package frc.team5104.module;

/**
 * A wrapper of all the requirements of a subsystem.
 */
public class Module {
	public static abstract class Main { }
	public static abstract class Constants { }
	
	public static abstract class Manager {
		protected abstract Module.Controller getController();
		protected abstract Module.Systems getSystems();
		protected abstract String getName();
		protected abstract void enabled();
		protected abstract void update();
		protected abstract void disabled();
	}
	
	public static abstract class Systems {
		protected abstract boolean isModuleAttached();
		protected abstract void init();
	}
	
	public static abstract class Controller {
		protected abstract void update();
		protected abstract void idle();
	}
}
