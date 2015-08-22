package com.wuballiance.ld33;

import com.osreboot.ridhvl.HvlCoord;

public class Spreader {

	private HvlCoord pos, vel;
	
	private float timeAlive;
	
	private boolean shouldDelete;
	
	public Spreader(HvlCoord pos, HvlCoord vel) {
		this.pos = pos;
		this.vel = vel;
	}

	public void update(float delta) {
		timeAlive += delta;
	}
	
	public void draw(float delta) {
		
	}
}
