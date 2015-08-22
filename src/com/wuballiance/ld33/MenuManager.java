package com.wuballiance.ld33;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.action.HvlAction2;
import com.osreboot.ridhvl.input.HvlInput;
import com.osreboot.ridhvl.menu.HvlComponent;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.HvlLabel;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;


public class MenuManager {
	
	private static HashMap<HvlComponent, Float> opacity = new HashMap<>();
	
	public static float textOpacityGoal = 1, textOpacity = 0, menuDecay;
	
	public static HvlMenu splash, main, levels, options, paused, game, quit, menuGoal;
	
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
		HvlLabeledButton defaultButton = new HvlLabeledButton(256, 64, null, null, font, "woops text", Color.white);
		defaultButton.setTextScale(0.2f);
		defaultButton.setAlign(0.5f);
		defaultButton.setDrawOverride(new HvlAction2<HvlComponent, Float>(){
			@Override
			public void run(HvlComponent component, Float delta){
				HvlLabeledButton button = (HvlLabeledButton)component;
				button.setTextColor(new Color(1, 1, 1, getOpacity(component)));
				button.draw(delta);
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
		
		main.add(new HvlArrangerBox.Builder().build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("left in shadow").build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("levels").setClickedCommand(getMenuLink(levels)).build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("options").build());
		main.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("quit").setClickedCommand(getMenuLink(quit)).build());
		
		levels.add(new HvlArrangerBox.Builder().build());
		levels.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("levels").build());
		levels.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("1").setTextScale(0.1f).setClickedCommand(getMenuLink(game)).build());
		levels.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("back").setClickedCommand(getMenuLink(main)).build());
		
		options.add(new HvlArrangerBox.Builder().build());
		options.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("back").setClickedCommand(getMenuLink(main)).build());
		
		paused.add(new HvlArrangerBox.Builder().build());
		paused.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabel.Builder().setText("paused").build());
		paused.getFirstChildOfType(HvlArrangerBox.class).add(new HvlLabeledButton.Builder().setText("resume").setClickedCommand(getMenuLink(game)).build());
		
		HvlMenu.setCurrent(main);
		
		new HvlInput(new HvlInput.HvlInputFilter(){
			@Override
			public float getCurrentOutput(){
				return Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) ? 1 : 0;
			}
		}).setReleasedAction(new HvlAction1<HvlInput>(){
			@Override
			public void run(HvlInput aArg){
				if(Main.getZoom() == 0 || Main.getZoom() == 1) if(HvlMenu.getCurrent() == paused) HvlMenu.setCurrent(game); else if(HvlMenu.getCurrent() == game) HvlMenu.setCurrent(paused);
			}
		});
	}
	
	public static void update(float delta){
		for(HvlComponent c : opacity.keySet()){
			opacity.put(c, HvlMath.stepTowards(opacity.get(c), delta, (c instanceof HvlLabeledButton && ((HvlLabeledButton)c).isHovering()) ? 1f : textOpacity));
			if(c instanceof HvlLabeledButton && ((HvlLabeledButton)c).isHovering()) textOpacityGoal = 0f;
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
	}
	
	public static float getOpacity(HvlComponent component){
		if(!opacity.containsKey(component)) opacity.put(component, 0f);
		return Math.min(Math.max(opacity.get(component), Math.min(HvlMenu.getCurrent().getTotalTime()/2f, 0.2f)), 1 - menuDecay) - Main.getZoom();
	}
	
	public static HvlAction1<HvlButton> getMenuLink(final HvlMenu menu){
		return new HvlAction1<HvlButton>(){
			@Override
			public void run(HvlButton button){
				menuGoal = menu;
			}
		};
	}
	
}
