package com.wuballiance.ld33;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.*;

import org.lwjgl.opengl.Display;

import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;

public class Main extends HvlTemplateInteg2D {

	public static final int tilesheetIndex = 0, fontIndex = 1, player1Index = 2, player2Index = 3;
	
	private float playerRotation = 0;
	
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
		getTextureLoader().loadResource("Font");
		getTextureLoader().loadResource("Player1");
		getTextureLoader().loadResource("Player2");
		
		MenuManager.initialize();
		
		Game.setCurrentLevel("TestMap");
		Game.initialize();
	}

	@Override
	public void update(float delta) {
		playerRotation += delta;
		
		drawPlayer(delta);
		
		MenuManager.update(delta);
		HvlMenu.updateMenus(delta);
	}
	
	private void drawPlayer(float delta){
		hvlRotate(Display.getWidth()/2, Display.getHeight()/2, -playerRotation * 3);
		hvlDrawQuad((Display.getWidth()/2) - 512, (Display.getHeight()/2) - 512, 1024, 1024, getTexture(player2Index));
		hvlResetRotation();
		hvlRotate(Display.getWidth()/2, Display.getHeight()/2, playerRotation * 2);
		hvlDrawQuad((Display.getWidth()/2) - 512, (Display.getHeight()/2) - 512, 1024, 1024, getTexture(player1Index));
		hvlResetRotation();
	}
	
}
