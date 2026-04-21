package io.github.Ya0Ya0.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuScreen {
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout layout;
    private int selectedOption = 0;
    private String[] menuOptions = {"Start Game", "Exit"};

    public MenuScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        titleFont.setColor(Color.YELLOW);

        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();
    }

    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Title
        String title = "MARIO KART 3D";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (Gdx.graphics.getWidth() - layout.width) / 2f, Gdx.graphics.getHeight() * 0.7f);

        // Menu options
        for (int i = 0; i < menuOptions.length; i++) {
            String option = menuOptions[i];
            if (i == selectedOption) {
                font.setColor(Color.YELLOW);
                option = "> " + option + " <";
            } else {
                font.setColor(Color.WHITE);
            }

            layout.setText(font, option);
            float yPosition = Gdx.graphics.getHeight() / 2f - (i * 80);
            font.draw(batch, option, (Gdx.graphics.getWidth() - layout.width) / 2f, yPosition);
        }

        // Controls info
        font.setColor(Color.GRAY);
        font.getData().setScale(1f);
        font.draw(batch, "Use UP/DOWN arrows to navigate, ENTER to select", 50, 80);
        font.draw(batch, "Controls: WASD to move, ESC to return to menu", 50, 50);
        font.getData().setScale(2f);

        batch.end();
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption--;
            if (selectedOption < 0) selectedOption = menuOptions.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption++;
            if (selectedOption >= menuOptions.length) selectedOption = 0;
        }
    }

    public boolean isStartSelected() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedOption == 0;
    }

    public boolean isExitSelected() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedOption == 1;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
        shapeRenderer.dispose();
    }
}
