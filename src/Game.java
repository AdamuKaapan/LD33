
public class Game {
	public enum State
	{
		MOVING, WINDUP
	}
	
	private static State currentState;
	
	public static void reset() {
		Player.reset();
	}
	
	public static void initialize() {
		
		reset();
	}
	
	public static void update(float delta) {
		Player.update(delta);
	}
	
	public static void draw(float delta) {
		Player.draw(delta);
	}

	public static State getCurrentState() {
		return currentState;
	}

	public static void setCurrentState(State currentState) {
		Game.currentState = currentState;
	}
}
