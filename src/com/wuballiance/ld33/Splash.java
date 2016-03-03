package com.wuballiance.ld33;

import static com.osreboot.ridhvl.painter.painter2d.HvlPainter2D.*;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class Splash {

	private static float total = 0;

	public static void draw(float delta){
		total += delta;
		
		hvlDrawQuad((Display.getWidth()/2) - 256, (Display.getDisplayMode().getHeight()/2) - 256, 512, 512, HvlTemplateInteg2D.getTexture(Main.logoIndex), new Color(1, 1, 1, (total > 2 ? 1 : total/2) + (total > 4 ? -total + 4 : 0)));
		
		if(total > 6 && HvlMenu.getCurrent() == MenuManager.splash) HvlMenu.setCurrent(MenuManager.main);
	}

	public static HvlCoord getOffset(){
		if(total < 5){
			return new HvlCoord(0, Display.getDisplayMode().getHeight()/3);
		}else if(total < 6){
			return new HvlCoord(0, (Display.getDisplayMode().getHeight()/3) * (-total + 6));
		}else return new HvlCoord(0, 0);
	}

}
