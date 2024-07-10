package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.GameScreen;

public class GameApp extends Game {
	private SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new GameScreen(batch));

		Gdx.audio.newSound(Gdx.files.internal("audio/267831__magedu__video-recorder-load-cassette-01v2.wav")).play();

		System.out.println("Hello World!");
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
