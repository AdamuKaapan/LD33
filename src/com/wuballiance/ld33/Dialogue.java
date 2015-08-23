package com.wuballiance.ld33;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.menu.HvlMenu;

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
			ArrayList<String> currentWords = new ArrayList<String>(Arrays.asList(words.get(index).split(" ")));
			ArrayList<String> currentLines = new ArrayList<String>();
			currentLines.add("");
			while(currentWords.size() > 0){
				if((currentLines.get(currentLines.size() - 1) + " " + currentWords.get(0)).length() < 40){
					currentLines.set(currentLines.size() - 1, (currentLines.get(currentLines.size() - 1) + " " + currentWords.get(0)));
				}else{
					currentLines.add(currentWords.get(0));
				}
				currentWords.remove(0);
			}
			for(String s : currentLines) MenuManager.font.drawWord(s, (Display.getWidth()/2) - (MenuManager.font.getLineWidth(s) * 0.05f), (Display.getHeight()/2) - (currentLines.size() * 8) + (currentLines.indexOf(s)*32), 0.1f, new Color(1, 1, 1, Math.min(fade, 1) - Math.max(0, fade - 4)));
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
