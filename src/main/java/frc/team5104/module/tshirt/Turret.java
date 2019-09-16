package frc.team5104.module.tshirt;

class Turret {
	static enum TurretState { MANUAL, AUTOMATIC };
	static TurretState state = TurretState.MANUAL;
	
	static enum TurretAutomaticState { CALIBRATING, RUNNING };
	static TurretAutomaticState automaticState = TurretAutomaticState.CALIBRATING;
	
	static void update() {
//		if (state == TurretState.AUTOMATIC) {
//			if (automaticState == TurretAutomaticState.RUNNING) {
//				//PID stuff....
//			}
//			else if (automaticState == TurretAutomaticState.CALIBRATING) {
//				if (TShirtSystems.turret.rightLimitHit()) {
//					TShirtSystems.turret.stop();
//					automaticState = TurretAutomaticState.RUNNING;
//				}
//				else TShirtSystems.turret.setSpeed(0.1);
//			}
//		}
	}
	
	static void enabled() {
		automaticState = TurretAutomaticState.CALIBRATING;
	}
	
	static void disabled() {
		automaticState = TurretAutomaticState.CALIBRATING;
	}
}
