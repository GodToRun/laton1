package laton.main;

import java.awt.geom.Line2D;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import laton.engine.AABB;
import laton.engine.ALManager;
import laton.engine.Animation;
import laton.engine.AudioBuffer;
import laton.engine.AudioListener;
import laton.engine.AudioSource;
import laton.engine.Camera;
import laton.engine.Canvas;
import laton.engine.Cube;
import laton.engine.DisplayManager;
import laton.engine.GLManager;
import laton.engine.GameObject;
import laton.engine.IEvent;
import laton.engine.Mesh;
import laton.engine.OBJLoader;
import laton.engine.Panel;
import laton.engine.Quad;
import laton.engine.Renderer;
import laton.engine.Sector;
import laton.engine.Text;
import laton.engine.Textures;
import laton.engine.WorldLight;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import vecutils.Vector3;
import vecutils.Vector4;

public class Game {
	public GLManager glman;
	public ALManager alman;
	public Renderer renderer;
	public Camera cam;
	public AABB camAABB;
	public ArrayList<AABB> mapAABB = new ArrayList<AABB>();
	public float speed = 0.2f;
	public int level = 1;
	GameObject lit;
	public String[][] codes = {
		new String[] { "set event1_o mvc2_yard mvt 0 -0.11 0 90",
				"set eventd_o1 mvcd_door1 mvt 0 0 0.16 80",
				"set eventf_o2 mvcf_floor1 mvt 0 0.15 0 82",
				"set event0_o3 mvc_MV1 mvt 0 0.12 0 80",
				"set eventex null ext"},
		new String[] { "set event1 mvc1 mvt 0 0.14 0 30",
				"set event2 mvc2 mvt 0 0.14 0 60",
				"set event3 mvc3 mvt 0 -0.14 0 80",
				"set event4 mvc4 mvt 0 0.14 0 190", 
				"set event5 mvc5 mvt 0 0 -0.14 80",
				"set event6 mvc6 mvt 0 0 -0.18 120",
				"set event7 mvc7 mvt 0 0 -0.18 120",
				"set event8 mvc8 mvt -0.18 0 0 120",
				"set event9 mvc9 mvt -0.18 0 0 120",
				"set ext null ext" },
		new String[] { "set event1 mv1 mvt 0 0.12 0 50",
				"set ev2 mv2 mvt 0 0.18 0 180",
				"set ev3 mv3 mvt 0 0.135 0 110",
				"set ext null ext"
				},
		new String[] { "set event1 mvc1 mvt 0 -0.14 0 100"
				, "set event2 mvc2 mvt 0 0.23 0 150"
				, "set event3 mvc3 mvt 0 0.22 0 110"
				, "set event4 mvc2 mvt 0.22 0 0 110"
				, "set event5 mvc4 mvt -0.14 0 0 100"
				, "skset event6 mvc5 mvt 0 -0.2 0 110"
				, "set ext null ext"
		},
		new String[] { "set event mvc mvt 0 0 0.18 120",
				"skset event2 mvc2 mvt 0.2 0 0 120"}
	};
	AABB waterAABB;
	GameObject obj, water, shotgun;
	GunAnimation gunAni;
	public OBJLoader shotgunMesh, rocketLauncherMesh, grenadeLauncherMesh, grenadeMesh, nailgunMesh, superNailgunMesh, nailMesh, rocketMesh, doubleShotgunMesh, thunderboltMesh;
	public Gun gun = Gun.Shotgun;
	boolean died = false;
	boolean camIsInWater = false;
	int playerHP = 100, playerAMMO = 50;
	ArrayList<IEvent> events = new ArrayList<IEvent>();
	public ArrayList<Monster> monsters = new ArrayList<Monster>();
	public ArrayList<Pickable> pickables = new ArrayList<Pickable>();
	public ArrayList<Teleporter> teleporters = new ArrayList<Teleporter>();
	public AudioSource playerSource;
	AudioListener playerListener;
	public AudioBuffer shotgunClip, ammoClip, expClip;
	Canvas canvas;
	Text HPText, ammoText, titleText, panelText;
	ParticleSystem hit;
	Panel gamePanel;
	public boolean silverkey = false;
	public float damageMultipler = 1f;
	public int damMultipleTick = -1;
	public Color panelCol;
	ShubNiggurath boss;
	boolean end;
	int endTick = -100;
	void updateGun() {
		shotgun.setPosition(new Vector3(0, -0.4f, -1.6f));
		if (gun == Gun.Shotgun || gun == Gun.DoubleShotgun) {
			if (gun == Gun.Shotgun) {
				shotgun.mesh = shotgunMesh;
				gunAni = new ShotgunAnimation(shotgun, 0.7f);
			}
			else if (gun == Gun.DoubleShotgun) {
				shotgun.mesh = doubleShotgunMesh;
				gunAni = new ShotgunAnimation(shotgun, 0.95f);
			}
			
			shotgun.texture = Textures.loadTexture("ltex/shotgun.png", 9728);
		}
		else if (gun == Gun.RocketLauncher) {
			shotgun.mesh = rocketLauncherMesh;
			gunAni = new ShotgunAnimation(shotgun, 1.22f);
			shotgun.texture = Textures.loadTexture("ltex/rocketl.png", 9728);
		}
		else if (gun == Gun.GrenadeLuncher) {
			shotgun.mesh = grenadeLauncherMesh;
			gunAni = new ShotgunAnimation(shotgun, 1.25f);
			shotgun.texture = Textures.loadTexture("ltex/grenadel.png", 9728);
		}
		else if (gun == Gun.Nailgun) {
			shotgun.mesh = nailgunMesh;
			gunAni = new NailgunAnimation(shotgun, (OBJLoader)shotgun.mesh);
			shotgun.texture = Textures.loadTexture("ltex/nailgun.png", 9728);
		}
		else if (gun == Gun.SuperNailgun) {
			shotgun.setPosition(new Vector3(0, -0.6f, -1.6f));
			shotgun.mesh = superNailgunMesh;
			gunAni = new SuperNailgunAnimation(shotgun);
			shotgun.texture = Textures.loadTexture("ltex/super_nailgun.png", 9728);
		}
		else if (gun == Gun.Thunderbolt) {
			shotgun.mesh = thunderboltMesh;
			gunAni = null;
			shotgun.texture = Textures.loadTexture("ltex/thunderbolt.png", 9728);
		}
		shotgun.create();
	}
	
	/* Ending */
	
	public void doEnding() {
		end = true;
	}
	public void doEndingUpdate() {
		String letters = ("You have defeated the hideous pit lord, shub-niggurath. Hell\r\n"
				+ "has finally bowed to you. By the way, you hear\r\n"
				+ "that demons are still out there somewhere. Upon hearing the news,\r\n"
				+ "you immediately board the slipgate and head somewhere.\r\n"
				+ "Continued from Laton 2: unfinished fight!");
		for (char letter : letters.toCharArray()) {
			if (endTick > 10) {
				titleText.text += letter;
				endTick = 0;
			}
			endTick++;
		}
	}
	
	void clear() {
		gun = Gun.Shotgun;
		updateGun();
		if (level == 4) {
			String letters = (" You defeated the monster that invaded the earth and finally killed two vicious shamblers.\r\n"
					+ "Now, just as you're about to go back to your hometown, you realize that this dimensional gate is connected to hell.\r\n"
					+ "Now the sight before you is a den of red skies and creepy demons. If you go back like this, you will not be able to turn the earth around, so you enter the den of demons with wild steps.\r\n"
					+ "Will you save the Earth like this? Or will it be crushed by demons?\r\n"
					+ "Next is episode 2. Den of the Demons!\r\n");
			for (char letter : letters.toCharArray()) {
				titleText.text += letter;
				try {
					Thread.sleep(38);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			titleText.text = "";
		}
	}
	OBJLoader loadLevel(String modelName, String[] latonShell) {
		if (level == 4) {
			clear();
			water.texture = Textures.loadTexture("ltex/lava.png", 9728);
		}
		if (obj != null)
			obj.setActive(false);
		obj = new GameObject(new OBJLoader(modelName));
		obj.texture = Textures.loadTexture("lmodels/e1m1/wall.jpg", 9728);
		obj.create();
		for (Monster m : monsters) {
			m.setActive(false);
		}
		for (Pickable m : pickables) {
			m.setActive(false);
		}
		events.clear();
		monsters.clear();
		pickables.clear();
		teleporters.clear();
		mapAABB.clear();
		OBJLoader loader = (OBJLoader)obj.mesh;
		for (Sector sec : loader.model.sectors) {
			if (sec.name.startsWith("grunt")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 5.8f;
				gruntAt(pos);
			}
			else if (sec.name.startsWith("enforcer")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 4.8f;
				enforcerAt(pos);
			}
			else if (sec.name.startsWith("knight")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 5f;
				knightAt(pos);
			}
			else if (sec.name.startsWith("ogre")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 5f;
				ogreAt(pos);
			}
			else if (sec.name.startsWith("zombie")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 5f;
				zombieAt(pos);
			}
			else if (sec.name.startsWith("deathknight")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 5f;
				deathKnightAt(pos);
			}
			else if (sec.name.startsWith("container")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 0.7f;
				containerAt(pos);
			}
			else if (sec.name.startsWith("dog")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1f;
				dogAt(pos);
			}
			else if (sec.name.startsWith("healthb")) {
				Vector3 pos = sec.vecs.get(0).copy();
				if (level != 1)
					pos.y += 1.2f;
				else
					pos.y += 0.35f;
				healthBoxAt(pos);
			}
			else if (sec.name.startsWith("silverkey")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 2.4f;
				silverKeyAt(pos);
			}
			else if (sec.name.startsWith("quaddamage")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 2.4f;
				quadDamageAt(pos);
			}
			else if (sec.name.startsWith("ammob")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.2f;
				ammoBoxAt(pos);
			}
			else if (sec.name.startsWith("shub")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.2f;
				shubAt(pos);
			}
			else if (sec.name.startsWith("teleporter")) {
				Vector3 pos = sec.vecs.get(0).copy();
				teleporterAt(pos, boss.spike.getPosition());
			}
			else if (sec.name.startsWith("shambler")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 7f;
				shamblerAt(pos);
			}
			else if (sec.name.startsWith("nailgun")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.3f;
				gunAt(new OBJLoader("lmodels/nailgun.obj"), Gun.Nailgun, pos, "ltex/nailgun.png");
			}
			else if (sec.name.startsWith("super_nailgun")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.3f;
				gunAt(new OBJLoader("lmodels/super_nailgun.obj"), Gun.SuperNailgun, pos, "ltex/super_nailgun.png");
			}
			else if (sec.name.startsWith("rocketl")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.3f;
				gunAt(new OBJLoader("lmodels/rocketl.obj"), Gun.RocketLauncher, pos, "ltex/rocketl.png");
			}
			else if (sec.name.startsWith("doubleshotgun")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.3f;
				gunAt(new OBJLoader("lmodels/double_shotgun.obj"), Gun.DoubleShotgun, pos, "ltex/shotgun.png");
			}
			else if (sec.name.startsWith("thunderbolt")) {
				Vector3 pos = sec.vecs.get(0).copy();
				pos.y += 1.3f;
				gunAt(new OBJLoader("lmodels/thunderbolt.obj"), Gun.Thunderbolt, pos, "ltex/thunderbolt.png");
			}
			else {
				sec.refAABB.min = sec.min;
				sec.refAABB.max = sec.max;
				mapAABB.add(sec.refAABB);
			}
		}
		cam.setPosition(new Vector3(6f, 6, 6f));
		LSInterpreter interpreter = new LSInterpreter(this, obj, loader, latonShell);
		ArrayList<IEvent> expanded = interpreter.expandEvents();
		for (IEvent event : expanded) {
			events.add(event);
		}
		return loader;
	}
	public void create() {
		//createLAD();
		createDisplay();
		createGL();
		createCamera();
		createRenderer();
		createInput();
		createAL();
		
		try {
			shotgunClip = new AudioBuffer(new BufferedInputStream(new FileInputStream("lsounds/shotgun.wav")));
			ammoClip = new AudioBuffer(new BufferedInputStream(new FileInputStream("lsounds/ammo.wav")));
			expClip = new AudioBuffer(new BufferedInputStream(new FileInputStream("lsounds/explosion.wav")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		canvas = new Canvas();
		panelCol = Color.black;
		HPText = new Text(canvas, 8, 20, "HP", 0.6f);
		ammoText = new Text(canvas, -36, 20, "AM", 0.6f);
		titleText = new Text(canvas, -258, -150, "", 0.2f);
		HPText.renderd = true;
		gamePanel = new Panel(canvas, 0, 0, panelCol, 1);
		gamePanel.renderd = false;
		panelCol.a = 0f;
		//panelText.render = false;
		//panelText.col = panelCol;
		
		playerSource = new AudioSource(false, true);
		playerListener = new AudioListener();
		
		hit = new ParticleSystem(new Vector4(0.6f, 0, 0, 1f));
		hit.create();
		
		shotgunMesh = new OBJLoader("lmodels/shotgun.obj");
		doubleShotgunMesh = new OBJLoader("lmodels/double_shotgun.obj");
		rocketLauncherMesh = new OBJLoader("lmodels/rocketl.obj");
		grenadeLauncherMesh = new OBJLoader("lmodels/grenadel.obj");
		nailgunMesh = new OBJLoader("lmodels/nailgun.obj");
		superNailgunMesh = new OBJLoader("lmodels/super_nailgun.obj");
		thunderboltMesh = new OBJLoader("lmodels/thunderbolt.obj");
		grenadeMesh = new OBJLoader("lmodels/grenade.obj");
		shotgunMesh.renderInSpace = false;
		doubleShotgunMesh.renderInSpace = false;
		rocketLauncherMesh.renderInSpace = false;
		grenadeLauncherMesh.renderInSpace = false;
		nailgunMesh.renderInSpace = false;
		superNailgunMesh.renderInSpace = false;
		thunderboltMesh.renderInSpace = false;
		shotgun = new GameObject(shotgunMesh);
		shotgun.setPosition(new Vector3(0, -0.4f, -1.6f));
		nailMesh = new OBJLoader("lmodels/nail.obj");
		rocketMesh = new OBJLoader("lmodels/rocket.obj");
		lit = new GameObject(new OBJLoader("lmodels/lightning.obj"));
		lit.setRotation(new Vector4(0, 90, 0, 0));
		lit.setPosition(new Vector3(0, 0, -28));
		lit.mesh.renderInSpace = false;
		lit.create();
		lit.setActive(false);
		lit.texture = Textures.loadTexture("ltex/lightning.png", 9728);
		updateGun();
		
		water = new GameObject(new Quad());
		water.texture = Textures.loadTexture("ltex/water.png", 9728);
		water.setRotation(new Vector4(90, 0, 0, 0));
		water.setScale(new Vector3(600, 600, 1));
		water.setPosition(new Vector3(6, 0, 6));
		water.create();
		
		loadLevel("lmodels/m" + level + ".obj", codes[level-1]);
		
		waterAABB = new AABB(new Vector3(-600, -600, -600), new Vector3(600, 0, 600));
		
		WorldLight.init();
		
		/*IEvent mv1 = new MovementEvent(obj, loader.findSector("mvc_MV1"), new Vector3(0, 0.12f, 0), 80);
		mv1.origin = new Vector3(15, 1f, 60);
		events.add(mv1);*/
		
		while(!DisplayManager.isDisplayShouldClose()) {
			for (int i = 0; i < GameObject.objects.size(); i++) {
				GameObject.objects.get(i).update();
			}
			if (!died) {
				while (Keyboard.next()) {
					if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
						for (IEvent event : events) {
							Vector3 dis = event.origin.distance(cam.getPosition());
							if (dis.x < 6f && dis.y < 6f && dis.z < 6f) {
								event.onActive();
								break;
							}
						}
					}
				}
				for (int i = 0; i < events.size(); i++) {
					if (i >= events.size()) return;
					events.get(i).update();
				}
				for (Pickable pickable : pickables) {
					Vector3 ami = pickable.getPosition().copy();
					Vector3 d = cam.getPosition().distance(ami);
					float l = 3.5f;
					if (d.x < l && d.y < l && d.z < l && pickable.active()) {
						pickable.onPick();
					}
				}
				for (Teleporter t : teleporters) {
					t.update();
				}
				updateController();
			}
			update();
			renderer.render(cam);
			canvas.render(cam);
			DisplayManager.updateDisplay();
			GameObject.processDeletePool();
		}
		alman.destroy();
	}
	void shubAt(Vector3 position) {
		ShubNiggurath mon1 = new ShubNiggurath(this, cam, new OBJLoader("lmodels/shubniggurath.obj"));
		boss = mon1;
		mon1.setScale(new Vector3(0.68f, 0.68f, 0.68f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void gruntAt(Vector3 position) {
		Grunt mon1 = new Grunt(this, cam, new OBJLoader("lmodels/woman.obj"));
		mon1.setScale(new Vector3(0.68f, 0.68f, 0.68f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void enforcerAt(Vector3 position) {
		Enforcer mon1 = new Enforcer(this, cam, new OBJLoader("lmodels/grunt.obj"));
		mon1.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void silverKeyAt(Vector3 pos) {
		SilverKey box = new SilverKey(this, new OBJLoader("lmodels/key.obj"), 1f, "ltex/silverkey.png");
		box.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		box.create();
		box.setPosition(pos);
		pickables.add(box);
	}
	void quadDamageAt(Vector3 pos) {
		QuadDamage box = new QuadDamage(this, new OBJLoader("lmodels/quaddamage.obj"), 1f, "ltex/silverkey.png");
		box.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		box.create();
		box.setPosition(pos);
		pickables.add(box);
	}
	void deathKnightAt(Vector3 position) {
		DeathKnight mon1 = new DeathKnight(this, cam, new OBJLoader("lmodels/deathknight.obj"));
		mon1.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void shamblerAt(Vector3 position) {
		Shambler mon1 = new Shambler(this, cam, new OBJLoader("lmodels/shambler.obj"));
		mon1.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void knightAt(Vector3 position) {
		Knight mon1 = new Knight(this, cam, new OBJLoader("lmodels/knight.obj"));
		mon1.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void ogreAt(Vector3 position) {
		Ogre mon1 = new Ogre(this, cam, new OBJLoader("lmodels/ogre.obj"));
		mon1.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void zombieAt(Vector3 position) {
		Zombie mon1 = new Zombie(this, cam, new OBJLoader("lmodels/zombie.obj"));
		mon1.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void dogAt(Vector3 position) {
		Dog mon1 = new Dog(this, cam, new OBJLoader("lmodels/dog.obj"));
		mon1.setScale(new Vector3(0.5f, 0.5f, 0.5f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	void healthBoxAt(Vector3 position) {
		HealthBox box = new HealthBox(this, new Cube());
		box.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		box.create();
		box.setPosition(position);
		pickables.add(box);
	}
	void teleporterAt(Vector3 pos, Vector3 telpos) {
		Teleporter t = new Teleporter(this, pos, telpos);
		teleporters.add(t);
	}
	void ammoBoxAt(Vector3 position) {
		AmmoBox box = new AmmoBox(this, new Cube(), 0f, "ltex/ammo.png");
		box.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		box.create();
		box.setPosition(position);
		pickables.add(box);
	}
	void gunAt(Mesh gunMesh, Gun gun, Vector3 position, String tex) {
		GunPickable box = new GunPickable(this, gun, gunMesh, tex);
		//box.setScale(new Vector3(0.7f, 0.7f, 0.7f));
		box.setPosition(position);
		box.create();
		pickables.add(box);
	}
	void containerAt(Vector3 position) {
		Container mon1 = new Container(this, cam, new Cube());
		mon1.setScale(new Vector3(0.7f, 1.4f, 0.7f));
		mon1.create();
		mon1.setPosition(position);
		monsters.add(mon1);
	}
	public void createInput() {
		Mouse.setGrabbed(true);
	}
	float si;
	void headBobbing() {
		float val = (float)Math.sin(si) / 4;
		renderer.offset = new Vector3(0, -2 + val, 0);
		if (-val > -0.18f) {
			shotgun.offset = new Vector3(0, 0, val / 1.2f);
		}
		si += 0.17f;
	}
	void gunShot() {
		if (playerAMMO > 0) {
			if (gun == Gun.Thunderbolt) {
				lit.getRotation().z = 30 * Math.abs((float)Math.sin(System.currentTimeMillis() / 10));
				if (lit.getRotation().z > 1) lit.getRotation().z = 1;
				lit.setActive(true);
				if (Math.random() > 0.05) {
					playerAMMO++;
				}
			}
			if (gunAni != null) {
				if (!gunAni.ended) {
					return;
				}
				playerSource.setBuffer(shotgunClip.getBufferId());
				playerSource.play();
				gunAni.init();
				gunAni.ended = false;
			}
			playerAMMO--;
			if (gun == Gun.Shotgun || gun == Gun.DoubleShotgun || gun == Gun.Thunderbolt) {
				Random random = new Random();
				for (int i = 0; i < 40; i++) {
					Vector3 fwd = cam.getPosition().add(cam.forward.mul(i * -2));
					for (Monster mon : monsters) {
						if (!mon.died) {
							Vector3 dis = fwd.distance(mon.getPosition());
							if (dis.x < 3f && dis.y < 6f && dis.z < 3f) {
								for (int j = 0; j < 13; j++) {
									float spr = 1.65f;
									hit.emit(mon.getPosition().add(new Vector3(random.nextFloat() * spr, -3 + random.nextFloat() * (spr), random.nextFloat() * (spr))));
								}
								if (gun == Gun.Shotgun)
									mon.damage((int)(20 * damageMultipler));
								else if (gun == Gun.DoubleShotgun) {
									playerAMMO--;
									mon.damage((int)(56 * damageMultipler));
								}
								else if (gun == Gun.Thunderbolt) {
									mon.damage((int)(3 * damageMultipler));
								}
								return;
							}
						}
					}
					
				}
			}
			else if (gun == Gun.Nailgun || gun == Gun.SuperNailgun) {
				Rocket nail = new Rocket(this, cam, nailMesh, 2f, (int)(12 * damageMultipler), false);
				nail.setScale(new Vector3(0.2f, 0.2f, 0.2f));
				nail.setPosition(cam.getPosition().add(cam.forward).add(new Vector3(0, 0.15f, 0)));
				nail.create();
				if (Math.random() > 0.3) {
					playerAMMO++;
				}
			}
			else if (gun == Gun.RocketLauncher) {
				Rocket rocket = new Rocket(this, cam, rocketMesh, 1.8f, (int)(90 * damageMultipler), true);
				rocket.texture = Textures.loadTexture("ltex/rocket.png", 9728);
				rocket.setScale(new Vector3(0.4f, 0.4f, 0.4f));
				rocket.setPosition(cam.getPosition().add(cam.forward.mul(-4)).add(new Vector3(0, 0.15f, 0)));
				rocket.create();
			}
			else if (gun == Gun.GrenadeLuncher) {
				Grenade grenade = new Grenade(cam.forward, this, cam, grenadeMesh, 1.8f, (int)(90 * damageMultipler), true);
				grenade.texture = Textures.loadTexture("ltex/rocket.png", 9728);
				grenade.setScale(new Vector3(0.4f, 0.4f, 0.4f));
				//System.out.println(cam.forward.x + ", " + cam.forward.y + ", " + cam.forward.z);
				grenade.setPosition(cam.getPosition().add(cam.forward.mul(-2f)).add(new Vector3(0, 1f, 0)));
				grenade.create();
			}
		}
	}
	public void updateController() {
		camAABB.min = cam.getPosition().sub(new Vector3(0, 1.3f, 0));
		camAABB.max = camAABB.min.add(new Vector3(1, 2, 1));
		cam.yaw(Mouse.getDX() / 1.8f);
		cam.pitch(-Mouse.getDY() / 1.8f);
		//System.out.println("(" + cam.getPosition().x + ", " + cam.getPosition().y + ", " + cam.getPosition().z + ")");
		cam.aabbUpdate(camAABB, mapAABB);
		lit.setActive(false);
		float drag = 10f;
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			Vector3 back = cam.forward.mul(speed);
			cam.velocity.x += back.x / drag;
			cam.velocity.z += back.z / drag;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			Vector3 forward = cam.forward.mul(-speed);  
			cam.velocity.x += forward.x / drag;
			cam.velocity.z += forward.z / drag;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			Vector3 right = cam.right.mul(-speed);
			cam.velocity.x += right.x / drag;
			cam.velocity.z += right.z / drag;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			Vector3 left = cam.right.mul(speed);
			cam.velocity.x += left.x / drag;
			cam.velocity.z += left.z / drag;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && cam.velocity.y == 0 && !camIsInWater) {
			cam.velocity.y = 0.5f;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && camIsInWater) {
			cam.velocity.y += 0.018f;
		}
		if ((Keyboard.isKeyDown(Keyboard.KEY_W) ||
			Keyboard.isKeyDown(Keyboard.KEY_A) ||
			Keyboard.isKeyDown(Keyboard.KEY_S) ||
			Keyboard.isKeyDown(Keyboard.KEY_D)) && !camIsInWater) {
			headBobbing();
		}
		if (Mouse.isButtonDown(0)) {
			gunShot();
		}
		if (camAABB.intersects(waterAABB)) {
			camIsInWater = true;
			cam.gravity = -0.004f;
		}
		else {
			camIsInWater = false;
			cam.gravity = -0.025f;
		}
		
		while (Mouse.next()) {
			if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
				//gunShot();
			}
		}
		
		if (gunAni != null)
			gunAni.update();
		
		/*for (int i = 0; i < obj.mesh.vertices.length-1; i++) {
			Vector3 vtx = obj.mesh.vertices[i];
			Vector3 fvtx = obj.mesh.vertices[i+1];
			float ldis = (float)Line2D.ptLineDist((float)vtx.x, (float)vtx.z, (float)fvtx.x, (float)fvtx.z,
					(float)-cam.getPosition().x, (float)-cam.getPosition().z);
			if (!Float.isNaN(ldis)) {
				if (ldis < 0.001f) {
					canGo = false;
				}
			}
			
		}*/
	}
	public void update() {
		if (end) doEndingUpdate();
		if (teleporters.size() > 0) teleporters.get(0).telpos = boss.spike.getPosition();
		if (damMultipleTick >= 0) damMultipleTick++;
		if (damMultipleTick > 1200) {
			damageMultipler = 1f;
			panelCol.a = 0f;
			gamePanel.renderd = false;
			damMultipleTick = -1;
		}
		HPText.text = playerHP + "";
		ammoText.text = playerAMMO + "";
		playerSource.setPosition(cam.getPosition());
		playerListener.setPosition(cam.getPosition());
		if (playerHP <= 0 && !died) {
			died = true;
		}
		if (died) {camAABB.min = cam.getPosition().add(new Vector3(0, 1, 0)); cam.aabbUpdate(camAABB, mapAABB);}
	}
	public void createCamera() {
		cam = new Camera();
		cam.gravity = -0.025f;
		cam.setPosition(new Vector3(6f, 6, 6f));
		camAABB = new AABB(cam.getPosition().sub(new Vector3(0, 1.3f, 0)), cam.getPosition().add(new Vector3(1, 2, 1)));
	}
	public void createRenderer() {
		renderer = new Renderer();
		renderer.init(cam);
	}
	public void createGL() {
		glman = new GLManager();
		glman.create();
	}
	public void createAL() {
		alman = new ALManager();
		alman.create();
	}
	public void createDisplay() {
		DisplayManager.createDisplay("LATON");
	}
}
