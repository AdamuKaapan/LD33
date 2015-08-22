package com.wuballiance.ld33;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.osreboot.ridhvl.tile.HvlLayeredTileMap;
import com.osreboot.ridhvl.tile.HvlTile;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil.LineSegment;
import com.osreboot.ridhvl.tile.collection.HvlSimpleTile;

public class Game {
	public enum State
	{
		MOVING, WINDUP
	}
	
	private static State state;
	
	private static HvlLayeredTileMap map;
	private static String currentLevel;
	
	public static void reset() {
		Player.reset();
		state = State.WINDUP;
	}
	
	public static void initialize() {
		map = HvlLayeredTileMap.load(currentLevel, true, 0, 0, 32, 32, HvlTemplateInteg2D.getTexture(Main.tilesheetIndex));
		reset();
	}
	
	public static void update(float delta) {
		map.update(delta);
		Player.update(delta);
	}
	
	public static void draw(float delta) {
		map.draw(delta);
		Player.draw(delta);
	}

	public static State getState() {
		return state;
	}

	public static void setState(State currentState) {
		Game.state = currentState;
	}

	public static HvlLayeredTileMap getMap() {
		return map;
	}

	public static String getCurrentLevel() {
		return currentLevel;
	}

	public static void setCurrentLevel(String currentLevel) {
		Game.currentLevel = currentLevel;
	}
	
	public static void applyCollision(float delta, HvlCoord pos, HvlCoord vel, float bounce) throws Exception {
		for (int i = 0; i < 100; i++) {
			List<LineSegment> segs = HvlTilemapCollisionUtil.getAllNearbySides(map, pos.x, pos.y, 1, 1);

			Map<HvlCoord, LineSegment> colls = new HashMap<>();

			for (LineSegment seg : segs) {
				HvlCoord coll = HvlMath.raytrace(pos, new HvlCoord(pos.x + (vel.x * delta), pos.y + (vel.y * delta)), seg.start, seg.end);

				if (coll != null) {
					colls.put(coll, seg);
				}
			}
			if (colls.isEmpty())
				return;

			final HvlCoord tempPos = pos.clone();

			List<HvlCoord> keys = new ArrayList<>();
			for (HvlCoord key : colls.keySet()) {
				if (key == null)
					continue;

				keys.add(key);
			}

			Collections.sort(keys, new Comparator<HvlCoord>() {
				@Override
				public int compare(HvlCoord arg0, HvlCoord arg1) {
					return (int) Math.signum(HvlMath.distance(arg0.x, arg0.y, tempPos.x, tempPos.y) - HvlMath.distance(arg1.x, arg1.y, tempPos.x, tempPos.y));
				}
			});

			HvlCoord coll = keys.get(0);
			LineSegment seg = colls.get(coll);

			float angle = (float) Math.atan2(pos.y - coll.y, pos.x - coll.x);

			float normal = (float) ((Math.PI / 2) + Math.atan2(seg.end.y - seg.start.y, seg.end.x - seg.start.x) % Math.PI);

			float angleOfReflection = normal - angle;

			float oldVel = new HvlCoord(vel.x, vel.y).length();

			float newAngle = angle + 2 * angleOfReflection;

			HvlCoord newDir = new HvlCoord((float) Math.cos(newAngle), (float) Math.sin(newAngle)).normalize().mult(oldVel);
			vel.x = newDir.x * bounce;
			vel.y = newDir.y * bounce;
			HvlCoord mod = vel.normalizeNew();

			pos.x = coll.x + (mod.x * 0.001f);
			pos.y = coll.y + (mod.y * 0.001f);
		}
		throw new Exception("Looped too many times.");
	}
	
	public static void activateTile(int x, int y)
	{
		if (x < 0 || x >= map.getLayer(1).getMapWidth() || y < 0 || y >= map.getLayer(1).getMapHeight()) return;
		
		HvlTile tile = map.getLayer(0).getTile(x, y);
		
		HvlSimpleTile st = (HvlSimpleTile) tile;
		
		if (st.getTile() == 0)
		{
			map.getLayer(1).setTile(x, y, new HvlSimpleTile(18));
		}
	}
}
