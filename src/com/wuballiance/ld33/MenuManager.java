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
import com.osreboot.ridhvl.config.HvlConfigUtil;
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

	private static HashMap<HvlComponent, Float> opacity = new HashMap<>();

	public static float textOpacityGoal = 1, textOpacity = 0, menuDecay;

	public static HvlMenu splash, main, levels, options, paused, game, quit, menuGoal, win, loss;

	public static HvlFontPainter2D font;

	public static void initialize(){
		font = new HvlFontPainter2D(HvlTemplateInteg2D.getTexture(Main.fontIndex), HvlFontUtil.SIMPLISTIC, 2048, 2048, 128, 160, 16);

		HvlLabel defaultLabel = new HvlLabel(font, "woops text", Color.white, 0.25f);
		defaultLabel.setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabel)component).setColor(new Color(1, 1, 1, getOpacity(component)));
				component.draw(delta);
			}
		});
		HvlComponentDefault.setDefault(defaultLabel);
		HvlArrangerBox defaultArrangerBox = new HvlArrangerBox(Display.getWidth(), Display.getHeight(), ArrangementStyle.VERTICAL);
		defaultArrangerBox.setBorderU(16);
		defaultArrangerBox.setBorderD(16);
		HvlComponentDefault.setDefault(defaultArrangerBox);
		HvlLabeledButton defaultLabeledButton = new HvlLabeledButton(256, 64, null, null, font, "woops text", Color.white);
		defaultLabeledButton.setTextScale(0.2f);
		defaultLabeledButton.setAlign(0.5f);
		defaultLabeledButton.setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				HvlLabeledButton button = (HvlLabeledButton)component;
				button.setTextColor(new Color(1, 1, 1, getOpacity(component)));
				button.draw(delta);
			}
		});
		HvlComponentDefault.setDefault(defaultLabeledButton);
		HvlButton defaultButton = new HvlButton(64, 64, null, null);
		defaultButton.setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				HvlButton button = (HvlButton)component;
				if(button.getOnDrawable() instanceof HvlTextureDrawable) ((HvlTextureDrawable)button.getOnDrawable()).setColor(new Color(1, 1, 1, getOpacity(component)));
				if(button.getOffDrawable() instanceof HvlTextureDrawable) ((HvlTextureDrawable)button.getOffDrawable()).setColor(new Color(1, 1, 1, getOpacity(component)));
				if(button.getHoverDrawable() instanceof HvlTextureDrawable) ((HvlTextureDrawable)button.getHoverDrawable()).setColor(new Color(1, 1, 1, getOpacity(component)));
				component.draw(delta);
			}
		});
		HvlComponentDefault.setDefault(defaultButton);

		main = new HvlMenu();
		levels = new HvlMenu();
		options = new HvlMenu();
		splash = new HvlMenu();
		game = new HvlMenu(){
			@Override
			public void update(float delta){
				Game.update(delta);
			}
			@Override
			public void draw(float delta){
				Game.draw(delta);
			}
		};
		paused = new HvlMenu();
		quit = new HvlMenu();
		win = new HvlMenu();
		loss = new HvlMenu();

		main.add(new HvlArrangerBox.Builder().build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("left in shadow").build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("begin").setClickedCommand(getMenuLink(levels)).build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("options").setClickedCommand(getMenuLink(options)).build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("quit").setClickedCommand(getMenuLink(quit)).build());
		main.add(new HvlButton.Builder().setOnDrawable(new HvlTextureDrawable(HvlTemplateInteg2D.getTexture(Main.logoInvertIndex))).setHoverDrawable(new HvlTextureDrawable(HvlTemplateInteg2D.getTexture(Main.logoInvertIndex))).setOffDrawable(new HvlTextureDrawable(HvlTemplateInteg2D.getTexture(Main.logoInvertIndex)))
				.setClickedCommand(new HvlAction1<HvlButton>(){
					@Override
					public void run(HvlButton button){
						try{
							if(Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI("www.wuballiance.com"));
						}catch(Exception e){}
					}
				}).setX((float)Display.getWidth() - 96).setY((float)Display.getHeight() - 96).build());

		levels.add(new HvlArrangerBox.Builder().build());
		levels.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("levels").build());
		levels.getFirstChildOfType(HvlArrangerBox.class).add(new HvlSpacer(0, Display.getHeight() / 2));

		//START LEVEL DEFINITIONS
		addLevelButton("1", "FirstSteps", 6, 0, 0, "please put a quote here");
		addLevelButton("2", "Conserve", 9, 1, 0);
		addLevelButton("3", "Rounds", 9, 1, -1);
		addLevelButton("4", "HarrisMap1", 7, 0, -1);
		addLevelButton("5", "Map1", 9, -1, -1);
		addLevelButton("6", "Corners", 9, -1, 0);
		addLevelButton("7", "Loading", 15, -1, 1);
		addLevelButton("8", "HollowPoint", 4, -1, 2);
		addLevelButton("9", "StoppingForce", 6, -2, 2);
		addLevelButton("10", "OneAndOnly", 10, -2, 1);
		addLevelButton("11", "Katamari", 7, -2, 0);
		addLevelButton("12", "Compass", 10, -2, -1);
		addLevelButton("13", "AliveFinally", 7, 2, 0);
		addLevelButton("14", "Clockwise", 7, 2, 1);
		//END LEVEL DEFINITIONS

		levels.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("back").setClickedCommand(getMenuLink(main)).build());

		options.add(new HvlArrangerBox.Builder().build());
		options.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("options").build());
		options.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("sound").setClickedCommand(new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton button){
				SaveFile.muted = !SaveFile.muted;
				HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");
			}
		}).build());
		options.add(new HvlLabeledButton.Builder().setTextScale(0.1f).setX((Display.getWidth()/16*6) - 128).setY(Display.getHeight()/32*15).setText("on").setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabeledButton)component).setTextColor(new Color(1, 1, 1, (getOpacity(component))));
				component.draw(delta);
			}
		}).setClickedCommand(new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton aArg){
				SaveFile.muted = false;
				HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");
			}
		}).build());
		options.add(new HvlLabeledButton.Builder().setTextScale(0.1f).setX((Display.getWidth()/16*10) - 128).setY(Display.getHeight()/32*15).setText("off").setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabeledButton)component).setTextColor(new Color(1, 1, 1, (getOpacity(component))));
				component.draw(delta);
			}
		}).setClickedCommand(new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton aArg){
				SaveFile.muted = true;
				HvlConfigUtil.saveStaticConfig(SaveFile.class, "res/Save.txt");
			}
		}).build());
		options.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("back").setClickedCommand(getMenuLink(main)).build());

		paused.add(new HvlArrangerBox.Builder().build());
		paused.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("paused").build());
		paused.getFirstChildOfType(HvlArrangerBox.class).add(getHighscoreLabel(false));
		paused.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("resume").setClickedCommand(getMenuLink(game)).build());
		paused.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("retry").setClickedCommand(new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton a) {
				Game.reset();
				menuGoal = game;
			}}).build());
		paused.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("quit").setClickedCommand(getMenuLink(main)).build());

		loss.add(new HvlArrangerBox.Builder().build());
		loss.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("depletion death").build());
		loss.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setScale(0.1f).setText("you used all shots").setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabel)component).setColor(new Color(1, 1, 1, getOpacity(component)/1.2f));
				component.draw(delta);
			}
		}).build());
		loss.getFirstChildOfType(HvlArrangerBox.class).add(getHighscoreLabel(false));
		loss.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("retry").setClickedCommand(new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton a) {
				Game.reset();
				menuGoal = game;
			}}).build());
		loss.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("give up").setClickedCommand(getMenuLink(levels)).build());

		win.add(new HvlArrangerBox.Builder().build());
		win.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("total eclipse [victory]").setScale(0.2f).build());
		win.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setScale(0.1f).setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabel)component).setColor(new Color(1, 1, 1, getOpacity(component)/1.2f));
				component.draw(delta);
			}
		}).build());
		win.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setScale(0.1f).setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabel)component).setText("[par " + Main.pars.get(Game.getCurrentLevel()) + "]");
				((HvlLabel)component).setColor(new Color(1, 1, 1, getOpacity(component)/1.2f));
				component.draw(delta);
			}
		}).build());
		win.getFirstChildOfType(HvlArrangerBox.class).add(getHighscoreLabel(true));
		win.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("level select").setClickedCommand(getMenuLink(levels)).build());
		win.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("replay").setClickedCommand(new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton a) {
				Game.reset();
				menuGoal = game;
			}}).build());

		HvlMenu.setCurrent(splash);

		new HvlInput(new HvlInput.HvlInputFilter(){
			@Override
			public float getCurrentOutput(){
				return Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Keyboard.isKeyDown(Keyboard.KEY_P) ? 1 : 0;
			}
		}).setReleasedAction(new HvlAction1<HvlInput>(){
			@Override
			public void run(HvlInput aArg){
				if(Main.getZoom() == 0 || Main.getZoom() == 1) if(HvlMenu.getCurrent() == paused) HvlMenu.setCurrent(game); else if(HvlMenu.getCurrent() == game) HvlMenu.setCurrent(paused);
			}
		});
	}

	public static HvlLabeledButton getOptionsSoundOn(){
		return (HvlLabeledButton)options.getChild(1);
	}

	public static HvlLabeledButton getOptionsSoundOff(){
		return (HvlLabeledButton)options.getChild(2);
	}

	private static ArrayList<HvlComponent> levelButtons = new ArrayList<>();

	public static void update(float delta){
		for(HvlComponent c : opacity.keySet()){
			if(c == getOptionsSoundOn()) opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, 1/(SaveFile.muted ? 1.8f : 1.2f)));
			else if(c == getOptionsSoundOff()) opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, 1/(!SaveFile.muted ? 1.8f : 1.2f)));
			else if(levelButtons.contains(c)){
				opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta*2, (c instanceof HvlButton && ((HvlButton)c).isHovering()) ? 1f : textOpacity + 0.6f));
				if(c instanceof HvlButton && ((HvlButton)c).isHovering()) textOpacityGoal = 0f;
			}else{
				opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, (c instanceof HvlButton && ((HvlButton)c).isHovering()) ? 1f : textOpacity));
				if(c instanceof HvlButton && ((HvlButton)c).isHovering()) textOpacityGoal = 0f;
			}
		}
		textOpacity = HvlMath.stepTowards(textOpacity, delta, textOpacityGoal);
		textOpacityGoal = 1f;

		if(menuGoal != null){
			menuDecay += delta;
			if(menuDecay > 1){
				if(menuGoal == quit) System.exit(0);
				HvlMenu.setCurrent(menuGoal);
				menuDecay = 0;
				menuGoal = null;
				for(HvlComponent c : opacity.keySet()) opacity.put(c, 0f);
				textOpacity = 0;
			}
		}

		Dialogue.update(delta);
	}
	
	public static boolean best = false;
	
	public static HvlLabel getHighscoreLabel(final boolean showBest){
		return new HvlLabel.Builder().setScale(0.1f).setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabel)component).setText(SaveFile.getHighScore(Game.getCurrentLevel()) == -1 ? "no best score yet" : ((best && showBest ? "[new] " : "") + "best " + SaveFile.getHighScore(Game.getCurrentLevel()) + " shots"));
				((HvlLabel)component).setColor(new Color(1, 1, 1, getOpacity(component)/1.2f));
				component.draw(delta);
			}
		}).build();
	}

	private static void addLevelButton(String id, String levelName, int par, int xArg, int yArg, String... dialogue){
		float x = Display.getWidth()/24*((float)xArg + 12);
		float y = Display.getHeight()/16*((float)-yArg + 7.5f);
		Main.pars.put(levelName, par);
		Dialogue cutscene = new Dialogue(new ArrayList<String>(Arrays.asList(dialogue)), game);
		HvlLabeledButton button = new HvlLabeledButton.Builder().setWidth(24).setX(x - 16).setY(y - 8).setText(id).setTextScale(0.1f).setDrawOverride(getLevelButtonDraw(levelName)).setClickedCommand(getLevelLink(dialogue.length > 0 ? cutscene.getMenu() : game, levelName)).build();
		levels.add(button);
		levelButtons.add(button);
	}

	public static float getOpacity(HvlComponent component){
		if(!opacity.containsKey(component)) opacity.put(component, 0f);
		return Math.min(Math.max(opacity.get(component), Math.min(HvlMenu.getCurrent().getTotalTime()/2f, 0.5f)), 1 - menuDecay) - (float)Math.pow(Main.getZoom(), 0.2f);
	}

	public static HvlAction1<HvlButton> getMenuLink(final HvlMenu menu){
		return new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton button){
				menuGoal = menu;
			}
		};
	}

	public static HvlAction1<HvlButton> getLevelLink(final HvlMenu menu, final String level){
		return new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton button){
				Game.setCurrentLevel(level);
				Game.initialize();
				menuGoal = menu;
			}
		};
	}

	public static HvlAction2<HvlComponent, Float> getLevelButtonDraw(final String levelName){
		return new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				((HvlLabeledButton)component).setTextColor(new Color(1, 1, 1, getOpacity(component)/(!SaveFile.isCompleted(levelName) ? 0.8f : 1.4f)));
				component.draw(delta);
			}
		};
	}

}
