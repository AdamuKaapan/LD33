package com.wuballiance.ld33;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.action.HvlAction2;
import com.osreboot.ridhvl.configold.HvlConfigUtil;
import com.osreboot.ridhvl.input.HvlInput;
import com.osreboot.ridhvl.menu.HvlComponent;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.HvlLabel;
import com.osreboot.ridhvl.menu.component.HvlSpacer;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;
import com.osreboot.ridhvl.menu.component.collection.HvlTextureDrawable;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class MenuManager {

	public static HashMap<String, String> nextLevel = new HashMap<>();
	private static HashMap<HvlComponent, Float> opacity = new HashMap<>();

	public static float textOpacityGoal = 1, textOpacity = 0, menuDecay;

	public static HvlMenu splash, main, levels, options, paused, game, quit, menuGoal, win, loss;

	public static HvlFontPainter2D font;

	public static void initialize() {
		font = new HvlFontPainter2D(HvlTemplateInteg2D.getTexture(Main.fontIndex), HvlFontUtil.SIMPLISTIC, 2048, 2048, 128, 160, 16);

		HvlLabel defaultLabel = new HvlLabel(font, "woops text", 0.25f, Color.white);
		defaultLabel.setDrawOverride(new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				((HvlLabel) component).setColor(new Color(1, 1, 1, getOpacity(component)));
				component.draw(delta);
			}
		});
		HvlComponentDefault.setDefault(defaultLabel);
		HvlArrangerBox defaultArrangerBox = new HvlArrangerBox(Display.getWidth(), Display.getDisplayMode().getHeight(), ArrangementStyle.VERTICAL);
		defaultArrangerBox.setBorderU(16);
		defaultArrangerBox.setBorderD(16);
		HvlComponentDefault.setDefault(defaultArrangerBox);
		HvlLabeledButton defaultLabeledButton = new HvlLabeledButton(256, 64, null, null, font, "woops text", Color.white);
		defaultLabeledButton.setTextScale(0.2f);
		defaultLabeledButton.setAlign(0.5f);
		defaultLabeledButton.setDrawOverride(new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				HvlLabeledButton button = (HvlLabeledButton) component;
				button.setTextColor(new Color(1, 1, 1, getOpacity(component)));
				button.draw(delta);
			}
		});
		HvlComponentDefault.setDefault(defaultLabeledButton);
		HvlButton defaultButton = new HvlButton(64, 64, null, null);
		defaultButton.setDrawOverride(new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				HvlButton button = (HvlButton) component;
				if (button.getOnDrawable() instanceof HvlTextureDrawable)
					((HvlTextureDrawable) button.getOnDrawable()).setColor(new Color(1, 1, 1, getOpacity(component)));
				if (button.getOffDrawable() instanceof HvlTextureDrawable)
					((HvlTextureDrawable) button.getOffDrawable()).setColor(new Color(1, 1, 1, getOpacity(component)));
				if (button.getHoverDrawable() instanceof HvlTextureDrawable)
					((HvlTextureDrawable) button.getHoverDrawable()).setColor(new Color(1, 1, 1, getOpacity(component)));
				component.draw(delta);
			}
		});
		HvlComponentDefault.setDefault(defaultButton);

		main = new HvlMenu();
		levels = new HvlMenu();
		options = new HvlMenu();
		splash = new HvlMenu();
		game = new HvlMenu() {
			@Override
			public void update(float delta) {
				Game.update(delta);
			}

			@Override
			public void draw(float delta) {
				Game.draw(delta);
			}
		};
		paused = new HvlMenu();
		quit = new HvlMenu();
		win = new HvlMenu();
		loss = new HvlMenu();

		main.add(new HvlArrangerBox.Builder().build());
		main.getFirstArrangerBox().add(new HvlLabel.Builder().setText("tenebrous expanse").build());
		main.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("begin").setClickedCommand(getMenuLink(levels)).build());
		main.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("options").setClickedCommand(getMenuLink(options)).build());
		main.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("quit").setClickedCommand(getMenuLink(quit)).build());
		main.add(new HvlButton.Builder().setOnDrawable(new HvlTextureDrawable(HvlTemplateInteg2D.getTexture(Main.logoInvertIndex)))
				.setHoverDrawable(new HvlTextureDrawable(HvlTemplateInteg2D.getTexture(Main.logoInvertIndex)))
				.setOffDrawable(new HvlTextureDrawable(HvlTemplateInteg2D.getTexture(Main.logoInvertIndex))).setClickedCommand(new HvlAction1<HvlButton>() {
					@Override
					public void run(HvlButton button) {
						try {
							if (Desktop.isDesktopSupported())
								Desktop.getDesktop().browse(new URI("www.wuballiance.com"));
						} catch (Exception e) {
						}
					}
				}).setX((float) Display.getWidth() - 96).setY((float) Display.getDisplayMode().getHeight() - 96).build());

		levels.add(new HvlArrangerBox.Builder().build());
		levels.getFirstArrangerBox().add(new HvlLabel.Builder().setText("levels").build());
		levels.getFirstArrangerBox().add(new HvlSpacer(0, Display.getDisplayMode().getHeight() / 2));

		// START LEVEL DEFINITIONS
		addLevelButton("1", "Maps/FirstSteps",		"2",	6, 		0, 0, "you are darkness... and in your presence no light can be shed");
		addLevelButton("2", "Maps/AliveFinally",	"3",	8, 		1, 0);
		addLevelButton("3", "Maps/Conserve",		"4",	9, 		1, -1);
		addLevelButton("4", "Maps/Corners",			"5",	11, 	0, -1, "to fight evil you must understand the dark", "(nalini singh)");
		addLevelButton("5", "Maps/Rounds",			"6",	7, 		-1, -1);
		addLevelButton("6", "Maps/HollowPoint",		"7",	5, 		-1, 0);
		addLevelButton("7", "Maps/StoppingForce",	"8",	6, 		-1, 1);
		addLevelButton("8", "Maps/Loading",			"9",	13, 	0, 1);
		addLevelButton("9", "Maps/Map1",			"10",	6, 		1, 1);
		addLevelButton("10", "Maps/OneAndOnly",		"11",	8, 		2, 1, "darkness does not age... nothing is always nothing", "(dejan stojanovic)");
		addLevelButton("11", "Maps/Katamari",		"12",	6, 		2, 0);
		addLevelButton("12", "Maps/Compass",		"13",	4, 		2, -1, "fear can only grow in darkness");
		addLevelButton("13", "Maps/Spiral",			"14",	9, 		1, -2);
		addLevelButton("14", "Maps/Zig",			"15",	6, 		-1, 2);
		addLevelButton("15", "Maps/Clockwise",		"16",	5, 		-2, 1);
		addLevelButton("16", "Maps/ChainReaction",	"17",	3, 		-2, 0);
		addLevelButton("17", "Maps/HarrisMap1",		"",		5, 		-2, -1);
		// END LEVEL DEFINITIONS

		levels.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("back").setClickedCommand(getMenuLink(main)).build());

		options.add(new HvlArrangerBox.Builder().build());
		options.getFirstArrangerBox().add(new HvlLabel.Builder().setText("options").build());
		options.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("sound").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton button) {
				SaveFile.muted = !SaveFile.muted;
				HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");
			}
		}).build());
		options.add(new HvlLabeledButton.Builder().setTextScale(0.1f).setX((Display.getWidth() / 16 * 6) - 128).setY(Display.getDisplayMode().getHeight() / 32 * 15)
				.setText("on").setDrawOverride(new HvlAction2<HvlComponent, Float>() {
					@Override
					public void run(HvlComponent component, Float delta) {
						((HvlLabeledButton) component).setTextColor(new Color(1, 1, 1, (getOpacity(component))));
						component.draw(delta);
					}
				}).setClickedCommand(new HvlAction1<HvlButton>() {
					@Override
					public void run(HvlButton aArg) {
						SaveFile.muted = false;
						HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");
					}
				}).build());
		options.add(new HvlLabeledButton.Builder().setTextScale(0.1f).setX((Display.getWidth() / 16 * 10) - 128).setY(Display.getDisplayMode().getHeight() / 32 * 15)
				.setText("off").setDrawOverride(new HvlAction2<HvlComponent, Float>() {
					@Override
					public void run(HvlComponent component, Float delta) {
						((HvlLabeledButton) component).setTextColor(new Color(1, 1, 1, (getOpacity(component))));
						component.draw(delta);
					}
				}).setClickedCommand(new HvlAction1<HvlButton>() {
					@Override
					public void run(HvlButton aArg) {
						SaveFile.muted = true;
						HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");
					}
				}).build());
		options.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("back").setClickedCommand(getMenuLink(main)).build());

		paused.add(new HvlArrangerBox.Builder().build());
		paused.getFirstArrangerBox().add(new HvlLabel.Builder().setText("paused").build());
		paused.getFirstArrangerBox().add(getHighscoreLabel(false));
		paused.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("resume").setClickedCommand(getMenuLink(game)).build());
		paused.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("retry").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				Game.reset();
				menuGoal = game;
			}
		}).build());
		paused.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("skip").setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabeledButton) component).setTextColor(new Color(1, 1, 1, getOpacity(component) / (nextLevel.containsKey(Game.getCurrentLevel()) ? 1f : 1.8f)));
				component.draw(delta);
			}
		}).setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				goToNextLevel();
			}
		}).build());
		paused.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("quit").setClickedCommand(getMenuLink(main)).build());

		loss.add(new HvlArrangerBox.Builder().build());
		loss.getFirstArrangerBox().add(new HvlLabel.Builder().setText("depletion death").build());
		loss.getFirstArrangerBox().add(
				new HvlLabel.Builder().setScale(0.1f).setText("you used all shots").setDrawOverride(new HvlAction2<HvlComponent, Float>() {
					@Override
					public void run(HvlComponent component, Float delta) {
						((HvlLabel) component).setColor(new Color(1, 1, 1, getOpacity(component) / 1.2f));
						component.draw(delta);
					}
				}).build());
		loss.getFirstArrangerBox().add(getHighscoreLabel(false));
		loss.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("retry").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				Game.reset();
				menuGoal = game;
			}
		}).build());
		loss.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("skip").setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabeledButton) component).setTextColor(new Color(1, 1, 1, getOpacity(component) / (nextLevel.containsKey(Game.getCurrentLevel()) ? 1f : 1.8f)));
				component.draw(delta);
			}
		}).setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				goToNextLevel();
			}
		}).build());
		loss.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("quit").setClickedCommand(getMenuLink(levels)).build());

		win.add(new HvlArrangerBox.Builder().build());
		win.getFirstArrangerBox().add(new HvlLabel.Builder().setText("total eclipse").setScale(0.2f).build());
		win.getFirstArrangerBox().add(new HvlLabel.Builder().setScale(0.1f).setDrawOverride(new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				((HvlLabel) component).setColor(new Color(1, 1, 1, getOpacity(component) / 1.2f));
				component.draw(delta);
			}
		}).build());
		win.getFirstArrangerBox().add(new HvlLabel.Builder().setScale(0.1f).setDrawOverride(new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				((HvlLabel) component).setText("[par " + Main.pars.get(Game.getCurrentLevel()) + "]");
				((HvlLabel) component).setColor(new Color(1, 1, 1, getOpacity(component) / 1.2f));
				component.draw(delta);
			}
		}).build());
		win.getFirstArrangerBox().add(getHighscoreLabel(true));
		win.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("next").setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabeledButton) component).setTextColor(new Color(1, 1, 1, getOpacity(component) / (nextLevel.containsKey(Game.getCurrentLevel()) ? 1f : 1.8f)));
				component.draw(delta);
			}
		}).setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				goToNextLevel();
			}
		}).build());
		win.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("level select").setClickedCommand(getMenuLink(levels)).build());
		win.getFirstArrangerBox().add(new HvlLabeledButton.Builder().setText("replay").setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				Game.reset();
				menuGoal = game;
			}
		}).build());

		HvlMenu.setCurrent(splash);

		new HvlInput(new HvlInput.HvlInputFilter() {
			@Override
			public float getCurrentOutput() {
				return Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Keyboard.isKeyDown(Keyboard.KEY_P) ? 1 : 0;
			}
		}).setReleasedAction(new HvlAction1<HvlInput>() {
			@Override
			public void run(HvlInput aArg) {
				if (Main.getZoom() == 0 || Main.getZoom() == 1)
					if (HvlMenu.getCurrent() == paused)
						HvlMenu.setCurrent(game);
					else if (HvlMenu.getCurrent() == game)
						HvlMenu.setCurrent(paused);
			}
		});
	}

	public static void goToNextLevel(){
		if(nextLevel.containsKey(Game.getCurrentLevel())){
			for(HvlComponent b : levelButtons){
				if(b instanceof HvlLabeledButton && ((HvlLabeledButton)b).getText().equals(nextLevel.get(Game.getCurrentLevel()))){
					((HvlLabeledButton)b).getClickedCommand().run((HvlLabeledButton)b);
					break;
				}
			}
		}
	}
	
	public static HvlLabeledButton getOptionsSoundOn() {
		return (HvlLabeledButton) options.getChild(1);
	}

	public static HvlLabeledButton getOptionsSoundOff() {
		return (HvlLabeledButton) options.getChild(2);
	}

	private static ArrayList<HvlComponent> levelButtons = new ArrayList<>();

	public static void update(float delta) {
		for (HvlComponent c : opacity.keySet()) {
			if (c == getOptionsSoundOn())
				opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, 1 / (SaveFile.muted ? 1.8f : 1.2f)));
			else if (c == getOptionsSoundOff())
				opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, 1 / (!SaveFile.muted ? 1.8f : 1.2f)));
			else if (levelButtons.contains(c)) {
				opacity.put(c,
						HvlMath.stepTowards(opacity.get(c), delta * 2, (c instanceof HvlButton && ((HvlButton) c).isHovering()) ? 1f : textOpacity + 0.6f));
				if (c instanceof HvlButton && ((HvlButton) c).isHovering())
					textOpacityGoal = 0f;
			} else {
				opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, (c instanceof HvlButton && ((HvlButton) c).isHovering()) ? 1f : textOpacity));
				if (c instanceof HvlButton && ((HvlButton) c).isHovering())
					textOpacityGoal = 0f;
			}
		}
		textOpacity = HvlMath.stepTowards(textOpacity, delta, textOpacityGoal);
		textOpacityGoal = 1f;

		if (menuGoal != null) {
			menuDecay += delta;
			if (menuDecay > 1) {
				if (menuGoal == quit)
					System.exit(0);
				HvlMenu.setCurrent(menuGoal);
				menuDecay = 0;
				menuGoal = null;
				for (HvlComponent c : opacity.keySet())
					opacity.put(c, 0f);
				textOpacity = 0;
			}
		}

		Dialogue.update(delta);
	}

	public static boolean best = false;

	public static HvlLabel getHighscoreLabel(final boolean showBest) {
		return new HvlLabel.Builder().setScale(0.1f).setDrawOverride(new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				((HvlLabel) component).setText(SaveFile.getHighScore(Game.getCurrentLevel()) == -1 ? "no best score yet" : ((best && showBest ? "[new] " : "")
						+ "best " + SaveFile.getHighScore(Game.getCurrentLevel()) + " shots"));
				((HvlLabel) component).setColor(new Color(1, 1, 1, getOpacity(component) / 1.2f));
				component.draw(delta);
			}
		}).build();
	}

	private static void addLevelButton(String id, String levelName, String nextLevelArg, int par, int xArg, int yArg, String... dialogue) {
		if(nextLevelArg != "") nextLevel.put(levelName, nextLevelArg);
		float x = Display.getWidth() / 24 * ((float) xArg + 12);
		float y = Display.getDisplayMode().getHeight() / 16 * ((float) -yArg + 7.5f);
		Main.pars.put(levelName, par);
		Dialogue cutscene = new Dialogue(new ArrayList<String>(Arrays.asList(dialogue)), game);
		HvlLabeledButton button = new HvlLabeledButton.Builder().setWidth(24).setX(x - 16).setY(y - 8).setText(id).setTextScale(0.1f)
				.setDrawOverride(getLevelButtonDraw(levelName)).setClickedCommand(getLevelLink(dialogue.length > 0 ? cutscene.getMenu() : game, levelName))
				.build();
		levels.add(button);
		levelButtons.add(button);
	}

	public static float getOpacity(HvlComponent component) {
		if (!opacity.containsKey(component))
			opacity.put(component, 0f);
		return Math.min(Math.max(opacity.get(component), Math.min(HvlMenu.getCurrent().getTotalTime() / 2f, 0.5f)), 1 - menuDecay)
				- (float) Math.pow(Main.getZoom(), 0.2f);
	}

	public static HvlAction1<HvlButton> getMenuLink(final HvlMenu menu) {
		return new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton button) {
				menuGoal = menu;
			}
		};
	}

	public static HvlAction1<HvlButton> getLevelLink(final HvlMenu menu, final String level) {
		return new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton button) {
				Game.setCurrentLevel(level);
				Game.initialize();
				menuGoal = menu;
			}
		};
	}

	public static HvlAction2<HvlComponent, Float> getLevelButtonDraw(final String levelName) {
		return new HvlAction2<HvlComponent, Float>() {
			@Override
			public void run(HvlComponent component, Float delta) {
				((HvlLabeledButton) component).setTextColor(new Color(1, 1, 1, getOpacity(component) / (!SaveFile.isCompleted(levelName) ? 0.8f : 1.4f)));
				component.draw(delta);
			}
		};
	}

}
