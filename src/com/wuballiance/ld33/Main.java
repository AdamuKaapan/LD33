package com.wuballiance.ld33;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.*;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.painter.HvlCamera;
import com.osreboot.ridhvl.painter.HvlCamera.HvlCameraAlignment;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;

public class Main extends HvlTemplateInteg2D {

	public static final int tilesheetIndex = 0, fontIndex = 1, player1Index = 2, player2Index = 3, playerSmall1Index = 4,
			playerSmall2Index = 5, player3Index = 6;

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
		getTextureLoader().loadResource("PlayerSmall1");
		getTextureLoader().loadResource("PlayerSmall2");
		getTextureLoader().loadResource("Player3");

		MenuManager.initialize();

		Game.setCurrentLevel("TestMap");
		Game.initialize();

		HvlCamera.setAlignment(HvlCameraAlignment.CENTER);
	}

	@Override
	public void update(float delta) {
		playerRotation += delta;

		MenuManager.update(delta);
		HvlMenu.updateMenus(delta);

		drawPlayer(delta);
	}

	private static float zoom = 0f;

	private float zoomGoal = 0f;
	public static final float maxZoom = 2f, mapFadeThreshold = 1.2f;

	private void drawPlayer(float delta){
		if(HvlMenu.getCurrent() == MenuManager.game){
			HvlCamera.setPosition(Player.getX(), Player.getY());
			zoomGoal = maxZoom;
		}else{
			HvlCamera.setPosition((Display.getWidth()/2), (Display.getHeight()/2));
			zoomGoal = 0f;
		}
		zoom = HvlMath.stepTowards(zoom, delta, zoomGoal);
		Game.mapOpacity = zoom > mapFadeThreshold ? (zoom - mapFadeThreshold) / (maxZoom - mapFadeThreshold) : 0;

		HvlCamera.undoTransform();
		float size = HvlMath.lerp(512, Player.radius, Math.min(zoom, 1));
		hvlRotate((Display.getWidth()/2), (Display.getHeight()/2), playerRotation * -4);
		hvlDrawQuad((Display.getWidth()/2) - size, (Display.getHeight()/2) - size, size*2, size*2, getTexture(player3Index), new Color(1, 1, 1, 1 - zoom - (float)Math.sin(playerRotation)));
		hvlResetRotation();
		hvlRotate((Display.getWidth()/2), (Display.getHeight()/2), -playerRotation * 3);
		hvlDrawQuad((Display.getWidth()/2) - size, (Display.getHeight()/2) - size, size*2, size*2, getTexture(player2Index), new Color(1, 1, 1, 1 - zoom));
		hvlResetRotation();
		hvlRotate((Display.getWidth()/2), (Display.getHeight()/2), playerRotation * 2);
		hvlDrawQuad((Display.getWidth()/2) - size, (Display.getHeight()/2) - size, size*2, size*2, getTexture(player1Index), new Color(1, 1, 1, 1 - zoom));
		hvlResetRotation();

		hvlRotate((Display.getWidth()/2), (Display.getHeight()/2), playerRotation * 32);
		hvlDrawQuad((Display.getWidth()/2) - (size*2), (Display.getHeight()/2) - (size*2), (size*2)*2, (size*2)*2, getTexture(playerSmall1Index), new Color(1, 1, 1, -(0.9f - zoom)));
		hvlResetRotation();
		hvlRotate((Display.getWidth()/2), (Display.getHeight()/2), playerRotation * -64);
		hvlDrawQuad((Display.getWidth()/2) - (size*2), (Display.getHeight()/2) - (size*2), (size*2)*2, (size*2)*2, getTexture(playerSmall2Index), new Color(1, 1, 1, -(0.2f - zoom)));
		hvlResetRotation();

		HvlCamera.doTransform();
	}
	
	public static float getZoom(){
		return zoom/maxZoom;
	}

}
