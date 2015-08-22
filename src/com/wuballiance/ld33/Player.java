package com.wuballiance.ld33;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.wuballiance.ld33.Game.State;

public class Player {

	public static final float radius = 8f;
	
	public static final float velDecay = -0.35f;
	
	private static HvlCoord pos;
	private static HvlCoord vel;

	public static void reset() {
		pos = new HvlCoord((Game.getMap().getTileWidth() / 2) + 5 * (Game.getMap().getTileWidth()), (Game.getMap().getTileHeight() / 2) + 5 * (Game.getMap().getTileHeight()));
		vel = new HvlCoord(0, 0);
	}

	public static void update(float delta) {
		
		if (Mouse.isButtonDown(0) && Game.getState() == State.WINDUP)
		{
			HvlCoord dir = new HvlCoord(HvlCursor.getCursorX() - pos.x, HvlCursor.getCursorY() - pos.y).normalize().fixNaN().mult(96.0f);
			vel.x = dir.x;
			vel.y = dir.y;
			Game.setState(State.MOVING);
		}
		
		if (Game.getState() == State.MOVING)
		{
			vel.x *= (float) Math.pow(Math.E, velDecay * delta);
			vel.y *= (float) Math.pow(Math.E, velDecay * delta);
			
			try {
				Game.applyCollision(delta, pos, vel, 1.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (vel.length() < 12f)
			{
				Game.setCurrentTurn(Game.getCurrentTurn() + 1);
				Game.setState(State.WINDUP);
				vel.x = 0;
				vel.y = 0;
			}
			
			pos.add(vel.multNew(delta));
			
			Game.activateTile(pos.x, pos.y, radius * 3f);
		}
	}

	public static void draw(float delta) {
		HvlPainter2D.hvlDrawQuad(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius, HvlTemplateInteg2D.getTexture(Main.playerIndex));
	}

	public static HvlCoord getPos() {
		return pos;
	}

	public static void setPos(HvlCoord pos) {
		Player.pos = pos;
	}

	public static HvlCoord getVel() {
		return vel;
	}

	public static void setVel(HvlCoord vel) {
		Player.vel = vel;
	}

	public static void setX(float x) {
		pos.x = x;
	}

	public static void setY(float y) {
		pos.y = y;
	}

	public static void setXVel(float x) {
		vel.x = x;
	}

	public static void setYVel(float y) {
		vel.y = y;
	}

	public static float getX() {
		return pos.x;
	}

	public static float getY() {
		return pos.y;
	}

	public static float getXVel() {
		return vel.x;
	}

	public static float getYVel() {
		return vel.y;
	}
}
