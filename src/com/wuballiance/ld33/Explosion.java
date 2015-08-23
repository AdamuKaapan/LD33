package com.wuballiance.ld33;

import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class Explosion {

	public boolean shouldBeDeleted;
	
	private final float lifetime;
	private float timeAlive;
	
	private HvlCoord pos, vel;
	
	public Explosion(HvlCoord pos, HvlCoord vel, float lifetime) {
		this.pos = pos.clone();
		this.vel = vel.clone();
		this.lifetime = lifetime;
	}
	
	public void update(float delta) {
		timeAlive += delta;
		if (timeAlive >= lifetime) shouldBeDeleted = true;
		
		try {
			Game.applyCollision(delta, pos, vel, 1.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pos.add(vel.multNew(delta));
		
		Game.activateSingleTile(pos.x, pos.y, vel.x, vel.y, true);
	}
	
	public void draw(float delta) {
		HvlPainter2D.hvlDrawQuad(pos.x - 16, pos.y - 16, 32, 32, HvlTemplateInteg2D.getTexture(Main.auraIndex), new Color(1, 1, 1, 1.0f - (timeAlive / lifetime)));
	}

	public static void activateSmallExplosion(int x, int y) {
		for (float theta = 0; theta < 2 * (float) Math.PI; theta += (float) Math.PI / 4) {
			HvlCoord dir = new HvlCoord((float) Math.cos(theta), (float) Math.sin(theta)).normalize().fixNaN().mult(192f);

			Game.explosionsToAdd.add(new Explosion(new HvlCoord(x * Game.getMap().getTileWidth() + (Game.getMap().getTileWidth() / 2), y * Game.getMap().getTileHeight()
					+ (Game.getMap().getTileHeight() / 2)), dir, 0.75f));
		}
	}

	public static void activateLargeExplosion(int x, int y, float xVel, float yVel) {

		float angle = (float) Math.atan2(yVel, xVel);

		for (float theta = angle - (float) Math.toRadians(30f); theta < angle + Math.toRadians(30f); theta += Math.toRadians(15f)) {
			HvlCoord vel = new HvlCoord((float) Math.cos(theta), (float) Math.sin(theta));
			vel.normalize().fixNaN().mult(128.0f);
			Game.explosionsToAdd.add(new Explosion(new HvlCoord(x * Game.getMap().getTileWidth() + (Game.getMap().getTileWidth() / 2), y * Game.getMap().getTileHeight()
					+ (Game.getMap().getTileHeight() / 2)), vel, 2f));
		}
	}
}
