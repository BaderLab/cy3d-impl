package org.cytoscape.paperwing.internal;
import java.util.Set;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseWheelMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.FastMath;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class OrbitCamControl {
	
	private enum RotateType { HORIZONTAL, VERTICAL }
	
    private Camera camera;
    
    private Vector3 position = new Vector3();
    private Vector3 target = new Vector3();
    private Vector3 upAxis = new Vector3();
    private Vector3 leftAxis = new Vector3();
    private Vector3 worldUpAxis = new Vector3();
    private Vector3 defaultTarget = new Vector3();
    
    private double zoomSpeed = 0.003;
    private double horizontalRotateSpeed = 0.008;
    private double verticalRotateSpeed = 0.008;
    private double rollSpeed = 0.001;
    
    private double distance = 10;
    private double minDistance = 2;
    private double maxDistance = 100;
    private double defaultDistance = 10;
    
    private double xMoveSpeed = 0.01;
    private double yMoveSpeed = 0.01;
    private double zMoveSpeed = 0.01;
    
    private double translateSpeed = 0.015;
    
    private InputTrigger mouseTrigger;
    
    public OrbitCamControl(Camera camera, Vector3 target, Vector3 worldUpAxis) {
    	this.target.set(target);
    	this.camera = camera;
    	this.worldUpAxis.set(worldUpAxis);
    	position.set(camera.getLocation());
    	defaultTarget.set(target);
    	
    	// Calculate camera distance
    	distance = position.distance(target);
    	defaultDistance = distance;
    	
    	upAxis.set(camera.getUp());
    	leftAxis.set(camera.getLeft());
    	
    	System.out.println(position);
    }
    
    public void setTarget(ReadOnlyVector3 target) {
    	// System.out.println("previous camera location: " + camera.getLocation());
    	// System.out.println("previous position: " + position);
    	// System.out.println("previous target: " + this.target);
    	
    	// Find translation vector
    	Vector3 offset = target.subtract(this.target, null);
    	
    	camera.setLocation(offset.addLocal(camera.getLocation()));
    	this.target.set(target);
    	position.set(camera.getLocation());
 
    	// System.out.println("new camera location: " + camera.getLocation());
    	// System.out.println("new position: " + position);
    	// System.out.println("new target: " + this.target);
    }
    
    private void resetTranslation() {
    	setTarget(defaultTarget);
    }
    
    private void resetZoom() {
    	zoom(distance - defaultDistance);
    }
    
    private void translate(ReadOnlyVector3 translation) {
    	setTarget(target.add(translation, null));
    }
    
    private void zoom(double amount) {
    	double newDistance = distance - amount;
    	newDistance = Math.max(minDistance, newDistance);
    	newDistance = Math.min(maxDistance, newDistance);
    	
    	// Offset vector from target to camera
    	Vector3 offset = new Vector3();
    	position.subtract(target, offset);
    	
    	offset.normalizeLocal();
    	offset.multiplyLocal(newDistance);
    	
    	distance = newDistance;
    	camera.setLocation(target.add(offset, position));
    }
    
    private void roll(double angle) {
    	// Parametric equation for circle in 3D space:
    	// P = R(cos(t)u + sin(t)nxu) + c
    	//
    	// Where:
    	//  -u is a unit vector from the centre of the circle to any point
    	// on the circumference
    	//  -R is the radius
    	//  -n is a unit vector perpendicular to the plane
    	//  -c is the centre of the circle.
    	
    	Vector3 newUp = new Vector3();
    	
    	// Calculate (cos(t)u + sin(t)nxu)
    	newUp.set(camera.getDirection().multiply(-1, null));
    	newUp.crossLocal(camera.getUp());
    	newUp.multiplyLocal(FastMath.sin(angle));
    	
    	newUp.addLocal(camera.getUp().multiply(FastMath.sin(angle), null));
    	newUp.normalizeLocal();
  
    	// Update the camera
    	camera.setUp(newUp);
    	camera.setLeft(camera.getDirection().cross(newUp, null).normalizeLocal().multiply(-1, null));
  
    	upAxis.set(camera.getUp());
    	leftAxis.set(camera.getLeft());
    	
    	// camera.g
    }
    
    private void rotateVector(ReadOnlyVector3 direction, ReadOnlyVector3 normal, double angle) {
    	// Parametric equation for circle in 3D space:
    	// P = R(cos(t)u + sin(t)nxu) + c
    	//
    	// Where:
    	//  -u is a unit vector from the centre of the circle to any point
    	// on the circumference
    	//  -R is the radius
    	//  -n is a unit vector perpendicular to the plane
        //  -c is the centre of the circle.
    	
    	// Vector3 normalized = direction.normalize(null);
    	// Vector3 
    }
    
    private void orbit(double angle, RotateType type) {
    	// Parametric equation for circle in 3D space:
    	// P = R(cos(t)u + sin(t)nxu) + c
    	//
    	// Where:
    	//  -u is a unit vector from the centre of the circle to any point
    	// on the circumference
    	//  -R is the radius
    	//  -n is a unit vector perpendicular to the plane
        //  -c is the centre of the circle.
    	
    	// TODO correct the distance?
    	// TODO check if need to normalize the up vector?
    	
    	Vector3 buffer = new Vector3();
    	Vector3 newPosition = new Vector3();
    	Vector3 unitRelativeToCentre = new Vector3();
    	
    	// Obtain vector u
    	position.subtract(target, buffer);
    	buffer.normalize(unitRelativeToCentre);
    	
    	// Obtain cross product nxu
    	if (type == RotateType.HORIZONTAL) {
    		upAxis.normalize(newPosition);
    	} else if (type == RotateType.VERTICAL) {
    		leftAxis.normalize(newPosition);
    	}
    	newPosition.crossLocal(unitRelativeToCentre);
    	
    	// Obtain sum cos(t)u + sin(t)nxu
    	newPosition.multiplyLocal(FastMath.sin(angle));
    	newPosition.addLocal(unitRelativeToCentre.multiply(FastMath.cos(angle), null));
    	
    	// Multiply by R, obtaining R(cos(t)u + sin(t)nxu)
    	newPosition.multiplyLocal(distance);
    	
    	// Add c to result. This results in the new position of the camera
    	newPosition.addLocal(target);
    	
    	// Obtain new unit direction vector from center to camera
    	newPosition.subtract(target, unitRelativeToCentre);
    	unitRelativeToCentre.normalizeLocal();
    	
    	// Obtain projection of previous camera up vector onto the new direction vector
    	Vector3 newUpVector = new Vector3(camera.getUp());
    	Vector3 projectionOntoDirection = new Vector3();
    	unitRelativeToCentre.multiply(unitRelativeToCentre.dot(newUpVector), projectionOntoDirection);
    	
    	// Obtain new up vector by subtracting this projection from the old one
    	newUpVector.subtractLocal(projectionOntoDirection);
    	newUpVector.normalizeLocal();
    	
    	camera.setLocation(newPosition);
    	// TODO: Check if can just use camera's lookAt method
    	camera.setDirection(target.subtract(newPosition, null).normalizeLocal());
    	camera.setUp(newUpVector);
    	
    	// Use (unit direction vector) x (up vector) to give left vector
    	camera.setLeft(unitRelativeToCentre.crossLocal(newUpVector));
    	
    	// Keep track of updated camera data
    	upAxis.set(camera.getUp());
    	leftAxis.set(camera.getLeft());
    	position.set(camera.getLocation());
    }
    
    private void rotate(double horizontal, double vertical) {
    	orbit(-horizontal, RotateType.HORIZONTAL);
    	orbit(-vertical, RotateType.VERTICAL);
    }
    
    private void handleKeyboard(KeyboardState keyBoard) {
    	Set<Key> keysDown = keyBoard.getKeysDown();
    	
    	// System.out.println(keysDown);
    	
    	if (keysDown.contains(Key.LEFT)) {
    		translate(leftAxis.multiply(translateSpeed, null));
    	}
    	
    	if (keysDown.contains(Key.RIGHT)) {
    		translate(leftAxis.multiply(-translateSpeed, null));
    	}
    	
    	if (keysDown.contains(Key.UP)) {
    		translate(upAxis.multiply(translateSpeed, null));
    	}
    	
    	if (keysDown.contains(Key.DOWN)) {
    		translate(upAxis.multiply(-translateSpeed, null));
    	}
    	
    	if (keysDown.contains(Key.SPACE)) {
    		System.out.println("Position: " + position);
    		System.out.println("Target: " + target);
    	}
    	
    	if (keysDown.contains(Key.Q)) {
    		roll(-rollSpeed);
    	}
    	
    	if (keysDown.contains(Key.E)) {
    		roll(rollSpeed);
    	}
    }
    
    public void setupInputTriggers(final LogicalLayer layer) {
        Predicate<TwoInputStates> mouseDown = Predicates.or(TriggerConditions.leftButtonDown(), Predicates
                .or(TriggerConditions.rightButtonDown(), TriggerConditions.middleButtonDown()));
        
        Predicate<TwoInputStates> mouseDragged = Predicates.and(TriggerConditions.mouseMoved(), mouseDown);
        Predicate<TwoInputStates> scrollWheelMoved = new MouseWheelMovedCondition();
        
        TriggerAction mouseAction = new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                final MouseState mouse = inputStates.getCurrent().getMouseState();
                if (mouse.getDx() != 0 || mouse.getDy() != 0) {
                    rotate(horizontalRotateSpeed * mouse.getDx(), verticalRotateSpeed * mouse.getDy());
                }

                if (mouse.getDwheel() != 0) {
                    zoom(zoomSpeed * mouse.getDwheel());
                }
            }
        };

        Predicate<TwoInputStates> predicate = Predicates.or(scrollWheelMoved, mouseDragged);
        mouseTrigger = new InputTrigger(predicate, mouseAction);
        layer.registerTrigger(mouseTrigger);
        
        layer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.LEFT), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				translate(leftAxis.multiply(translateSpeed, null));
			}
		}));
        
        layer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.RIGHT), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				translate(leftAxis.multiply(-translateSpeed, null));
			}
		}));
        
        layer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.UP), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				translate(upAxis.multiply(translateSpeed, null));
			}
		}));
        
        layer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.DOWN), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				translate(upAxis.multiply(-translateSpeed, null));
			}
		}));
        
        layer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.C), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				resetTranslation();
				resetZoom();
			}
		}));
    	
    }
    
}