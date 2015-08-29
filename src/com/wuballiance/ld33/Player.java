package com.wuballiance.ld33;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.tile.collection.HvlSimpleTile;
import com.wuballiance.ld33.Game.State;

public class Player {

	public static final float radius = 8f;

	public static final float velDecay = -0.5f, velStoppingDecay = -1.2f, velThreshold = 50;
	public static final float launchSpeed = 2f;

	public static HvlCoord collisionAnimationPos = null;
	public static float collisionAnimationRot;

	private static HvlCoord pos;
	private static HvlCoord vel;

	private static HvlCoord dragStart;
	private static boolean isDragging;

	//	private static HvlSimpleParticleSystem particles;

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
		int tileX = 5;
		int tileY = 5;

		for (int x = 0; x < Game.getMap().getLayer(2).getMapWidth(); x++)
		{
			for (int y = 0; y < Game.getMap().getLayer(2).getMapHeight(); y++)
			{
				if (!Game.getMap().isTileInLocation(x, y, 2)) continue;

				HvlSimpleTile st = (HvlSimpleTile) Game.getMap().getLayer(2).getTile(x, y);

				if (st.getTile() == 56)
				{
					tileX = x;
					tileY = y;
				}
			}
		}

		pos = new HvlCoord((Game.getMap().getTileWidth() / 2) + tileX * (Game.getMap().getTileWidth()), (Game.getMap().getTileHeight() / 2) + tileY
				* (Game.getMap().getTileHeight()));
		vel = new HvlCoord(0, 0);
	}

	public static void update(float delta) {
		//particles.setPosition(pos.x, pos.y);
		if (Game.getState() == State.WINDUP && Game.currentTurn < Game.par) {
			if (Mouse.isButtonDown(0)) {
				if (HvlMath.distance(HvlCursor.getCursorX(), HvlCursor.getCursorY(), Display.getWidth() / 2, Display.getHeight() / 2) < radius) {
					isDragging = true;
					dragStart = new HvlCoord(HvlCursor.getCursorX(), HvlCursor.getCursorY());
				}
			} else {
				if (isDragging && HvlMath.distance(HvlCursor.getCursorX(), HvlCursor.getCursorY(), Display.getWidth() / 2, Display.getHeight() / 2) > radius * 2) {
					HvlCoord dir = new HvlCoord(dragStart.x - HvlCursor.getCursorX(), dragStart.y - HvlCursor.getCursorY());
					float oldLen = dir.length();
					dir.normalize().mult(Math.min(oldLen, 196.0f) * launchSpeed);
					vel.x = dir.x;
					vel.y = dir.y;
					Game.setState(State.MOVING);
					Game.setCurrentTurn(Game.getCurrentTurn() + 1);
				}
				isDragging = false;
			}
		}

		if (Game.getState() == State.MOVING) {
			vel.x *= (float) Math.pow(Math.E, (vel.length() < velThreshold ? velStoppingDecay : velDecay) * delta);
			vel.y *= (float) Math.pow(Math.E, (vel.length() < velThreshold ? velStoppingDecay : velDecay) * delta);

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
			dir.normalize().fixNaN().mult(Math.min(oldLen, 196.0f));

			float distance = HvlMath.distance(HvlCursor.getCursorX(), HvlCursor.getCursorY(), Display.getWidth() / 2, Display.getHeight() / 2);
			if(distance > radius * 2){
				HvlCoord startPoint = new HvlCoord(dir.x, dir.y);
				startPoint.normalize();
				startPoint.mult(-16);
				startPoint.add(pos);
				HvlPainter2D.hvlDrawLine(startPoint.x, startPoint.y, pos.x - dir.x, pos.y - dir.y, new Color(0.4f, 0.4f, 0.4f, 1), 3);
				
				HvlCoord hairPoint = new HvlCoord(dir.x, dir.y);
				hairPoint.normalize();
				hairPoint.mult(16);
				hairPoint.add(pos);
				
				HvlCoord hair2Point = new HvlCoord(dir.x, dir.y);
				hair2Point.normalize();
				hair2Point.mult(18);
				hair2Point.add(pos);
				
				HvlPainter2D.hvlDrawLine(hairPoint.x, hairPoint.y, hair2Point.x, hair2Point.y, new Color(0.4f, 0.4f, 0.4f, 1), 2);
			}	
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
