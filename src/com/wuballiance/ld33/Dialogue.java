package com.wuballiance.ld33;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.painter.HvlCamera;
import com.osreboot.ridhvl.painter.HvlCamera.HvlCameraTransformation;

public class Dialogue {
	
	private static ArrayList<Dialogue> dialogues = new ArrayList<Dialogue>();

	public static void update(float delta){
		for(Dialogue d : dialogues) if(d.getMenu() == HvlMenu.getCurrent()) d.draw(delta);
	}
	
	private ArrayList<String> words;
	private float fade;
	private int index;
	private HvlMenu menu, link;
	
	public Dialogue(ArrayList<String> wordsArg, HvlMenu linkArg){
		menu = new HvlMenu();
		words = wordsArg;
		link = linkArg;
		index = 0;
		fade = 0;
		dialogues.add(this);
	}

	public void draw(float delta){
		if(fade < 5){
			fade += delta;//TODO multiple lines
			MenuManager.font.drawWord(words.get(index), (Display.getWidth()/2) - (MenuManager.font.getLineWidth(words.get(index)) * 0.05f), (Display.getHeight()/2), 0.1f, new Color(1, 1, 1, Math.min(fade, 1) - Math.max(0, fade - 4)));
		}else{
			index++;
			fade = 0;
			if(index == words.size()){
				index = 0;
				fade = 0;
				HvlMenu.setCurrent(link);
			}
		}
	}
	
	public HvlMenu getMenu(){
		return menu;
	}

	public HvlMenu getLink(){
		return link;
	}
	
}
