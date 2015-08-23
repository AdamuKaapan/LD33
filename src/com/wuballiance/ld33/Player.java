package com.wuballiance.ld33;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.particle.HvlParticle;
import com.osreboot.ridhvl.particle.HvlParticleSystem;
import com.osreboot.ridhvl.particle.collection.HvlLinearPositionProvider;
import com.osreboot.ridhvl.particle.collection.HvlSimpleParticle;
import com.osreboot.ridhvl.particle.collection.HvlSimpleParticleSystem;
import com.osreboot.ridhvl.particle.correlation.HvlParticleCorrelator;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.wuballiance.ld33.Game.State;

public class Player {

	public static final float radius = 8f;

	public static final float velDecay = -0.35f;

	public static HvlCoord collisionAnimationPos = null;
	public static float collisionAnimationRot;

	private static HvlCoord pos;
	private static HvlCoord vel;

	private static HvlCoord dragStart;
	private static boolean isDragging;

	private static HvlSimpleParticleSystem particles;

	public static void initialize(){
//		particles = new HvlSimpleParticleSystem(pos.x, pos.y, 32, 32, new HvlLinearPositionProvider(0, 0, 0, 0), HvlTemplateInteg2D.getTexture(Main.wallParticleIndex));
//		particles.setMinXVel(-32f);
//		particles.setMinYVel(-32f);
//		particles.setMaxXVel(32f);
//		particles.setMaxYVel(32f);
//		particles.setScaleDecay(-1.0f);
//		particles.setStartColor(new Color(0, 0, 0, 1f));
//		particles.setEndColor(Color.transparent);
//		particles.setMinScale(0.8f);
//		particles.setMaxScale(1.0f);
//		particles.setMinParticlesPerSpawn(1);
//		particles.setMaxParticlesPerSpawn(2);
//		particles.setMinLifetime(3);
//		particles.setMaxLifetime(4);
//		particles.setMinTimeToSpawn(0.01f);
//		particles.setMaxTimeToSpawn(0.1f);
	}

	public static void reset(){
		pos = new HvlCoord((Game.getMap().getTileWidth() / 2) + 5 * (Game.getMap().getTileWidth()), (Game.getMap().getTileHeight() / 2) + 5
				* (Game.getMap().getTileHeight()));
		vel = new HvlCoord(0, 0);
	}

	public static void update(float delta) {
		//particles.setPosition(pos.x, pos.y);
		if (Game.getState() == State.WINDUP) {
			if (Mouse.isButtonDown(0)) {
				if (HvlMath.distance(HvlCursor.getCursorX(), HvlCursor.getCursorY(), Display.getWidth() / 2, Display.getHeight() / 2) < radius) {
					isDragging = true;
					dragStart = new HvlCoord(HvlCursor.getCursorX(), HvlCursor.getCursorY());
				}
			} else {
				if (isDragging && HvlMath.distance(HvlCursor.getCursorX(), HvlCursor.getCursorY(), Display.getWidth() / 2, Display.getHeight() / 2) > radius) {
					HvlCoord dir = new HvlCoord(dragStart.x - HvlCursor.getCursorX(), dragStart.y - HvlCursor.getCursorY());
					float oldLen = dir.length();
					dir.normalize().mult(Math.min(oldLen, 128.0f));
					vel.x = dir.x;
					vel.y = dir.y;
					Game.setState(State.MOVING);
				}
				isDragging = false;
			}
		}

		if (Game.getState() == State.MOVING) {
			vel.x *= (float) Math.pow(Math.E, velDecay * delta);
			vel.y *= (float) Math.pow(Math.E, velDecay * delta);

			try {
				float angle = Game.applyCollision(delta, pos, vel, 1.0f);
				if(angle != Game.NO_COLLISION){
					Main.collisionAnimation.reset();
					Main.collisionAnimation.setRunning(true);
					collisionAnimationPos = pos.clone();
					collisionAnimationRot = angle;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (vel.length() < 12f) {
				Game.setCurrentTurn(Game.getCurrentTurn() + 1);
				Game.setState(State.WINDUP);
				vel.x = 0;
				vel.y = 0;
				Game.onEndTurn();
			}

			pos.add(vel.multNew(delta));
		}

		Game.activateTile(pos.x, pos.y, radius * 2.1f);
	}

	public static void draw(float delta) {
		//particles.draw(delta);
		if(collisionAnimationPos != null){
			hvlRotate(collisionAnimationPos.x, collisionAnimationPos.y, collisionAnimationRot + 90);
			hvlDrawQuad(collisionAnimationPos.x - 16, collisionAnimationPos.y - 12, 48, 48, Main.collisionAnimation);
			hvlResetRotation();
		}

		if (isDragging)
		{
			HvlCoord dir = new HvlCoord(dragStart.x - HvlCursor.getCursorX(), dragStart.y - HvlCursor.getCursorY());
			float oldLen = dir.length();
			dir.normalize().fixNaN().mult(Math.min(oldLen, 256.0f));

			HvlPainter2D.hvlDrawLine(pos.x - dir.x, pos.y - dir.y, pos.x, pos.y, Color.gray, 2);
		}
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
