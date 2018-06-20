package com.company;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class PlayGround {
    public static int width = 600;
    public static int height = 350;
}

public class Main extends Application {

    public final int width = 600;
    public final int height = 400;

    Snake snake = new Snake();
    Mouse mouse = new Mouse();

    boolean isGameOver = false;

    private KeyCode lastKey = KeyCode.RIGHT;
    final Font font = Font.loadFont(Main.class.getResource("ARCADECLASSIC.ttf").toExternalForm(), 36);
    final Font smallFont = Font.loadFont(Main.class.getResource("ARCADECLASSIC.ttf").toExternalForm(), 24);

    @Override
   public void start(Stage stage) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new StackPane(canvas));
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(e ->  lastKey = e.getCode() );

        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(100),  e -> run(gc)));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
   }

   private void run(GraphicsContext gc) {
        handleInput();

        if(!isGameOver) {
            update();
            checkCollision();
        }

        draw(gc);
   }

   private void handleInput() {
        switch (lastKey) {
            case UP:
                snake.up();
                break;
            case DOWN:
                snake.down();
                break;
            case LEFT:
                snake.left();
                break;
            case RIGHT:
                snake.right();
                break;
            default:
                break;
        }

        if(isGameOver) {
            if(lastKey == KeyCode.R) {
                isGameOver = false;
                snake = new Snake();
                lastKey = KeyCode.RIGHT;
            }
        }
   }

   private void gameOver(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(font);
        gc.fillText("game over", 215, 150);

        gc.setFont(smallFont);
        gc.fillText("Press  r  to  Retry", 205, 180);
   }

   private void displayPoints(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(font);
        gc.fillText(Integer.toString(snake.getPoints()), 30, 385);

        gc.fillRect(5, 350, 590, 5);
   }

   private void checkCollision() {
        // check if mouse has been eaten
        if(snake.getHead().x == mouse.x && snake.getHead().y == mouse.y) {
            snake.grow();
            mouse.reborn();
        }

        // check if snake has eaten itself
        if(snake.checkHeadCollision()) {
            isGameOver = true;
        }
   }

   private void update() {
        snake.update();
   }

   private void draw(GraphicsContext gc) {
        // clear screen
        clearScreen(gc);

        displayPoints(gc);
        snake.draw(gc);
        mouse.draw(gc);

        if(isGameOver) {
            gameOver(gc);
        }
   }

   private void clearScreen(GraphicsContext gc) {
       gc.setFill(Color.BLACK);
       gc.fillRect(0, 0, width, height);
   }
}

enum Direction {
    UP, DOWN, LEFT, RIGHT
}

class SnakePart {

    public Direction direction;

    public int x;
    public int y;
    public int size = 10;

    public SnakePart(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, size, size);
    }
}

class Snake {

    ArrayList<SnakePart> body = new ArrayList<>();
    int speed = 10;
    int initialSize = 4;

    Direction direction = Direction.RIGHT;

    Snake() {
        for(int i = initialSize; i > -1; i--) {
            body.add(new SnakePart((10 * i) + 20, 200, direction));
        }
    }

    public boolean checkHeadCollision() {
        for(int i = 1; i < body.size(); i++) {
            if ( getHead().x == body.get(i).x && getHead().y == body.get(i).y) {
                return true;
            }
        }
        return false;
    }

    public SnakePart getHead() {
        return body.get(0);
    }

    public SnakePart getTail() {
        return body.get(body.size()-1);
    }

    public void grow() {
        int size = getHead().size;
        int x;
        int y;
        switch(direction) {
            case UP:
                x = getTail().x;
                y = getTail().y + size;
                break;
            case DOWN:
                x = getTail().x;
                y = getTail().y - size;
                break;
            case LEFT:
                x = getTail().x + size;
                y = getTail().y;
                break;
            case RIGHT:
                x = getTail().x - size;
                y = getTail().y;
                break;
            default:
                x = getTail().x;
                y = getTail().y;
        }
        Direction direction = getTail().direction;
        body.add(new SnakePart(x, y, direction));
    }

    public int getPoints() {
        return (body.size() - initialSize - 1) * 10;
    }

    void update() {
        for(int i = (body.size() - 1); i > 0; i--) {
            body.get(i).x = body.get(i-1).x;
            body.get(i).y = body.get(i-1).y;
            body.get(i).direction = body.get(i-1).direction;
        }

        switch (direction) {
            case UP:
                getHead().y -= speed;
                break;
            case DOWN:
                getHead().y += speed;
                break;
            case LEFT:
                getHead().x -= speed;
                break;
            case RIGHT:
                getHead().x += speed;
                break;
        }

        int size = getHead().size;
        if (getHead().y < 0) {
            getHead().y = PlayGround.height - size;
        }
        else if (getHead().y > PlayGround.height - size) {
            getHead().y = 0;
        }
        else if (getHead().x < 0) {
            getHead().x = PlayGround.width - size;
        }
        else if (getHead().x > PlayGround.width - size) {
            getHead().x = 0;
        }

    }

    public void draw(GraphicsContext gc) {
        for(int i = 0; i < body.size(); i++) {
            body.get(i).draw(gc);
        }
    }

    public void up(){
        if(direction != Direction.DOWN) {
            direction = Direction.UP;
        }
    }

    public void down(){
        if(direction != Direction.UP) {
            direction = Direction.DOWN;
        }

    }
    public void  left(){
        if(direction != Direction.RIGHT) {
            direction = Direction.LEFT;
        }
    }

    public void right(){
        if(direction != Direction.LEFT) {
            direction = Direction.RIGHT;
        }
    }

}

class Mouse {
    int x;
    int y;
    int size=10;

    public Mouse() {
        reborn();
    }

    public void reborn() {
        x = ThreadLocalRandom.current().nextInt(0, (PlayGround.width - size)+ 1);
        x = x - (x % size);

        y = ThreadLocalRandom.current().nextInt(0, (PlayGround.height - size) + 1);
        y = y - (y % size);
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, size, size);
    }
}
