package de.test.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.test.game.Testmap.ScreenType;

public class Battlescreen implements Screen {
	
	enum Battlestate{firstStrike, LeonTurn, EnemyTurn, BattleOver, GameOver};
	static Battlestate battlestate;
	static Monster gegner1, gegner2, gegner3;
	static List<Fighter> fighterList = new ArrayList<>();
	
	SpriteBatch batch;
	TextureAtlas enemies;
	TextureRegion enemytr1, enemytr2, enemytr3;
	Sprite enemy1;
	Sprite enemy2 ;
	Sprite enemy3;
	static Player player = Gamescreen.player;
	Vector2 playerPosition;
	RectangleMapObject rect1;
	RectangleMapObject rect2;
	RectangleMapObject rect3;
	BitmapFont font;
	Skin skin;
	Skin skin2;
	TextureAtlas butwin;
	TextureAtlas baratlas;
	TextButtonStyle buttonstyle = new TextButtonStyle();
	WindowStyle ws = new WindowStyle();
	ImageButtonStyle angriff = new ImageButtonStyle();
	ImageButtonStyle select = new ImageButtonStyle();
	LabelStyle labstyle = new LabelStyle();
	ImageButton ang;
	ImageButton sel;
	static boolean anim = false;
	float stateTime;
	ProgressBar barPlayer;
	ProgressBar barEnemy1, barEnemy2, barEnemy3;
	ProgressBarStyle barStyle;
	Drawable textureBar;
	int i = 0;
	
	static Battlemenu battlemenu;
	static Skills skills;
	static BattleItem battleitem;
	
	OrthographicCamera cam;
	Viewport viewport;
	
	Stage stage;
	static Testmap battle;
	
	Dialog dialog;
	Label win, playerHP, enemy1HP, enemy2HP, enemy3HP;
	
	@SuppressWarnings("static-access")
	public Battlescreen(Testmap battle){
		
		this.battle = battle;

	}
	
	@Override
	public void show() {
		Testmap.setHorst("bs");
		player.currentFrame = player.f1.getKeyFrame(0);
		skin = new Skin();
		font = new BitmapFont(Gdx.files.internal("default.fnt"));
		butwin = new TextureAtlas("butwin.atlas");
		skin.addRegions(butwin);
		ws.background = skin.getDrawable("default-window");
		ws.titleFont = font;
		buttonstyle.down = skin.getDrawable("testbutton2");
		buttonstyle.up = skin.getDrawable("testbutton");
		buttonstyle.font = font;
		labstyle.font = font;
		skin.add("default", labstyle);
		skin.add("default", ws);
		skin.add("default", buttonstyle);
		skin.add("dialog", ws);
		skin.add("Button", buttonstyle);
		angriff.down = skin.getDrawable("Action");
		angriff.up = skin.getDrawable("Action");
		select.up = skin.getDrawable("Sbutton");
		select.down = skin.getDrawable("Sbutton");
		skin.add("ang", angriff);
		skin.add("sel", select);
		ang = new ImageButton(skin, "ang");
		sel = new ImageButton(skin, "sel");
		battlemenu = new Battlemenu(skin);
		skills = new Skills(skin);
		battleitem = new BattleItem(skin);
		
		cam = new OrthographicCamera();
		viewport = new ExtendViewport(800, 480, cam);
		cam.setToOrtho(false);
		stage = new Stage(viewport);
		
		battlemenu.setVisible(true);
		battlemenu.setKeepWithinStage(false);
		battlemenu.setMovable(false);
		
		skills.setVisible(false);
		skills.setKeepWithinStage(false);
		skills.setMovable(false);
		
		battleitem.setVisible(false);
		battleitem.setKeepWithinStage(false);
		battleitem.setMovable(false);
		
		win = new Label("You WON\n" + "5000k GOLD\n" + "2 EXP", labstyle);
		
		dialog = new Dialog("", skin, "dialog") {
			public void result(Object obj) {
				if(obj.equals(true)){
					battle.setScreen(battle.getScreenType(ScreenType.Gamescreen));
				}
		    }
		};
		dialog.text(win);
		dialog.button("Heap Heap Hurra", true); //sends "true" as the result
		dialog.setVisible(false);
		dialog.setPosition(300, 200);
		dialog.sizeBy(100, 100);

		ang.setVisible(true);
		sel.setVisible(true);
		
		batch = new SpriteBatch();
		
		skin2 = new Skin();
		baratlas = new TextureAtlas("bar.atlas");
		skin2.addRegions(baratlas);
		
		textureBar = new TextureRegionDrawable(new TextureRegion(skin2.getRegion("barknob")));
		barStyle = new ProgressBarStyle(skin2.newDrawable("barbackground"), textureBar);
		barStyle.knobBefore = barStyle.knob;
		
		barPlayer = new ProgressBar(0, player.maxHP, 1, false, barStyle);
	    barPlayer.setSize(100, 11);
	    barPlayer.setValue(player.curHP);
	    barPlayer.setPosition(player.getPosition().x, player.getPosition().y - 225);
		
		ang.setPosition(140, 40);
		sel.setPosition(15, 40);
		battlemenu.setPosition(0, 480 - battlemenu.getHeight());
		skills.setPosition(0, 480 - skills.getHeight());
		battleitem.setSize(skills.getWidth(), skills.getHeight());
		battleitem.setPosition(skills.getX(), skills.getY());
		
		int anzgeg = (int) (Math.random()*3);
		
		for( i = 0; i <= anzgeg; i++){
			switch(i){
				case 0:
					gegner1 = enemySelect();
					enemytr1 = gegner1.texture;
					enemy1 = new Sprite(enemytr1);
					enemy1HP = new Label(gegner1.getCurrentHP() + "/" + gegner1.getMaxHP(), labstyle);
					stage.addActor(enemy1HP);
					fighterList.add(player);
					fighterList.add(gegner1);
				break;
				
				case 1:
					gegner2 = enemySelect();
					enemytr2 = gegner2.texture;
					enemy2 = new Sprite(enemytr2);
					enemy2HP = new Label(gegner2.getCurrentHP() + "/" + gegner2.getMaxHP(), labstyle);
					stage.addActor(enemy2HP);
					fighterList.add(gegner2);
				break;
				
				case 2:
					gegner3 = enemySelect();
					enemytr3 = gegner3.texture;
					enemy3 = new Sprite(enemytr3);
					enemy3HP = new Label(gegner3.getCurrentHP() + "/" + gegner3.getMaxHP(), labstyle);
					stage.addActor(enemy3HP);
					fighterList.add(gegner3);
				break;
			}
		}
		
		playerHP = new Label(player.getCurrentHP() + "/" + player.getMaxHP(), labstyle);
		playerHP.setPosition(barPlayer.getX(), 30);
		System.out.println("Int: "+ i);
		if(i == 1){
			barEnemy1 = new ProgressBar(0, Battlescreen.gegner1.maxHP, 1, false, barStyle);
			barEnemy1.setSize(100, 11);
			barEnemy1.setValue(Battlescreen.gegner1.curHP);
			barEnemy1.setPosition(player.getPosition().x - 400, barPlayer.getY());
			
			enemy1HP.setPosition(barEnemy1.getX(), 30);
		    stage.addActor(barEnemy1);
		}
			
		if(i == 2){
			barEnemy1 = new ProgressBar(0, Battlescreen.gegner1.maxHP, 1, false, barStyle);
			barEnemy1.setSize(100, 11);
			barEnemy1.setValue(Battlescreen.gegner1.curHP);
			barEnemy1.setPosition(player.getPosition().x - 400, player.getPosition().y - 200);
			
			barEnemy2 = new ProgressBar(0, Battlescreen.gegner2.maxHP, 1, false, barStyle);
			barEnemy2.setSize(100, 11);
			barEnemy2.setValue(Battlescreen.gegner2.curHP);
			barEnemy2.setPosition(player.getPosition().x - 400, player.getPosition().y - 250);
			
			enemy1HP.setPosition(barEnemy1.getX(), 60);
			enemy2HP.setPosition(barEnemy2.getX(), 10);
			stage.addActor(barEnemy1);
		    stage.addActor(barEnemy2);
		}
			
		if(i == 3){
			barEnemy1 = new ProgressBar(0, Battlescreen.gegner1.maxHP, 1, false, barStyle);
			barEnemy1.setSize(100, 11);
			barEnemy1.setValue(Battlescreen.gegner1.curHP);
			barEnemy1.setPosition(player.getPosition().x - 400, player.getPosition().y - 200);
			
			barEnemy2 = new ProgressBar(0, Battlescreen.gegner2.maxHP, 1, false, barStyle);
			barEnemy2.setSize(100, 11);
			barEnemy2.setValue(Battlescreen.gegner2.curHP);
			barEnemy2.setPosition(player.getPosition().x - 250, barPlayer.getY());
			
			barEnemy3 = new ProgressBar(0, Battlescreen.gegner3.maxHP, 1, false, barStyle);
			barEnemy3.setSize(100, 11);
			barEnemy3.setValue(Battlescreen.gegner3.curHP);
			barEnemy3.setPosition(player.getPosition().x - 400, player.getPosition().y - 250);
			
			enemy1HP.setPosition(barEnemy1.getX(), 60);
			enemy2HP.setPosition(barEnemy2.getX(), 30);
			enemy3HP.setPosition(barEnemy3.getX(), 10);

			stage.addActor(barEnemy1);
		    stage.addActor(barEnemy2);
		    stage.addActor(barEnemy3);
			
		}
		
		stateTime = 0f;
		
		stage.addActor(ang);
		stage.addActor(sel);
		stage.addActor(battlemenu);
		stage.addActor(skills);
		stage.addActor(dialog);
		stage.addActor(battleitem);
	    stage.addActor(barPlayer);
	    stage.addActor(playerHP);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(cam.combined);
	    cam.update();
		batch.begin();
		batch.draw(player.getCurrentFrame(), player.getPosition().x, player.getPosition().y);
		if(i == 1){
			batch.draw(enemy1, player.getPosition().x - 400, player.getPosition().y);
		}
			
		if(i == 2){
			batch.draw(enemy1, player.getPosition().x - 400, player.getPosition().y + 30);
			batch.draw(enemy2, player.getPosition().x - 400, player.getPosition().y - 150);
		}
			
		if(i == 3){
			batch.draw(enemy1, player.getPosition().x - 400, player.getPosition().y + 70);
			batch.draw(enemy2, player.getPosition().x - 240, player.getPosition().y -40);
			batch.draw(enemy3, player.getPosition().x - 400, player.getPosition().y - 150);
		}
		batch.end();
		stage.act();
		stage.draw();
		
		playerHP.setText(player.getCurrentHP() + "/" + player.getMaxHP());
		barPlayer.setValue(player.getCurrentHP());
		if(i == 1){
			barEnemy1.setValue(gegner1.getCurrentHP());
			enemy1HP.setText(gegner1.getCurrentHP() + "/" + gegner1.getMaxHP());
		}
		if(i == 2){
			barEnemy1.setValue(gegner1.getCurrentHP());
			barEnemy2.setValue(gegner2.getCurrentHP());
			enemy1HP.setText(gegner1.getCurrentHP() + "/" + gegner1.getMaxHP());
			enemy2HP.setText(gegner2.getCurrentHP() + "/" + gegner2.getMaxHP());
		}
		if(i == 3){
			barEnemy1.setValue(gegner1.getCurrentHP());
			barEnemy2.setValue(gegner2.getCurrentHP());
			barEnemy3.setValue(gegner3.getCurrentHP());
			enemy1HP.setText(gegner1.getCurrentHP() + "/" + gegner1.getMaxHP());
			enemy2HP.setText(gegner2.getCurrentHP() + "/" + gegner2.getMaxHP());
			enemy3HP.setText(gegner3.getCurrentHP() + "/" + gegner3.getMaxHP());
		}
		
		if(anim){
			if(stateTime <= 0.8f){
	        	stateTime += Gdx.graphics.getDeltaTime();
	        	player.currentFrame = player.f1.getKeyFrame(stateTime);
	        }
			else{
				stateTime = 0;
				anim = false;
				player.currentFrame = player.f1.getKeyFrame(stateTime);
			}
			
		}		
		
		switch (battlestate){
			
			case firstStrike:
				if(Fighter.turnSpeed(gegner1) < Fighter.turnSpeed(Gamescreen.player)){
					battlestate = Battlestate.LeonTurn;
				}
				else{
					battlestate = Battlestate.EnemyTurn;
				}
				
				System.out.println("Vorher");
				fighterList.sort(Comparator.comparingInt(Fighter -> de.test.game.Fighter.getSpeed(Fighter)));
				fighterList.forEach(System.out::println);
				Collections.reverse(fighterList);
				fighterList.forEach(System.out::println);
			break;
		
			case LeonTurn:				
				battlemenu.setTouchable(Touchable.enabled);
				
				if(Battlemenu.hasClicked){
					Battlemenu.hasClicked = false;
					if(gegner1.curHP > 0){
						battlestate = Battlestate.EnemyTurn;
					}
					else{
						battlestate = Battlestate.BattleOver;
					}
				}
				
			break;
			
			case EnemyTurn:
				System.out.println(gegner1.getName() + " ist dran");
				battlemenu.setTouchable(Touchable.disabled);
				
				Fighter.attack(gegner1, player);
				System.out.println(gegner1.getName() + " greift an!");
				if(player.curHP <= 0){
					battlestate = Battlestate.GameOver;
				}
				else{
					battlestate = Battlestate.LeonTurn;
				}
			break;
			
			case BattleOver:
				battlemenu.setTouchable(Touchable.disabled);
				player.saveBattle();
				if(anim == false){
					dialog.setVisible(true);
				}
				fighterList.clear();	
			break;
			
			case GameOver:
				
				
			break;
		}
				
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		cam.update();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		font.dispose();
		skin.dispose();
		butwin.dispose();
	}
	
	public Monster enemySelect(){
		Random gen = new Random();
		int battleGen = gen.nextInt(3);
		Monster enemy;
		
		switch(battleGen){
			case 0:
				enemy = new Goblin();
			break;
			
			case 1:
				enemy = new Saubaer();
			break;
			
			case 2:
				enemy = new Daemonenhund();
			break;
			
			default:
				enemy = new Goblin();
			break;
		}
		System.out.println("Gegner: " + battleGen);
		return enemy;
	}
	
}
