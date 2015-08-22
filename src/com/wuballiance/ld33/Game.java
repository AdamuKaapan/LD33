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
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil.LineSegment;
import com.osreboot.ridhvl.tile.collection.HvlSimpleTile;

public class Game {	
	public enum State
	{
		MOVING, WINDUP
	}
	
	public static final int offTile = 8, onTile = 16, largeExplosionTile = 62, smallExplosionTile = 63;
	
	private static int currentTurn;
	private static int par;
	
	private static State state;
	
	private static HvlLayeredTileMap map;
	private static String currentLevel;
	
	public static float mapOpacity = 0f;
	
	public static void reset() {
		Player.reset();
		currentTurn = 0;
		par = 3;
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
		for(int i = 0; i < map.getLayerCount(); i++) map.getLayer(i).setOpacity(mapOpacity);
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
	
	public static final float NO_COLLISION = Float.MAX_VALUE;
	
	public static float applyCollision(float delta, HvlCoord pos, HvlCoord vel, float bounce) throws Exception {
		float toReturn = NO_COLLISION;
		
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
				return toReturn;

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
			
			toReturn = (float) Math.toDegrees(normal);
			Player.collisionAnimationPos.x = coll.x;
			Player.collisionAnimationPos.y = coll.y;
		}
		throw new Exception("Looped too many times.");
	}
	
	public static void activateTile(float x, float y, float radius)
	{
		activateSingleTile(x, y);
		activateSingleTile(x + radius, y);
		activateSingleTile(x - radius, y);
		activateSingleTile(x, y + radius);
		activateSingleTile(x, y - radius);
		activateSingleTile(x + (float) Math.sqrt(2) * radius, y + (float) Math.sqrt(2) * radius);
		activateSingleTile(x + (float) Math.sqrt(2) * radius, y - (float) Math.sqrt(2) * radius);
		activateSingleTile(x - (float) Math.sqrt(2) * radius, y + (float) Math.sqrt(2) * radius);
		activateSingleTile(x - (float) Math.sqrt(2) * radius, y - (float) Math.sqrt(2) * radius);
	}
	
	public static void activateSingleTile(float xArg, float yArg)
	{
		int x = map.toTileX(xArg);
		int y = map.toTileY(yArg);
		
		if (x < 0 || x >= map.getLayer(0).getMapWidth() || y < 0 || y >= map.getLayer(0).getMapHeight()) return;
		
		HvlSimpleTile st0 = (HvlSimpleTile) map.getLayer(0).getTile(x, y);
		HvlSimpleTile st2 = (HvlSimpleTile) map.getLayer(2).getTile(x, y);
		
		if (st0 != null && st0.getTile() == offTile)
		{
			map.getLayer(0).setTile(x, y, new HvlSimpleTile(onTile));
		}
		if (st2 != null && st2.getTile() == smallExplosionTile)
		{
			map.getLayer(2).setTile(x, y, null);
			activateSmallExplosion(x, y);
		}
		if (st2 != null && st2.getTile() == largeExplosionTile)
		{
			map.getLayer(2).setTile(x, y, null);
			activateLargeExplosion(xArg, yArg);
		}
	}

	public static void activateSmallExplosion(int x, int y)
	{
		for (int xI = -2; xI < 3; xI++)
		{
			for (int yI = -2; yI < 3; yI++)
			{
				activateSingleTile((x + xI) * map.getTileWidth(), (y + yI) * map.getTileHeight());
			}
		}
	}
	
	public static void activateLargeExplosion(float x, float y)
	{
		int angleSubdivisions = 3;
		float angleVar = (float) Math.toRadians(30.0f);
		
		float angle = (float)Math.atan2(y - Player.getY(), x - Player.getX());
		
		for (float theta = angle - angleVar; theta <= angle + angleVar; theta += angleVar / angleSubdivisions)
		{
			for (int i = 0; i < 10; i++)
			{
				activateSingleTile(x + (float)(Math.cos(theta) * map.getTileWidth() * i * 0.5f), y + (float)(Math.sin(theta) * map.getTileHeight() * i * 0.5f));
			}
		}
		
//		for (float theta = angle - angleVar; theta <= angle + angleVar; theta += angleVar / angleSubdivisions)
//		{			
//			for (int i = 0; i < 10; i++)
//			{
//				float tX = x + (float) (Math.cos(theta) * (map.getTileWidth() * 0.5f * i));
//				float tY = y + (float) (Math.sin(theta) * (map.getTileHeight() * 0.5f * i));
//				
//				activateSingleTile(map.toTileX(tX), map.toTileY(tY));
//			}
//		}
	}
	
	public static int getCurrentTurn() {
		return currentTurn;
	}

	public static void setCurrentTurn(int currentTurn) {
		Game.currentTurn = currentTurn;
	}
	
	public static void onEndTurn() {
		boolean win = true;
		
		for (int x = 0; x < map.getLayer(0).getMapWidth(); x++)
		{
			for (int y = 0; y < map.getLayer(0).getMapHeight(); y++)
			{
				HvlSimpleTile t = (HvlSimpleTile) map.getLayer(0).getTile(x, y);
				
				if (t.getTile() == offTile)
				{
					win = false;
					break;
				}
			}
		}

		if (win)
		{
			onWin();
		}
		else if (Game.currentTurn >= Game.par)
		{
			onLose();
		}
	}
	
	private static void onWin() {
		System.out.println("Win!");
	}
	
	private static void onLose() {
		System.out.println("... you failed. Stalemate.");
	}
}
