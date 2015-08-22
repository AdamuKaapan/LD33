package com.wuballiance.ld33;
import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;


public class MenuManager {
	
	public static HvlMenu splash, main, game;
	
	public static HvlFontPainter2D font;
	
	public static void initialize(){
		font = new HvlFontPainter2D(HvlTemplateInteg2D.getTexture(Main.fontIndex), HvlFontUtil.SIMPLISTIC, 2048, 2048, 128, 192, 16);
		
		main = new HvlMenu();
		splash = new HvlMenu();
		game = new HvlMenu(){
			@Override
			public void update(float delta){
				Game.update(delta);
			}
		};
		
		HvlMenu.setCurrent(main);
	}
	
}
