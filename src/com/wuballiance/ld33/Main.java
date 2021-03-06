package com.wuballiance.ld33;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawLine;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlDrawQuad;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlResetRotation;
import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.hvlRotate;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.HvlTimer;
import com.osreboot.ridhvl.configold.HvlConfigUtil;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeDefault;
import com.osreboot.ridhvl.loader.HvlSoundLoader.HvlSoundType;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.HvlMenuDJ;
import com.osreboot.ridhvl.painter.HvlAnimatedTextureUV;
import com.osreboot.ridhvl.painter.HvlCamera;
import com.osreboot.ridhvl.painter.HvlCamera.HvlCameraAlignment;
import com.osreboot.ridhvl.painter.HvlCamera.HvlCameraTransformation;
import com.osreboot.ridhvl.painter.HvlRenderFrame;
import com.osreboot.ridhvl.painter.HvlShader;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;

public class Main extends HvlTemplateInteg2D {
	
	public static Map<String, Integer> pars;

	public static final int tilesheetIndex = 0, fontIndex = 1, player1Index = 2, player2Index = 3, playerSmall1Index = 4, playerSmall2Index = 5,
			player3Index = 6, spikeAnimationIndex = 7, wallParticleIndex = 8, playerAnimationIndex = 9, auraIndex = 10, logoIndex = 11, logoInvertIndex = 12,
			darkenAnimationIndex = 13, smallExplosionAnimationIndex = 14, dotAnimationIndex = 15, tutorial1Index = 16, smallBombIndex = 17,
			largeBombIndex = 18, largeExplosionIndex = 19, directionalBombIndex = 20, directionalExplosionIndex = 21, cursorIndex = 22, aura2Index = 23;

	private float playerRotation = 0;

	public static HvlRenderFrame frame1, frame2;
	public static HvlShader bloom1, bloom2;
	
	public static HvlAnimatedTextureUV collisionAnimation, playerAnimation;
	
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		super(60, 1280, 720, "Tenebrous Expanse - by WubAlliance", "Icon", new HvlDisplayModeDefault());
	}

	@Override
	public void initialize() {
		getTimer().setMaxDelta(HvlTimer.MD_TENTH);

		HvlConfigUtil.loadStaticConfig(SaveFile.class, "res/Save.txt");
		HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");

		HvlTilemapCollisionUtil.registerCornerSet(25, 26, 33, 34);

		getTextureLoader().loadResource("Tilesheet");
		getTextureLoader().loadResource("Font");
		getTextureLoader().loadResource("Player1");
		getTextureLoader().loadResource("Player2");
		getTextureLoader().loadResource("PlayerSmall1");
		getTextureLoader().loadResource("PlayerSmall2");
		getTextureLoader().loadResource("Player3");
		getTextureLoader().loadResource("SpikeAnimation");
		getTextureLoader().loadResource("DarkSpark");
		getTextureLoader().loadResource("PlayerAnimation");
		getTextureLoader().loadResource("Aura");
		getTextureLoader().loadResource("Logo");
		getTextureLoader().loadResource("LogoInvert");
		getTextureLoader().loadResource("DarkenAnimation");
		getTextureLoader().loadResource("SmallExplosionAnimation");
		getTextureLoader().loadResource("DotAnimation");
		getTextureLoader().loadResource("Tutorial1");
		getTextureLoader().loadResource("SmallBombAnimation");
		getTextureLoader().loadResource("LargeBombAnimation");
		getTextureLoader().loadResource("LargeExplosionAnimation");
		getTextureLoader().loadResource("DirectionalBombAnimation");
		getTextureLoader().loadResource("DirectionalExplosionAnimation");
		getTextureLoader().loadResource("Cursor");
		getTextureLoader().loadResource("Aura2");

		getSoundLoader().loadResource("Bright_Darkness", HvlSoundType.OGG);
		getSoundLoader().loadResource("Checkmate", HvlSoundType.OGG);
		getSoundLoader().loadResource("Droplet_of_Light", HvlSoundType.OGG);
		getSoundLoader().loadResource("Light_and_Darkness", HvlSoundType.OGG);
		getSoundLoader().loadResource("Nightfall", HvlSoundType.OGG);
		getSoundLoader().loadResource("Persona", HvlSoundType.OGG);
		getSoundLoader().loadResource("Sky_without_Stars", HvlSoundType.OGG);
		getSoundLoader().loadResource("Torental_River", HvlSoundType.OGG);
		
//		HvlCursor.setNativeHidden(true);
//		HvlCursor.setTexture(getTexture(cursorIndex));
//		HvlCursor.setOffset(-8);
//		HvlCursor.setHeight(16);
//		HvlCursor.setWidth(16);
//		HvlCursor.setRelativeToCamera(true);
		
		pars = new HashMap<>();
		pars.put("TestMap", 30);

		collisionAnimation = new HvlAnimatedTextureUV(getTexture(spikeAnimationIndex), 256, 64, 0.02f);
		collisionAnimation.setAutoStop(true);

		playerAnimation = new HvlAnimatedTextureUV(getTexture(playerAnimationIndex), 256, 62, 0.02f);

		frame1 = new HvlRenderFrame(Display.getWidth(), Display.getDisplayMode().getHeight());
		frame2 = new HvlRenderFrame(Display.getWidth(), Display.getDisplayMode().getHeight());
		bloom1 = new HvlShader(HvlShader.PATH_SHADER_DEFAULT + "bloom1" + HvlShader.SUFFIX_FRAGMENT);
		bloom2 = new HvlShader(HvlShader.PATH_SHADER_DEFAULT + "bloom2" + HvlShader.SUFFIX_FRAGMENT);

		MenuManager.initialize();

		HvlMenuDJ.addSong(MenuManager.main, getSound(0));
		HvlMenuDJ.addSong(MenuManager.main, getSound(1));
		HvlMenuDJ.addSong(MenuManager.main, getSound(2));
		HvlMenuDJ.addSong(MenuManager.game, getSound(3));
		HvlMenuDJ.addSong(MenuManager.game, getSound(4));
		HvlMenuDJ.addSong(MenuManager.game, getSound(5));
		HvlMenuDJ.addSong(MenuManager.game, getSound(6));
		HvlMenuDJ.addSong(MenuManager.game, getSound(7));
		
		
		HvlMenuDJ.addAllSongs(MenuManager.main, MenuManager.splash);
		HvlMenuDJ.addAllSongs(MenuManager.main, MenuManager.levels);
		HvlMenuDJ.addAllSongs(MenuManager.main, MenuManager.options);
		HvlMenuDJ.addAllSongs(MenuManager.game, MenuManager.win);
		HvlMenuDJ.addAllSongs(MenuManager.game, MenuManager.paused);
		HvlMenuDJ.addAllSongs(MenuManager.game, MenuManager.loss);
		
		HvlMenuDJ.setFadeTime(1f);
		
		HvlCamera.setAlignment(HvlCameraAlignment.CENTER);
	}
	
	@Override
	public void update(float delta) {
//		HvlCursor.setColor(new Color(1 - zoom, 1 - zoom, 1 - zoom, 1));
		
		HvlMenuDJ.setVolume(SaveFile.muted ? 0 : 1f);
		
		hvlDrawQuad(HvlCamera.getX() - (Display.getWidth() / 2), HvlCamera.getY() - (Display.getDisplayMode().getHeight() / 2), Display.getWidth(), Display.getDisplayMode().getHeight(),
				new Color(1, 1, 1, getZoom()));

		frame1.setX((int) (HvlCamera.getX() - (Display.getWidth() / 2)));
		frame1.setY((int) (HvlCamera.getY() - (Display.getDisplayMode().getHeight() / 2)));
		HvlRenderFrame.setCurrentRenderFrame(frame1);
		HvlCamera.doTransformation(HvlCameraTransformation.NEGATIVE);
		playerRotation += delta;
		MenuManager.update(delta);
		HvlMenu.updateMenus(delta);
		drawHealthBar(delta);
		HvlCamera.doTransformation(HvlCameraTransformation.UNDONEGATIVE);
		HvlRenderFrame.setCurrentRenderFrame(null);

		HvlRenderFrame.setCurrentRenderFrame(frame2);
		HvlShader.setCurrentShader(bloom1);
		HvlCamera.doTransformation(HvlCameraTransformation.NEGATIVE);
		hvlDrawQuad(0, 0, Display.getWidth(), Display.getDisplayMode().getHeight(), frame1);
		HvlCamera.doTransformation(HvlCameraTransformation.UNDONEGATIVE);
		HvlShader.setCurrentShader(null);
		HvlRenderFrame.setCurrentRenderFrame(null);

		HvlShader.setCurrentShader(bloom2);
		HvlCamera.undoTransform();
		hvlDrawQuad(0, 0, Display.getWidth(), Display.getDisplayMode().getHeight(), frame2);
		HvlCamera.doTransform();
		HvlShader.setCurrentShader(null);

		HvlCamera.doTransformation(HvlCameraTransformation.NEGATIVE);
		hvlDrawQuad(0, 0, Display.getWidth(), Display.getDisplayMode().getHeight(), frame1);
		HvlCamera.doTransformation(HvlCameraTransformation.UNDONEGATIVE);

		drawPlayer(delta);
	}

	public static final float maxZoom = 2f, mapFadeThreshold = 1.2f;
	private static float zoom = maxZoom;
	private float zoomGoal = maxZoom;

	private void drawPlayer(float delta) {
		if (HvlMenu.getCurrent() == MenuManager.game) {
			HvlCamera.setPosition(Player.getX(), Player.getY());
			zoomGoal = maxZoom;
		} else if (HvlMenu.getCurrent() == MenuManager.splash) {
			HvlCamera.setPosition((Display.getWidth() / 2), (Display.getDisplayMode().getHeight() / 2));
			zoomGoal = maxZoom;
			Splash.draw(delta);
		} else {
			HvlCamera.setPosition((Display.getWidth() / 2), (Display.getDisplayMode().getHeight() / 2));
			zoomGoal = 0f;
		}

		zoom = HvlMath.stepTowards(zoom, delta, zoomGoal);
		Game.mapOpacity = zoom > mapFadeThreshold ? (zoom - mapFadeThreshold) / (maxZoom - mapFadeThreshold) : 0;

		HvlCamera.undoTransform();
		float size = HvlMath.lerp(512, Player.radius, Math.min(zoom, 1));
		HvlCoord offset = Splash.getOffset();
		hvlRotate((Display.getWidth() / 2) + offset.x, (Display.getDisplayMode().getHeight() / 2) + offset.y, playerRotation * -4);
		hvlDrawQuad((Display.getWidth() / 2) - size + offset.x, (Display.getDisplayMode().getHeight() / 2) - size + offset.y, size * 2, size * 2, getTexture(player3Index),
				new Color(1, 1, 1, 1 - zoom - (float) Math.sin(playerRotation)));
		hvlResetRotation();
		hvlRotate((Display.getWidth() / 2) + offset.x, (Display.getDisplayMode().getHeight() / 2) + offset.y, -playerRotation * 3);
		hvlDrawQuad((Display.getWidth() / 2) - size + offset.x, (Display.getDisplayMode().getHeight() / 2) - size + offset.y, size * 2, size * 2, getTexture(player2Index),
				new Color(1, 1, 1, 1 - zoom));
		hvlResetRotation();
		hvlRotate((Display.getWidth() / 2) + offset.x, (Display.getDisplayMode().getHeight() / 2) + offset.y, playerRotation * 2);
		hvlDrawQuad((Display.getWidth() / 2) - size + offset.x, (Display.getDisplayMode().getHeight() / 2) - size + offset.y, size * 2, size * 2, getTexture(player1Index),
				new Color(1, 1, 1, 1 - zoom));
		hvlResetRotation();

		float size2 = (size * 2.5f);
		HvlPainter2D.hvlDrawQuad((Display.getWidth() / 2) - (size2 * 2) + offset.x, (Display.getDisplayMode().getHeight() / 2) - (size2 * 2) + offset.y, (size2 * 2) * 2,
				(size2 * 2) * 2, playerAnimation, new Color(1, 1, 1, -(0.9f - zoom)));

//		for(float x = 0; x <= 1.05f; x += 0.05f){
//			for(float y = 0; y <= 1.05f; y += 0.05f){
//				if(HvlMath.distance(x * Display.getWidth(), y * Display.getDisplayMode().getHeight(), Display.getWidth()/2, Display.getDisplayMode().getHeight()/2) < 400){
//					float distance = HvlMath.distance(x * Display.getWidth(), y * Display.getDisplayMode().getHeight(), Display.getWidth()/2, Display.getDisplayMode().getHeight()/2)/400;
//					float direction = (float)Math.atan2((Display.getWidth()/2) - (x * Display.getWidth()), (Display.getDisplayMode().getHeight()/2) - (y * Display.getDisplayMode().getHeight()));
//					hvlRotate(x * Display.getWidth(), y * Display.getDisplayMode().getHeight(), 360 - (float)Math.toDegrees(direction));
//					hvlDrawQuadc(x * Display.getWidth(), y * Display.getDisplayMode().getHeight(), 50, 50 * ((1 - distance)*0.75f), getTexture(aura2Index), new Color(1f, 1f, 1f, Math.max((1 - zoom) * distance * 0.05f, 0)));
//					hvlResetRotation();
//				}
//			}
//		}
		
		HvlCamera.doTransform();
	}

	public static float getZoom() {
		return zoom / maxZoom;
	}

	private static float barProgress = 0;

	public static void resetBar() {
		barProgress = 0;
	}

	private void drawHealthBar(float delta) {
		barProgress = HvlMath.stepTowards(barProgress, delta / 2, Game.getHealthBar());
		if (HvlMenu.getCurrent() == MenuManager.game) {
			HvlCoord offset = new HvlCoord((int) (Player.getPos().x - (Player.getVel().x * delta)), (int) (Player.getPos().y - (Player.getVel().y * delta)));
			hvlDrawLine(offset.x - (Display.getWidth() / 8) - 4, offset.y - (Display.getDisplayMode().getHeight() / 16 * 7), offset.x + (Display.getWidth() / 8) + 4, offset.y
					- (Display.getDisplayMode().getHeight() / 16 * 7), new Color(0.4f, 0.4f, 0.4f, getZoom()), 8);
			hvlDrawLine(offset.x - (Display.getWidth() / 8), offset.y - (Display.getDisplayMode().getHeight() / 16 * 7),
					offset.x - (Display.getWidth() / 8) + ((Display.getWidth() / 4) * barProgress), offset.y - (Display.getDisplayMode().getHeight() / 16 * 7), new Color(0, 0,
							0, getZoom()), 4);
			for(float i = 0; i < 1; i += 1f/((float)pars.get(Game.getCurrentLevel()))){
				hvlDrawLine(
						offset.x - (Display.getWidth() / 8) + (i * (Display.getWidth()/4)), offset.y - (Display.getDisplayMode().getHeight() / 16 * 7) - 4,
						offset.x - (Display.getWidth() / 8) + (i * (Display.getWidth()/4)), offset.y - (Display.getDisplayMode().getHeight() / 16 * 7) + 4, new Color(0.4f, 0.4f, 0.4f, getZoom()), 2);
			}
		}
	}

}
