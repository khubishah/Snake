package ca.uwaterloo.cs349;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.shape.*;
import java.lang.Math;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.ArrayList;


public class Game extends Application {
    Image appleImg = new Image("apple-small.jpg");
    Image snakeImg = new Image("snake.jpg");
    Image snakeHeadEast = new Image("headEastFacing.jpg");
    Image snakeHeadNorth = new Image("headNorthFacing.jpg");
    Image snakeHeadWest = new Image("headWestFacing.jpg");
    Image snakeHeadSouth = new Image("headSouthFacing.jpg");
    private int score = 0;
    private int lvl = 0;
    private long lastUpdate = 0;
    private AnimationTimer timer;
    boolean paused = false;
    private MediaPlayer defeatPlayer;
    private MediaPlayer eatPlayer;
    private Media defeatTheme;
    private Media eatTheme;
    private Timeline t;
    private int time = 30;
    private Snake snake;
    private Apple initialApples = new Apple();
    private int applesObtained = 0;
    private ArrayList<Coordinate> applesOnBoard = new ArrayList<>(15);
    private final int boardXWidth = 20;
    private final int boardYHeight = 15;
    private ArrayList<Rectangle> squaresList = new ArrayList<>(300);
    Text timerLabel;
    Text scoreBoard;
    Text level;
    Text applesBoard;
    GridPane board;

    public long frameRateForLevel() {

        if (lvl == 1) {
            return 35_000_000;
        } else if (lvl == 2) {
            return 32_000_000;
        } else {
            return 29_000_000;
        }
    }

    public void renderFinalScore(Stage stage) {
        timer.stop();
        t.stop();

        Group pane = new Group();
        Text gameOver = createText("GAME OVER", Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 100),
                Color.LIMEGREEN, TextAlignment.CENTER);
        gameOver.setX(350);
        gameOver.setY(200);
        Text highScore = createText("High Score: " + score, Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 50),
                Color.DEEPPINK, TextAlignment.CENTER);
        highScore.setX(500);
        highScore.setY(300);
        Button button = new Button("Play Again");
        button.setFont( Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        button.setTranslateX(600);
        button.setTranslateY(400);
        button.setTextFill(Color.BLACK);
        button.setOnMouseClicked(mouseEvent -> {
            score = 0;
            applesObtained = 0;
            time = 30;
            applesOnBoard.clear();
            squaresList.clear();
            board.getChildren().removeAll();
            lvl = 1;
            renderLevel(stage);
        });

        pane.getChildren().add(gameOver);
        pane.getChildren().add(highScore);
        pane.getChildren().add(button);
        Scene scene = new Scene(pane, 1280, 800, Color.BLACK);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                boolean switchingLevels = false;
                if (event.getCode() == KeyCode.DIGIT1 || event.getCode() == KeyCode.DIGIT2 ||
                event.getCode() == KeyCode.DIGIT3 || event.getCode() == KeyCode.R) {
                    score = 0;
                    applesObtained = 0;
                    time = 30;
                    applesOnBoard.clear();
                    squaresList.clear();
                    board.getChildren().removeAll();
                }
                switch (event.getCode()) {
                    case DIGIT1:
                        lvl = 1;
                        renderLevel(stage);
                        break;
                    case DIGIT2:
                        lvl = 2;
                        renderLevel(stage);
                        break;
                    case DIGIT3:
                        lvl = 3;
                        renderLevel(stage);
                        break;
                    case R:
                        start(stage);
                        break;
                }
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public boolean validNewApple(int x, int y) {
        for (int i = 0; i < applesOnBoard.size(); i++) {
            Coordinate c = new Coordinate(applesOnBoard.get(i).x, applesOnBoard.get(i).y);
            if (c.x == x && c.y == y) return false;
        }
        if (snake.contains(new Coordinate(x,y))) return false;
        return true;
    }

    public void removeConsumedApple(int x, int y) {
        for (int i = 0; i < applesOnBoard.size(); i++) {
            Coordinate c = new Coordinate(applesOnBoard.get(i).x, applesOnBoard.get(i).y);
            if (c.x == x && c.y == y) {
                applesOnBoard.remove(i);
                break;
            }
        }
    }

    public int levelApples() {
        if (lvl == 1) {
            return 5;
        } else if (lvl == 2) {
            return 10;
        } else {
            return 15;
        }
    }

    public Coordinate addRandomApple() {
        boolean inValidApple = true;
        int newAppleX = 0;
        int newAppleY = 0;
        while (inValidApple) {
            newAppleX = (int)(Math.random() * boardXWidth) + 0;
            newAppleY = (int)(Math.random() * boardYHeight) + 0;
            if (validNewApple(newAppleX,newAppleY)) {
                break;
            }
        }
        return new Coordinate(newAppleX, newAppleY);
    }

    public Text createText(String value, Font f, Color c, TextAlignment t) {
        Text text = new Text();
        text.setText(value);
        text.setFont(f);
        text.setFill(c);
        text.setTextAlignment(t);
        return text;
    }
    public void rotateHead() {
        char newDirection = snake.getDirection();
        Coordinate c = new Coordinate(snake.getHead().x, snake.getHead().y);
        Rectangle square = new Rectangle(40,40);
        if (newDirection == 'N') {
            square.setFill(new ImagePattern(snakeHeadNorth));
        } else if (newDirection == 'E') {
            square.setFill(new ImagePattern(snakeHeadEast));
        } else if (newDirection == 'W') {
            square.setFill(new ImagePattern(snakeHeadWest));
        } else {
            square.setFill(new ImagePattern(snakeHeadSouth));
        }

        board.getChildren().remove(squaresList.get(c.x + 20*c.y));
        board.add(square, c.x, c.y);
        squaresList.set(c.x + 20*c.y, square);
    }
    public void switchLevels() {
        applesObtained = 0;
        time = 30;
        applesBoard.setText("Apples: " + applesObtained);
        level.setText("Level " + lvl);
        if (lvl == 3) {
            timerLabel.setText("");
        } else {
            timerLabel.setText("Time: " + time);
        }

        for (int i = 0; i < boardYHeight; i++) {
            for (int j = 0; j < boardXWidth; j++) {
                if (snake.contains(new Coordinate(j,i))) continue;
                Rectangle square = new Rectangle(40,40);
                square.setStroke(Color.FLORALWHITE);
                square.setFill(Color.BLACK);
                board.getChildren().remove(squaresList.get(j + 20*i));
                board.add(square, j,i);
                squaresList.add(square);
                squaresList.set(j + 20*i, square);
            }
        }
        applesOnBoard.clear();

        // initialized fixed pattern of initial apples for new level
        for (int i = 0; i < levelApples(); i++) {
            Coordinate apple = new Coordinate(initialApples.placements.get(i).x, initialApples.placements.get(i).y);
            if (snake.contains(apple)) {
                Coordinate c = addRandomApple();
                apple = new Coordinate(c.x, c.y);
            }
            Rectangle square = new Rectangle(40,40);
            square.setFill(new ImagePattern(appleImg));
            board.getChildren().remove(squaresList.get(apple.x + 20*apple.y));
            board.add(square, apple.x, apple.y);
            squaresList.set(apple.x + 20*apple.y, square);
            applesOnBoard.add(apple);
        }
        if (lvl == 1 || lvl == 2) {
            t.playFromStart();
        }
        timer.start();
    }
    public void renderLevel(Stage stage) {
        snake = new Snake();
        t = new Timeline();
        stage.setTitle("Level " + lvl);
        GridPane levelScreen = new GridPane();
        levelScreen.setVgap(5);
        levelScreen.setHgap(50);
        levelScreen.setAlignment(Pos.CENTER);
        levelScreen.setPrefSize(1200,800);

        board = new GridPane();
        board.setPrefSize(800,600);
        for (int i = 0; i < boardYHeight; i++) {
            for (int j = 0; j < boardXWidth; j++) {
                Rectangle square = new Rectangle(40,40);
                square.setStroke(Color.FLORALWHITE);
                square.setFill(Color.BLACK);
                board.add(square, j,i);
                squaresList.add(square);
            }
        }

        ArrayList<Coordinate> coordinates = snake.getCoordinates();

        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate c = new Coordinate(coordinates.get(i).x, coordinates.get(i).y);

            Rectangle square = new Rectangle(40,40);
            square.setFill(new ImagePattern(snakeImg));
            if (i == 0) {
                square.setFill(new ImagePattern(snakeHeadEast));
                square.setRotate(-90);
            }
            board.getChildren().remove(squaresList.get(c.x + 20*c.y));
            board.add(square, c.x, c.y);
            squaresList.set(c.x + 20*c.y, square);
        }

        for (int i = 0; i < levelApples(); i++) {
            Coordinate apple = new Coordinate(initialApples.placements.get(i).x, initialApples.placements.get(i).y);
            if (snake.contains(apple)) {
                Coordinate c = addRandomApple();
                apple = new Coordinate(c.x, c.y);
            }
            Rectangle square = new Rectangle(40,40);
            square.setFill(new ImagePattern(appleImg));
            board.getChildren().remove(squaresList.get(apple.x + 20*apple.y));
            board.add(square, apple.x, apple.y);
            squaresList.set(apple.x + 20*apple.y, square);
            applesOnBoard.add(apple);
        }


        level = createText("Level " + lvl, Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 40),
                Color.LIMEGREEN, TextAlignment.CENTER);

        timerLabel = createText(lvl == 3 ? "" : "Time: " + time, Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20),
                Color.DEEPPINK, TextAlignment.CENTER);
        scoreBoard = createText("Score: " + score, Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20),
                Color.DEEPPINK, TextAlignment.CENTER);

        applesBoard = createText("Apples: " + applesObtained, Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20),
                Color.DEEPPINK, TextAlignment.CENTER);

        levelScreen.add(scoreBoard, 0,0);
        levelScreen.add(applesBoard, 0,1);
        levelScreen.add(level, 1,1);
        levelScreen.add(timerLabel, 1,0);
        levelScreen.add(board, 0,2);
        Scene levelScene = new Scene(levelScreen, 1280, 800, Color.BLACK);
        stage.setScene(levelScene);
        stage.show();

        levelScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                boolean switchingLevels = false;
                switch (event.getCode()) {
                    case LEFT:

                        snake.changeDirection('L');
                        rotateHead();
                        break;
                    case RIGHT:
                        snake.changeDirection('R');
                        rotateHead();
                        break;
                    case DIGIT1:
                        if (lvl != 1) {
                            timer.stop();
                            t.stop();
                            lvl = 1;
                            switchingLevels = true;
                        }
                        break;
                    case DIGIT2:
                        if (lvl != 2) {
                            timer.stop();
                            t.stop();
                            lvl = 2;
                            switchingLevels = true;
                        }
                        break;
                    case DIGIT3:
                        if (lvl != 3) {
                            timer.stop();
                            t.stop();
                            lvl = 3;
                            switchingLevels = true;
                        }
                        break;
                    case P:
                        if (!paused) {
                            paused = true;
                            timer.stop();
                            t.stop();
                        } else {
                            paused = false;
                            timer.start();
                            t.play();
                        }
                        break;
                    case Q:
                        timer.stop();
                        t.stop();
                        renderFinalScore(stage);
                        break;
                    case R:
                        timer.stop();
                        t.stop();
                        score = 0;
                        applesObtained = 0;
                        time = 30;
                        applesOnBoard.clear();
                        squaresList.clear();
                        board.getChildren().removeAll();
                        start(stage);
                        break;
                }
                if (switchingLevels) {
                    switchLevels();
                }

            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate <= frameRateForLevel()) { // 1 frame every 40 ms
                    return;
                }
                lastUpdate = now;
                Coordinate tail = new Coordinate(snake.getTail().x, snake.getTail().y);
                snake.move();
                if (snake.checkDead() == true) {
                    defeatPlayer.stop();
                    defeatPlayer.play();
                    defeatPlayer.setStartTime(Duration.ZERO);
                    renderFinalScore(stage);
                    return;
                }
                Coordinate head = new Coordinate(snake.getHead().x, snake.getHead().y);
                Coordinate pieceBeforeHead = new Coordinate(snake.getCoordinates().get(1).x, snake.getCoordinates().get(1).y);

                Rectangle beforeSquare = new Rectangle(40,40);
                beforeSquare.setFill(new ImagePattern(snakeImg));
                board.getChildren().remove(squaresList.get(pieceBeforeHead.x + 20*pieceBeforeHead.y));
                board.add(beforeSquare, pieceBeforeHead.x, pieceBeforeHead.y);
                squaresList.set(pieceBeforeHead.x + 20 * pieceBeforeHead.y, beforeSquare);

                rotateHead();

                Rectangle eraseTail = new Rectangle(40, 40);
                eraseTail.setStroke(Color.FLORALWHITE);
                eraseTail.setFill(Color.BLACK);
                board.getChildren().remove(squaresList.get(tail.x + 20 * tail.y));
                board.add(eraseTail, tail.x, tail.y);
                squaresList.set(tail.x + 20 * tail.y, eraseTail);


                if (snake.checkApplesCaptured(applesOnBoard)) {
                    eatPlayer.stop();
                    eatPlayer.play();

                    removeConsumedApple(head.x, head.y);
                    score++;
                    applesObtained++;
                    scoreBoard.setText("Score: " + score);
                    applesBoard.setText("Apples: " + applesObtained);

                    snake.grow(applesOnBoard);
                    Coordinate growth = new Coordinate(snake.getTail().x, snake.getTail().y);
                    Rectangle grownTail = new Rectangle(40, 40);
                    //grownTail.setStroke(Color.LIMEGREEN);
                    grownTail.setFill(new ImagePattern(snakeImg));
                    board.getChildren().remove(squaresList.get(growth.x + 20 * growth.y));
                    board.add(grownTail, growth.x, growth.y);
                    squaresList.set(growth.x + 20 * growth.y, grownTail);

                    boolean inValidApple = true;
                    int newAppleX = 0;
                    int newAppleY = 0;
                    while (inValidApple) {
                        newAppleX = (int) (Math.random() * boardXWidth) + 0;
                        newAppleY = (int) (Math.random() * boardYHeight) + 0;
                        if (validNewApple(newAppleX, newAppleY)) {
                            break;
                        }
                    }
                    applesOnBoard.add(new Coordinate(newAppleX, newAppleY));
                    Rectangle square = new Rectangle(40, 40);
                    square.setFill(new ImagePattern(appleImg));
                    board.getChildren().remove(squaresList.get(newAppleX + 20 * newAppleY));
                    board.add(square, newAppleX, newAppleY);
                    squaresList.set(newAppleX + 20 * newAppleY, square);
                    eatPlayer.setStartTime(Duration.ZERO);
                }
            }

        };

        t = new Timeline(new KeyFrame(Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (lvl != 3) {
                            if (time > 0) {
                                time--;
                                timerLabel.setText("Time: " + time);
                            } else {
                                timer.stop();
                                t.stop();
                                lvl = lvl == 1 ? 2 : 3;
                                switchLevels();
                            }
                        }
                    }
                }));

        t.setCycleCount(Timeline.INDEFINITE);
        t.playFromStart();
        timer.start();


    }
    public void start(Stage stage)
    {
        defeatTheme = new Media(getClass().getClassLoader().getResource("defeat.wav").toExternalForm());
        defeatPlayer = new MediaPlayer(defeatTheme);
        eatTheme = new Media(getClass().getClassLoader().getResource("eating.wav").toExternalForm());
        eatPlayer = new MediaPlayer(eatTheme);
        stage.setTitle("Snake Splash Screen");
        stage.setResizable(false);

        Text gameTitle = new Text();
        gameTitle.setText("Snake");
        gameTitle.setFont(Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 75));
        gameTitle.setX(560);
        gameTitle.setY(80);
        gameTitle.setFill(Color.LIMEGREEN);

        Text instructionHeader = new Text();
        instructionHeader.setText("How to Play:");
        instructionHeader.setFont(Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 40));
        instructionHeader.setX(560);
        instructionHeader.setY(150);
        instructionHeader.setFill(Color.DEEPPINK);

        Text instructions = new Text();
        instructions.setText("Hiss! You're a hungry snake who is always in motion. \nTry to grow your size and score points by eating as many fruits as possible " +
                "without going off the grid, or running into yourself. \nA timer ticks down on each level, so when " +
                "the timer runs out, you move on to the next level.\n For each level, your speed increases as well as the number " +
                "of available fruits. \nYou can only use the left and right arrow keys to change direction " +
                "relative to your current path. \nHit key 1 to play! Good luck.");
        instructions.setFont(Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        instructions.setX(100);
        instructions.setY(200);
        instructions.setFill(Color.FLORALWHITE);
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.setLineSpacing(7.0f);

        Text keyboardShortcutsTitle = new Text();
        keyboardShortcutsTitle.setText("Keyboard Shortcuts:");
        keyboardShortcutsTitle.setFont(Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 40));
        keyboardShortcutsTitle.setX(470);
        keyboardShortcutsTitle.setY(400);
        keyboardShortcutsTitle.setFill(Color.DEEPPINK);
        keyboardShortcutsTitle.setTextAlignment(TextAlignment.CENTER);
        keyboardShortcutsTitle.setLineSpacing(7.0f);

        Text keyboardShortcuts = new Text();
        keyboardShortcuts.setText("Left/Right Arrows: Turn the snake\nP: pause" +
                "\nR: reset to the splash screen\n1: Start Level \n2: Start Level 2\n3: Start Level 3\nQ: Quit and display the high score");
        keyboardShortcuts.setFont(Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        keyboardShortcuts.setX(490);
        keyboardShortcuts.setY(450);
        keyboardShortcuts.setFill(Color.FLORALWHITE);
        keyboardShortcuts.setTextAlignment(TextAlignment.CENTER);
        keyboardShortcuts.setLineSpacing(7.0f);

        Text name = new Text();
        name.setText("Made by Khubi Shah (ID: 20764562)");
        name.setFont(Font.font("helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        name.setX(490);
        name.setY(700);
        name.setFill(Color.DEEPPINK);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setLineSpacing(7.0f);

        Group root = new Group();
        root.getChildren().add(gameTitle);
        root.getChildren().add(instructionHeader);
        root.getChildren().add(instructions);
        root.getChildren().add(keyboardShortcutsTitle);
        root.getChildren().add(keyboardShortcuts);
        root.getChildren().add(name);

        Scene splashScreen = new Scene(root, 1280, 800, Color.BLACK);

        stage.setScene(splashScreen);

        stage.show();

        splashScreen.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                switch (event.getCode()) {
                    case DIGIT1:
                        lvl = 1;
                        renderLevel(stage);
                        break;
                    case DIGIT2:
                        lvl = 2;
                        renderLevel(stage);
                        break;
                    case DIGIT3:
                        lvl = 3;
                        renderLevel(stage);
                        break;
                }
            }
        });
    }

}