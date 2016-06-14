package jmini3d.demo.gvr;

import android.content.Context;

import com.google.vr.sdk.base.HeadTransform;

import java.util.ArrayList;

import jmini3d.Scene;
import jmini3d.SceneController;
import jmini3d.Vector3;

/**
 * The scene controller selects the scene to show
 */
public class MySceneController implements SceneController {

	ArrayList<Scene> scenes = new ArrayList<>();

	float[] forward = new float[3];
	Vector3 forwardVector = new Vector3();
	long lastTimeStamp = -1;
	int sceneIndex = 0;

	VRTextureButton buttonLeft, buttonRight;

	public MySceneController(Context ctx) {
		buttonLeft = new VRTextureButton(2, 2, "arrow_left.png", new VRTextureButton.VRClickListener() {
			@Override
			public void onClickListener() {
				sceneIndex--;
				if (sceneIndex < 0) {
					sceneIndex = scenes.size() - 1;
				}
			}
		});
		buttonLeft.setPosition(-3, -3, -10);
		buttonRight = new VRTextureButton(2, 2, "arrow_right.png", new VRTextureButton.VRClickListener() {
			@Override
			public void onClickListener() {
				sceneIndex++;
				if (sceneIndex >= scenes.size()) {
					sceneIndex = 0;
				}
			}
		});
		buttonRight.setPosition(3, -3, -10);

		Scene scene = new SceneObjLoader(ctx);
		scene.addChild(buttonLeft.object3d);
		scene.addChild(buttonRight.object3d);
		scenes.add(scene);

		scene = new SceneCubes(ctx);
		scene.addChild(buttonLeft.object3d);
		scene.addChild(buttonRight.object3d);
		scenes.add(scene);
	}

	@Override
	public Scene getScene() {
		return scenes.get(sceneIndex);
	}

	@Override
	public boolean updateScene(int width, int height) {
		long currentTime = System.currentTimeMillis();
		long timeElapsed = lastTimeStamp == -1 ? 0 : currentTime - lastTimeStamp;
		lastTimeStamp = currentTime;

		// Now update the shown scene
		scenes.get(sceneIndex).setViewPort(width, height); // Mandatory
		if (sceneIndex == 0) {
			((SceneObjLoader) scenes.get(sceneIndex)).update(timeElapsed);
		}

		// And do not forget to animate the buttons
		buttonLeft.animate(timeElapsed);
		buttonRight.animate(timeElapsed);
		return true;
	}

	// Before each frame, the head position is notified
	public void setHeadTransform(HeadTransform headTransform) {
		headTransform.getForwardVector(forward, 0);
		forwardVector.setAll(forward, 0);

		// Check if we are looking to one of the two buttons
		buttonLeft.checkIsLooking(scenes.get(sceneIndex).camera.getPosition(), forwardVector);
		buttonRight.checkIsLooking(scenes.get(sceneIndex).camera.getPosition(), forwardVector);
	}
}