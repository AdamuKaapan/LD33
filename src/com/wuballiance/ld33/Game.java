package com.wuballiance.ld33;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlCoord;
import com.osreboot.ridhvl.HvlMath;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.particle.HvlParticle;
import com.osreboot.ridhvl.particle.collection.HvlLinearPositionProvider;
import com.osreboot.ridhvl.particle.collection.HvlRectanglePositionProvider;
import com.osreboot.ridhvl.particle.collection.HvlSimpleParticle;
import com.osreboot.ridhvl.particle.collection.HvlSimpleParticleSystem;
import com.osreboot.ridhvl.particle.correlation.HvlParticleCorrelator;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;
import com.osreboot.ridhvl.tile.HvlLayeredTileMap;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil;
import com.osreboot.ridhvl.tile.HvlTilemapCollisionUtil.LineSegment;
import com.osreboot.ridhvl.tile.collection.HvlSimpleTile;

public class Game {
	public enum State {
		MOVING, WINDUP
	}

	public static class TileCoord {
		int x, y;

		public TileCoord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TileCoord other = (TileCoord) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	public static final int offTile = 8, onTile = 16, largeExplosionTile = 62, smallExplosionTile = 63;

	public static List<HvlSimpleParticleSystem> particles;

	private static Map<TileCoord, Float> opacities;

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
		particles = new LinkedList<>();
		opacities = new HashMap<>();
		map = HvlLayeredTileMap.load(currentLevel, true, 0, 0, 32, 32, HvlTemplateInteg2D.getTexture(Main.tilesheetIndex));

		for (int x = 0; x < map.getLayer(1).getMapWidth(); x++) {
			for (int y = 0; y < map.getLayer(1).getMapHeight(); y++) {

				if (!map.getLayer(1).isTileInLocation(x, y))
					continue;

				HvlSimpleTile t = (HvlSimpleTile) map.getLayer(1).getTile(x, y);

				switch (t.getTile()) {
				case 2:
					particles.add(generateWallParticles(new HvlCoord(x * map.getTileWidth(), y * map.getTileHeight() + (map.getTileHeight() / 4)),
							new HvlCoord((x + 1) * map.getTileWidth(), y * map.getTileHeight() + (map.getTileHeight() / 4))));
					break;
				case 18:
					particles.add(generateWallParticles(new HvlCoord((x + 1) * map.getTileWidth(), y * map.getTileHeight() + (3 * map.getTileHeight() / 4)),
							new HvlCoord(x * map.getTileWidth(), y * map.getTileHeight() + (3 * map.getTileHeight() / 4))));
					break;
				case 9:
					particles.add(generateWallParticles(new HvlCoord(x * map.getTileWidth() + (map.getTileWidth() / 4), (y + 1) * map.getTileHeight()),
							new HvlCoord(x * map.getTileWidth() + (map.getTileWidth() / 4), y * map.getTileHeight())));
					break;
				case 11:
					particles.add(generateWallParticles(new HvlCoord(x * map.getTileWidth() + (3 * map.getTileWidth() / 4), y * map.getTileHeight()),
							new HvlCoord(x * map.getTileWidth() + (3 * map.getTileWidth() / 4), (y + 1) * map.getTileHeight())));
					break;
				case 25:
					particles.add(generateWallParticles(new HvlCoord(x * map.getTileWidth() + (map.getTileWidth() / 4), (y + 1) * map.getTileHeight()),
							new HvlCoord((x + 1) * map.getTileWidth(), y * map.getTileHeight() + (map.getTileHeight() / 4))));
					break;
				case 26:
					particles.add(generateWallParticles(new HvlCoord(x * map.getTileWidth(), y * map.getTileHeight() + (map.getTileHeight() / 4)),
							new HvlCoord(x * map.getTileWidth() + (3 * map.getTileWidth() / 4), (y + 1) * map.getTileHeight())));
					break;
				case 33:
					particles.add(generateWallParticles(new HvlCoord((x + 1) * map.getTileWidth(), y * map.getTileHeight() + (3 * map.getTileHeight() / 4)),
							new HvlCoord(x * map.getTileWidth() + (map.getTileWidth() / 4), y * map.getTileHeight())));
					break;
				case 34:
					particles.add(generateWallParticles(new HvlCoord(x * map.getTileWidth() + (3 * map.getTileWidth() / 4), y * map.getTileHeight()),
							new HvlCoord(x * map.getTileWidth(), y * map.getTileHeight() + (3 * map.getTileHeight() / 4))));
					break;
				}
			}
		}

		reset();

		Player.initialize();
	}

	public static void update(float delta) {
		map.update(delta);
		Player.update(delta);

		for (Map.Entry<TileCoord, Float> entry : opacities.entrySet())
		{
			entry.setValue(Math.min(1.0f, entry.getValue() + delta * 2));
		}
	}

	public static void draw(float delta) {
		for (int i = 0; i < map.getLayerCount(); i++)
			map.getLayer(i).setOpacity(mapOpacity);
		map.draw(delta);
		for (int x = map.toTileX(Player.getX() - (Display.getWidth() / 2)) - 1; x < map.toTileX(Player.getX() + (Display.getWidth() / 2)) + 1; x++) {
			for (int y = map.toTileY(Player.getY() - (Display.getHeight() / 2)) - 1; y < map.toTileY(Player.getY() + (Display.getHeight() / 2)) + 1; y++) {
				if (x < 0 || y < 0 || x >= map.getLayer(0).getMapWidth() || y >= map.getLayer(0).getMapHeight() || !map.isTileInLocation(x, y, 0, 1, 2)) {
					float black = isTileBlacked(x, y);

					if (black >= 0.0f) {
						if (!opacities.containsKey(new TileCoord(x, y)))
						{
							opacities.put(new TileCoord(x, y), -black);
						}

						HvlPainter2D.hvlDrawQuad(x * map.getTileWidth(), y * map.getTileHeight(), map.getTileWidth(), map.getTileHeight(), new Color(0, 0, 0, Math.max(0.0f, opacities.get(new TileCoord(x, y)))));
					}
				}
			}
		}
		for (HvlSimpleParticleSystem ps : particles) {
			ps.draw(delta);
		}
		Player.draw(delta);
	}

	private static float isTileBlacked(final int xArg, final int yArg) {
		int radius = 1;

		List<TileCoord> found = new ArrayList<>();

		while (true) {
			for (int x = -radius; x < radius + 1; x++) {
				for (int y = -radius; y < radius + 1; y++) {
					if (xArg + x < 0 || xArg + x >= map.getLayer(0).getMapWidth() || yArg + y < 0 || yArg + y >= map.getLayer(0).getMapHeight())
						continue;

					if (!map.getLayer(0).isTileInLocation(xArg + x, yArg + y))
						continue;

					HvlSimpleTile t = (HvlSimpleTile) map.getLayer(0).getTile(xArg + x, yArg + y);
					if (t.getTile() == offTile || t.getTile() == onTile)
						found.add(new TileCoord(xArg + x, yArg + y));
				}
			}

			if (!found.isEmpty()) {
				Collections.sort(found, new Comparator<TileCoord>() {

					@Override
					public int compare(TileCoord arg0, TileCoord arg1) {
						return (int) Math.signum(HvlMath.distance(map.toWorldX(arg0.x) + (map.getTileWidth() / 2), map.toWorldY(arg0.y) + (map.getTileHeight() / 2),
								map.toWorldX(xArg) + (map.getTileWidth() / 2), map.toWorldY(yArg) + (map.getTileHeight() / 2))
								- HvlMath.distance(map.toWorldX(arg1.x) + (map.getTileWidth() / 2), map.toWorldY(arg1.y) + (map.getTileHeight() / 2), map.toWorldX(xArg)
										+ (map.getTileWidth() / 2), map.toWorldY(yArg) + (map.getTileHeight() / 2)));
					}});

				HvlSimpleTile t = (HvlSimpleTile) map.getLayer(0).getTile(found.get(0).x, found.get(0).y);
				if (t.getTile() == onTile)
				{
					return HvlMath.distance(map.toWorldX(found.get(0).x) + (map.getTileWidth() / 2), map.toWorldY(found.get(0).y) + (map.getTileHeight() / 2),
							map.toWorldX(xArg) + (map.getTileWidth() / 2), map.toWorldY(yArg) + (map.getTileHeight() / 2)) / 128.0f;
				}
				else
					return -1.0f;
			}

			radius++;
		}
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

			float normal = (float) ((Math.PI / 2) + Math.atan2(seg.end.y - seg.start.y, seg.end.x - seg.start.x));

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
			if (coll != null)
				Player.collisionAnimationPos = new HvlCoord(coll.x, coll.y);
		}
		throw new Exception("Looped too many times.");
	}

	public static void activateTile(float x, float y, float radius) {
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

	public static void activateSingleTile(float xArg, float yArg) {
		int x = map.toTileX(xArg);
		int y = map.toTileY(yArg);

		if (x < 0 || x >= map.getLayer(0).getMapWidth() || y < 0 || y >= map.getLayer(0).getMapHeight())
			return;

		HvlSimpleTile st0 = (HvlSimpleTile) map.getLayer(0).getTile(x, y);
		HvlSimpleTile st2 = (HvlSimpleTile) map.getLayer(2).getTile(x, y);

		if (st0 != null && st0.getTile() == offTile) {
			map.getLayer(0).setTile(x, y, new HvlSimpleTile(onTile));
		}
		if (st2 != null && st2.getTile() == smallExplosionTile) {
			map.getLayer(2).setTile(x, y, null);
			activateSmallExplosion(x, y);
		}
		if (st2 != null && st2.getTile() == largeExplosionTile) {
			map.getLayer(2).setTile(x, y, null);
			activateLargeExplosion(xArg, yArg);
		}
	}

	public static void activateSmallExplosion(int x, int y) {
		for (int xI = -2; xI < 3; xI++) {
			for (int yI = -2; yI < 3; yI++) {
				activateSingleTile((x + xI) * map.getTileWidth(), (y + yI) * map.getTileHeight());
			}
		}
	}

	public static void activateLargeExplosion(float x, float y) {
		int angleSubdivisions = 3;
		float angleVar = (float) Math.toRadians(30.0f);

		float angle = (float) Math.atan2(y - Player.getY(), x - Player.getX());

		for (float theta = angle - angleVar; theta <= angle + angleVar; theta += angleVar / angleSubdivisions) {
			for (int i = 0; i < 10; i++) {
				activateSingleTile(x + (float) (Math.cos(theta) * map.getTileWidth() * i * 0.5f), y
						+ (float) (Math.sin(theta) * map.getTileHeight() * i * 0.5f));
			}
		}
	}

	public static int getCurrentTurn() {
		return currentTurn;
	}

	public static void setCurrentTurn(int currentTurn) {
		Game.currentTurn = currentTurn;
	}

	public static void onEndTurn() {
		boolean win = true;

		for (int x = 0; x < map.getLayer(0).getMapWidth(); x++) {
			for (int y = 0; y < map.getLayer(0).getMapHeight(); y++) {
				if (!map.isTileInLocation(x, y, 0))
					continue;

				HvlSimpleTile t = (HvlSimpleTile) map.getLayer(0).getTile(x, y);

				if (t.getTile() == offTile) {
					win = false;
					break;
				}
			}
		}

		if (win) {
			onWin();
		} else if (Game.currentTurn >= Game.par) {
			onLose();
		}
	}

	private static void onWin() {
		System.out.println("Win!");
	}

	private static void onLose() {
		System.out.println("... you failed. Stalemate.");
	}

	public static HvlSimpleParticleSystem generateWallParticles(HvlCoord start, HvlCoord end) {
		HvlSimpleParticleSystem tr = new HvlSimpleParticleSystem(start.x, start.y, 16, 16,
				new HvlLinearPositionProvider(0, 0, end.x - start.x, end.y - start.y), HvlTemplateInteg2D.getTexture(Main.wallParticleIndex));
		float angle = (float) Math.atan2(end.y - start.y, end.x - start.x);
		angle -= (Math.PI / 2);
		HvlCoord dir = new HvlCoord((float) Math.cos(angle), (float) Math.sin(angle));
		tr.setMinXVel(dir.x * 4.0f);
		tr.setMinYVel(dir.y * 4.0f);
		tr.setMaxXVel(dir.x * 8.0f);
		tr.setMaxYVel(dir.y * 8.0f);
		tr.setStartColor(new Color(1, 1, 1, 1f));
		tr.setEndColor(Color.transparent);
		tr.setMinScale(0.8f);
		tr.setMaxScale(1.0f);
		tr.setParticlesPerSpawn(1);
		tr.setMinLifetime(2.5f);
		tr.setMaxLifetime(4f);
		tr.setMinTimeToSpawn(1f);
		tr.setMaxTimeToSpawn(2.5f);
		tr.addCorrelator(new HvlParticleCorrelator(){
			{
				setContinuous(true);
			}

			@Override
			public void correlate(HvlParticle in, float delta) {
				HvlSimpleParticle sp = (HvlSimpleParticle) in;

				if (HvlMath.distance(Player.getX(), Player.getY(), sp.getX(), sp.getY()) < 128.0f) {
					float len = new HvlCoord(sp.getxVel(), sp.getyVel()).length();

					len *= (float) Math.pow(Math.E, 2.5f * delta);

					HvlCoord newDir = new HvlCoord(Player.getX() - sp.getX(), Player.getY() - sp.getY()).normalize().fixNaN().mult(len);
					sp.setxVel(newDir.x);
					sp.setyVel(newDir.y);
				}
			}
		});
		return tr;
	}

//	public static HvlSimpleParticleSystem generateTileParticles(int tileX, int tileY){
//		HvlSimpleParticleSystem tr = new HvlSimpleParticleSystem(tileX * map.getTileWidth(), tileY * map.getTileHeight(), 64, 64,
//				new HvlRectanglePositionProvider(0, map.getTileWidth(), 0, map.getTileHeight()), HvlTemplateInteg2D.getTexture(Main.wallParticleIndex));
//		tr.setStartColor(new Color(1, 1, 1, 1f));
//		tr.setEndColor(Color.transparent);
//		tr.setMinScale(0.8f);
//		tr.setMaxScale(1.0f);
//		tr.setParticlesPerSpawn(25);
//		tr.setMinLifetime(5f);
//		tr.setMaxLifetime(7f);
//		tr.setMinTimeToSpawn(5f);
//		tr.setMaxTimeToSpawn(5f);
//		return tr;
//	}
}
