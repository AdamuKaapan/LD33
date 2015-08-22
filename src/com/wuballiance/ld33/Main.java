package com.wuballiance.ld33;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;

public class Main extends HvlTemplateInteg2D {

	public static final int tilesheetIndex = 0;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		super(60, 1280, 720, "LD33", new HvlDisplayModeDefault());
	}

	@Override
	public void initialize() {
		HvlTilemapCollisionUtil.registerCornerSet(25, 26, 33, 34);
		
		getTextureLoader().loadResource("Tilesheet");
		Game.setCurrentLevel("TestMap");
		Game.initialize();
	}

	@Override
	public void update(float delta) {
		Game.update(delta);
		
		Game.draw(delta);
	}
	
	
}
