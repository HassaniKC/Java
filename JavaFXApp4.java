/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapp4;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.PathTransition;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JavaFXApp4 extends Application {

    private Pane root;

    private List<GameObject> bullets = new ArrayList<>();
    private Enemy enemy = new Enemy();

    private GameObject player;

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(600, 600);

        player = new Player();
        player.setVelocity(new Point2D(0,-1));
        addGameObject(player, 300, 500);
        
        enemy = new Enemy();
        addGameObject(enemy, 0, 50);
        Line line = new Line(10, 60, 600, 60);
        PathTransition transition = new PathTransition();
        transition.setNode(enemy.getView());
        transition.setDuration(Duration.seconds(8));
        transition.setPath(line);
        transition.setCycleCount(PathTransition.INDEFINITE); 
        transition.play();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void addBullet(GameObject bullet, double x, double y) {
        bullets.add(bullet);
        addGameObject(bullet, x, y);
    }


    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void onUpdate() {
        for (GameObject bullet : bullets) {
            
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);

                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            
        }

        bullets.removeIf(GameObject::isDead);
       root.getChildren().remove(enemy);
       

        bullets.forEach(GameObject::update);
        enemy.update();

       // player.update();

       
    }

    private static class Player extends GameObject {
        Player() {
            super(new Rectangle(20, 40, Color.BLUE));
        }
    }

    private static class Enemy extends GameObject {
        Enemy() {
            super(new Circle(15, 15, 15, Color.RED));
        }
    }

    private static class Bullet extends GameObject {
        Bullet() {
            super(new Circle(5, 5, 5, Color.BROWN));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                player.rotateLeft();
            } else if (e.getCode() == KeyCode.RIGHT) {
                player.rotateRight();
            } else if (e.getCode() == KeyCode.UP) {
                Bullet bullet = new Bullet();
                
                double angle= Math.toRadians(player.getRotate());
                double x = player.getView().getTranslateX();
                double y= player.getView().getTranslateY();
                

                double x2 = x+ ( Math.cos(angle) - Math.sin(angle));
                double y2 = y+ ( Math.sin(angle) +   Math.cos(angle));
                
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                addBullet(bullet, x2, y2 );
                
               
                System.out.println("rotate player angle: "+ angle);
                System.out.println("player velocity: "+ player.getVelocity());
                System.out.println("Bullet velocity: "+ bullet.getVelocity());
                //System.out.println("player: ("+x+","+ y+")" );
                //System.out.println("bullet: ("+x2+","+ y2+")" );
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}