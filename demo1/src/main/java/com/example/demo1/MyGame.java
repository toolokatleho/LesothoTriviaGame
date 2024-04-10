package com.example.demo1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MyGame extends Application {
    private String[] mediaResources = {
            getResource("/images/kingdom.jpg").getUrl(),
            getResource("/images/landscape.jpg").getUrl(),
            getResource("/images/qiloane.mp4").getUrl(),
            getResource("/images/video.mp4").getUrl(),
            getResource("/images/Maseru.jpg").getUrl()
    };
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Timeline countdown;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-image: url('background_img.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 0);");

        VBox bottomSection = new VBox();
        bottomSection.getStyleClass().add("bottom-section");
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setSpacing(20);

        Label timerLabel = new Label("00:06");
        timerLabel.getStyleClass().add("timer-label");
        startCountdown(timerLabel, primaryStage);

        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.getStyleClass().add("score-label");

        bottomSection.getChildren().addAll(timerLabel, scoreLabel);
        root.setBottom(bottomSection);

        HBox centerSection = new HBox();
        centerSection.getStyleClass().add("center-section");
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setSpacing(20);

        VBox mediaSection = new VBox();
        mediaSection.getStyleClass().add("media-section");
        mediaSection.setAlignment(Pos.CENTER);
        mediaSection.setSpacing(20);

        Label questionText = new Label(questions[currentQuestionIndex]);
        questionText.getStyleClass().add("question-text");
        questionText.setWrapText(true);

        centerSection.getChildren().addAll(mediaSection, questionText);

        root.setCenter(centerSection);

        VBox optionsSection = new VBox();
        optionsSection.getStyleClass().add("options-section");
        optionsSection.setAlignment(Pos.CENTER);
        optionsSection.setSpacing(10);
        for (int i = 0; i < options[currentQuestionIndex].length; i++) {
            Button optionButton = new Button(options[currentQuestionIndex][i]);
            int finalI = i;
            optionButton.setOnAction(e -> handlePlayerAnswer(primaryStage, finalI));
            optionButton.getStyleClass().add("option-button");
            optionsSection.getChildren().add(optionButton);
        }
        root.setLeft(optionsSection);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Lesotho Trivia");
        primaryStage.show();
    }

    private String[] questions = {
            "Who founded the Basotho nation?",
            "What is the traditional Basotho clothing called?",
            "What is the official language of Lesotho?",
            "Which river forms part of the border between Lesotho and South Africa?",
            "What is the traditional Basotho hat called?"
    };

    private String[][] options = {
            {"Nkhono Mantsopa", "Mohlomi", "Lepoqo", "Peete"},
            {"Thethana", "Seshoshoe", "Tsheya", "Kobo"},
            {"English", "Sesotho", "French", "Zulu"},
            {"Caledon River", "Limpopo River", "Orange River", "Tugela River"},
            {"Mokorotlo", "Tophat", "Fez", "Sombrero"}
    };

    private int[] correctAnswers = {2, 0, 1, 0, 0};

    private void startCountdown(Label timerLabel, Stage stage) {
        countdown = new Timeline();
        countdown.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, e -> timerLabel.setText("00:06")),
                new KeyFrame(Duration.seconds(1), e -> {
                    int secondsLeft = Integer.parseInt(timerLabel.getText().substring(3)) - 1;
                    if (secondsLeft >= 0) {
                        timerLabel.setText(String.format("00:0%d", secondsLeft));
                    } else {
                        countdown.stop();
                        handlePlayerAnswer(stage, -1); // Automatically select correct answer when time runs out
                    }
                })
        );
        countdown.setCycleCount(6);
        countdown.playFromStart();
    }

    private void handlePlayerAnswer(Stage stage, int selectedAnswerIndex) {
        countdown.stop();
        VBox optionsSection = (VBox) ((BorderPane) stage.getScene().getRoot()).getLeft();
        Button[] optionButtons = optionsSection.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .toArray(Button[]::new);

        for (int i = 0; i < optionButtons.length; i++) {
            if (i == correctAnswers[currentQuestionIndex]) {
                optionButtons[i].getStyleClass().add("correct-answer");
            } else if (i == selectedAnswerIndex) {
                optionButtons[i].getStyleClass().add("wrong-answer");
            }
            optionButtons[i].setDisable(true);
        }

        if (selectedAnswerIndex == correctAnswers[currentQuestionIndex]) {
            score += 10;
        }

        Label scoreLabel = (Label) ((VBox) ((BorderPane) stage.getScene().getRoot()).getBottom()).getChildren().get(1);
        scoreLabel.setText("Score: " + score);

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            updateQuestion(stage);
        } else {
            showScore(stage);
        }
    }

    private void updateQuestion(Stage stage) {
        Label questionText = (Label) ((HBox) ((BorderPane) stage.getScene().getRoot()).getCenter()).getChildren().get(1);
        questionText.setText(questions[currentQuestionIndex]);

        VBox optionsSection = (VBox) ((BorderPane) stage.getScene().getRoot()).getLeft();
        optionsSection.getChildren().clear();

        for (int i = 0; i < options[currentQuestionIndex].length; i++) {
            Button optionButton = new Button(options[currentQuestionIndex][i]);
            int finalI = i;
            optionButton.setOnAction(e -> handlePlayerAnswer(stage, finalI));
            optionButton.getStyleClass().add("option-button");
            optionsSection.getChildren().add(optionButton);
        }
        displayMedia((VBox) ((HBox) ((BorderPane) stage.getScene().getRoot()).getCenter()).getChildren().get(0), mediaResources[currentQuestionIndex]);
    }

    private void displayMedia(VBox mediaSection, String mediaFile) {
        mediaSection.getChildren().clear();

        if (mediaFile.endsWith(".jpg") || mediaFile.endsWith(".png")) {
            ImageView imageView = new ImageView(new Image(mediaFile));
            imageView.setPreserveRatio(true);
            double mediaBoxWidth = 400;
            double mediaBoxHeight = 300;
            imageView.setFitWidth(mediaBoxWidth);
            imageView.setFitHeight(mediaBoxHeight);
            imageView.getStyleClass().add("media-image");
            mediaSection.getChildren().add(imageView);
        } else if (mediaFile.endsWith(".mp4")) {
            Media media = new Media(mediaFile);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.getStyleClass().add("media-video");
            double videoWidth = 640;
            double videoHeight = 360;
            mediaView.setFitWidth(videoWidth);
            mediaView.setFitHeight(videoHeight);
            Button playButton = new Button("Play");
            Button pauseButton = new Button("Pause");
            playButton.getStyleClass().add("media-control-button");
            pauseButton.getStyleClass().add("media-control-button");
            playButton.setOnAction(e -> mediaPlayer.play());
            pauseButton.setOnAction(e -> mediaPlayer.pause());
            mediaSection.getChildren().addAll(playButton, pauseButton, mediaView);
            mediaPlayer.play();
        }
    }

    private void showScore(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Your score: " + score);

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setOnAction(e -> {
            alert.close();
            restartGame(stage);
        });

        alert.getDialogPane().getButtonTypes().clear();
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        alert.getDialogPane().setContent(new VBox(new Label(alert.getContentText()), playAgainButton));

        alert.showAndWait();
    }

    private void restartGame(Stage stage) {
        currentQuestionIndex = 0;
        score = 0;
        countdown.playFromStart();
        start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Image getResource(String resourcePath) {
        return new Image(getClass().getResourceAsStream(resourcePath));
    }
}
