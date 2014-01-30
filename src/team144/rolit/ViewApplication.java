package team144.rolit;

import java.awt.Canvas;

import team144.rolit.network.Client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

/**
 * The LibGDX Application instance tasked with rendering the board.
 */
public class ViewApplication extends ApplicationAdapter {
	private static final float BALL_SCALE = 0.55f;
	private static final float BOARD_OFFSET = 3.5f;
	
	private static final String ROLIT_BOARD_MODEL = "rolit.g3db";
	private static final String ROLIT_BALL_MODEL = "rolit_ball.g3db";
	
	private AssetManager assetManager;
	
	private Environment environment;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	
	private Vector2 camPos = new Vector2(5f, 0f);
	private Vector3 tmp = new Vector3();
	private Plane ground = new Plane(Vector3.Y, 0f);
	
	private Model ballModel;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private ModelInstance previewInstance;
	private ModelInstance boardInstance;
	
	private boolean loading = true;
	private boolean hasClicked;
	private boolean showPreview;
	
	private final Canvas canvas;
	private final Game game;
	private final Client client;
	
	public ViewApplication(Game game, Client client) {
		this.game = game;
		this.client = client;
		canvas = new LwjglAWTCanvas(this, true).getCanvas();
	}
	
	@Override
	public void create() {
		//create a new manager for asynchronous loading and specify assets
		assetManager = new AssetManager();
		assetManager.load(ROLIT_BOARD_MODEL, Model.class);
		assetManager.load(ROLIT_BALL_MODEL, Model.class);
		
		//construct the lighting
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		modelBatch = new ModelBatch();
		
		cam = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		Gdx.input.setInputProcessor(new InputHandler());
	}
	
	@Override
	public void render() {
		if (assetManager.update() && loading) finalizeLoading();
		
		//rotate and look at the center of the board
//		camPos.rotate(Gdx.graphics.getDeltaTime() * 5f);
		cam.position.set(camPos.x, 8f, camPos.y);
		cam.direction.set(tmp.set(cam.position).scl(-1f).nor());
		cam.up.set(Vector3.Y);
		cam.normalizeUp();
		cam.update();
		
		handleInput();
		
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if (!loading) {
			modelBatch.begin(cam);
			modelBatch.render(boardInstance, environment);
			modelBatch.render(instances, environment);
			if (showPreview) modelBatch.render(previewInstance, environment);
			modelBatch.end();
		}
	}
	
	/**
	 * Updates the board, called when the board changes.
	 */
	public void update() {
		if (loading) return;
		
		instances.clear();
		for (int x = 0; x < Board.DIMENSION; x++) {
			for (int y = 0; y < Board.DIMENSION; y++) {
				Tile tile = game.getBoard().getTile(x, y);
				
				if (tile != Tile.EMPTY) {
					ModelInstance instance = new ModelInstance(ballModel);
					
					setTransform(instance, x, y, tile);
					
					instances.add(instance);
				}
			}
		}
	}
	
	private void handleInput() {
		if (game.getCurrentPlayer() == client.getPlayer()) {
			Ray ray = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			
			//see where the mouse points at the ground
			Intersector.intersectRayPlane(ray, ground, tmp.set(0f, 0f, 0f));
			
			//by game graphics convention, y points up
			int x = (int) Math.floor(tmp.x) + 4;
			int y = (int) Math.floor(tmp.z) + 4;
			
			if (previewInstance != null) {
				setTransform(previewInstance, x, y, client.getPlayer().getTile());
				
				Color color = game.isValidMove(x, y) ? Color.LIGHT_GRAY : Color.DARK_GRAY;
				setColor(previewInstance, color);
			}
			
			showPreview = game.getBoard().getTile(x, y) == Tile.EMPTY;
			
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				if (!hasClicked) client.getPlayer().trySendMove(client, x, y);
				
				hasClicked = true;
			} else {
				hasClicked = false;
			}
		} else {
			showPreview = false;
		}
	}
	
	private void finalizeLoading() {
		System.out.println("Done loading");
		
		boardInstance = new ModelInstance(assetManager.get(ROLIT_BOARD_MODEL, Model.class));
		
		ballModel = assetManager.get(ROLIT_BALL_MODEL, Model.class);
		
		previewInstance = new ModelInstance(ballModel);
		
		setTransform(previewInstance, 0, 0, Tile.RED);
		
		loading = false;
		
		update();
	}
	
	private void setTransform(ModelInstance instance, int x, int y, Tile tile) {
		instance.transform.idt();
		instance.transform.translate(x - BOARD_OFFSET, -0.2f, y - BOARD_OFFSET);
		instance.transform.scl(BALL_SCALE);
		
		if (tile == Tile.YELLOW) instance.transform.rotate(0f, 1f, 0f, 180f);
		if (tile == Tile.GREEN) instance.transform.rotate(1f, 0f, 0f, 110f);
		if (tile == Tile.BLUE) {
			instance.transform.rotate(1f, 0f, 0f, 110f);
			instance.transform.rotate(0f, 1f, 0f, 120f);
		}
		if (tile == Tile.RED) {
			instance.transform.rotate(1f, 0f, 0f, 110f);
			instance.transform.rotate(0f, 1f, 0f, 240f);
		}
	}
	
	private void setColor(ModelInstance instance, Color color) {
		for (Material m : instance.materials) {
			m.set(new ColorAttribute(ColorAttribute.Diffuse, color));
		}
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = width;
		cam.viewportHeight = height;
	}
	
	private class InputHandler extends InputAdapter {
		private static final float ZOOM_SPEED = 20f;
		private static final float SCROLL_ZOOM_SPEED = 10f;
		private static final float ROTATION_SPEED = 90f;
		private static final float MAX_FOV = 120f;
		private static final float MIN_FOV = 30f;
		
		private int lastX, lastY;
		private int currentPointer = -1;
		
		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			if (pointer == currentPointer) {
				float dx = (float) (screenX - lastX) / (float) Gdx.graphics.getWidth();
				float dy = (float) (screenY - lastY) / (float) Gdx.graphics.getHeight();
				
				camPos.rotate(dx * ROTATION_SPEED);
				cam.fieldOfView += dy * ZOOM_SPEED;
				cam.fieldOfView = MathUtils.clamp(cam.fieldOfView, MIN_FOV, MAX_FOV);
			}
			
			lastX = screenX;
			lastY = screenY;
			
			return true;
		}
		
		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if(button == Buttons.RIGHT){
				lastX = screenX;
				lastY = screenY;
				
				currentPointer = pointer;
			}
			
			return true;
		}
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if(button == Buttons.RIGHT) currentPointer = -1;
			
			return true;
		}
		
		@Override
		public boolean scrolled(int amount) {
			cam.fieldOfView += amount * SCROLL_ZOOM_SPEED;
			cam.fieldOfView = MathUtils.clamp(cam.fieldOfView, MIN_FOV, MAX_FOV);
			
			return true;
		}
	}
}
