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
}
