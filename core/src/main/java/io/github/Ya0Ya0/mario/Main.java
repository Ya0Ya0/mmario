package io.github.Ya0Ya0.mario;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private GameState currentState;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    @Override
    public void create() {
        currentState = GameState.MENU;
        menuScreen = new MenuScreen();
    }

    @Override
    public void render() {
        switch (currentState) {
            case MENU:
                menuScreen.update();
                menuScreen.render();

                if (menuScreen.isStartSelected()) {
                    currentState = GameState.PLAYING;
                    if (gameScreen != null) {
                        gameScreen.dispose();
                    }
                    gameScreen = new GameScreen();
                }

                if (menuScreen.isExitSelected()) {
                    Gdx.app.exit();
                }
                break;

            case PLAYING:
                if (gameScreen != null) {
                    gameScreen.render();

                    if (gameScreen.shouldReturnToMenu()) {
                        currentState = GameState.MENU;
                    }
                }
                break;
        }
    }

    @Override
    public void dispose() {
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }
}
