import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Post;
import models.User;

public class MyMomentApp extends Application {

    private Stage primaryStage;
    private User currentUser;
    private List<Post> posts = new ArrayList<>();
    private FlowPane postsContainer;
    private ScrollPane scrollPane;

    private ImageView profileImageViewRegister = new ImageView();
    private File selectedProfileImageFile;

    private static final double ASPECT_RATIO = 2.0 / 3.0;
    private double currentUploadRotation = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MyMoment");
        showRegisterScene();
        primaryStage.show();
    }

    private Image loadPlaceholderImage() {
        try {
            InputStream stream = getClass().getResourceAsStream("/placeholder.png");
            return (stream != null) ? new Image(stream) : null;
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
            return null;
        }
    }

    private void showRegisterScene() {
        VBox registerLayout = new VBox(20);
        registerLayout.setPadding(new Insets(50));
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.setStyle("-fx-background-color: #AEDFF7;"); 

        Label titleLabel = new Label("MyMoment");
        titleLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0B3D91;");

        TextField nickNameField = new TextField();
        nickNameField.setPromptText("Input User Account (Nick Name)");
        nickNameField.setMaxWidth(300);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.setMaxWidth(300);

        Button uploadProfileButton = new Button("Upload Foto Profil");
        profileImageViewRegister.setFitHeight(100);
        profileImageViewRegister.setFitWidth(100);
        Circle clip = new Circle(50, 50, 50);
        profileImageViewRegister.setClip(clip);
        profileImageViewRegister.setImage(loadPlaceholderImage());

        uploadProfileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Foto Profil");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            selectedProfileImageFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedProfileImageFile != null) {
                try {
                    Image image = new Image(new FileInputStream(selectedProfileImageFile));
                    profileImageViewRegister.setImage(image);
                } catch (FileNotFoundException ex) {
                    showAlert("Error", "File tidak ditemukan.");
                }
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String nickName = nickNameField.getText();
            String fullName = fullNameField.getText();
            if (nickName.isEmpty() || fullName.isEmpty() || selectedProfileImageFile == null) {
                showAlert("Input Error", "Mohon isi semua field dan upload foto profil.");
                return;
            }
            try {
                Image profileImage = new Image(new FileInputStream(selectedProfileImageFile));
                currentUser = new User(nickName, fullName, profileImage);
                showHomeScene();
            } catch (FileNotFoundException ex) {
                showAlert("Error", "Gagal memuat foto profil.");
            }
        });

        registerLayout.getChildren().addAll(
                titleLabel, nickNameField, fullNameField,
                uploadProfileButton, profileImageViewRegister, submitButton
        );

        Scene registerScene = new Scene(registerLayout, 500, 600);
        primaryStage.setScene(registerScene);
    }

    private void showHomeScene() {
        BorderPane homeLayout = new BorderPane();
        homeLayout.setPadding(new Insets(20));
        homeLayout.setStyle("-fx-background-color: #B3D4FC;"); 

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #0B3D91; -fx-background-radius: 10;");

        ImageView profileImageViewHome = new ImageView(currentUser.getProfileImage());
        profileImageViewHome.setFitHeight(80);
        profileImageViewHome.setFitWidth(80);
        Circle clip = new Circle(40, 40, 40);
        profileImageViewHome.setClip(clip);

        VBox userInfo = new VBox(5);
        Label nickNameLabel = new Label(currentUser.getNickName());
        nickNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18;");
        Label fullNameLabel = new Label(currentUser.getFullName());
        fullNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        userInfo.getChildren().addAll(nickNameLabel, fullNameLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addPostButton = new Button("Add Post");
        addPostButton.setStyle("-fx-background-color: #F9A825; -fx-text-fill: black; -fx-font-weight: bold;");
        addPostButton.setOnAction(e -> showUploadPostWindow());

        topBar.getChildren().addAll(profileImageViewHome, userInfo, spacer, addPostButton);
        homeLayout.setTop(topBar);

        postsContainer = new FlowPane();
        postsContainer.setPadding(new Insets(20));
        postsContainer.setAlignment(Pos.TOP_LEFT);
        postsContainer.setHgap(15);
        postsContainer.setVgap(15);
        postsContainer.setPrefWrapLength(630); 

        scrollPane = new ScrollPane(postsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        homeLayout.setCenter(scrollPane);


        updatePostsDisplay();
        Scene homeScene = new Scene(homeLayout, 650, 700);
        primaryStage.setScene(homeScene);
    }

    private void showUploadPostWindow() {
        currentUploadRotation = 0;

        Stage uploadStage = new Stage();
        uploadStage.initModality(Modality.APPLICATION_MODAL);
        uploadStage.initOwner(primaryStage);
        uploadStage.setTitle("Upload Post");

        VBox uploadLayout = new VBox(15);
        uploadLayout.setPadding(new Insets(30));
        uploadLayout.setAlignment(Pos.CENTER);

        ImageView postImageView = new ImageView(loadPlaceholderImage());
        postImageView.setFitHeight(200);
        postImageView.setFitWidth(200);
        postImageView.setPreserveRatio(true);

        final File[] selectedPostImageFile = {null};

        Button uploadImageButton = new Button("Upload Image");
        uploadImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Gambar Postingan");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            selectedPostImageFile[0] = fileChooser.showOpenDialog(uploadStage);
            if (selectedPostImageFile[0] != null) {
                try {
                    Image image = new Image(new FileInputStream(selectedPostImageFile[0]));
                    postImageView.setImage(image);
                    currentUploadRotation = 0;
                    postImageView.setRotate(currentUploadRotation);
                } catch (FileNotFoundException ex) {
                    showAlert("Error", "File tidak ditemukan.");
                }
            }
        });

        Button rotateButton = new Button("Rotate");
        rotateButton.setOnAction(e -> {
            currentUploadRotation = (currentUploadRotation + 90) % 360;
            postImageView.setRotate(currentUploadRotation);
        });

        TextField captionField = new TextField();
        captionField.setPromptText("Caption");
        captionField.setMaxWidth(300);

        Button submitPostButton = new Button("Submit");
        submitPostButton.setOnAction(e -> {
            String caption = captionField.getText();
            if (selectedPostImageFile[0] == null) {
                showAlert("Input Error", "Mohon upload gambar postingan.");
                return;
            }
            try {
                Image originalImage = new Image(new FileInputStream(selectedPostImageFile[0]));
                Image imageToPost = originalImage;
                if (currentUploadRotation != 0) {
                    ImageView tempView = new ImageView(originalImage);
                    tempView.setRotate(currentUploadRotation);
                    SnapshotParameters params = new SnapshotParameters();
                    params.setFill(Color.TRANSPARENT);
                    imageToPost = new Group(tempView).snapshot(params, null);
                }
                posts.add(new Post(caption, imageToPost));
                updatePostsDisplay();
                uploadStage.close();
            } catch (FileNotFoundException ex) {
                showAlert("Error", "Gagal memuat gambar postingan.");
            }
        });

        HBox buttonsRow = new HBox(10, uploadImageButton, rotateButton);
        buttonsRow.setAlignment(Pos.CENTER);

        uploadLayout.getChildren().addAll(buttonsRow, postImageView, captionField, submitPostButton);
        Scene uploadScene = new Scene(uploadLayout, 400, 500);
        uploadStage.setScene(uploadScene);
        uploadStage.showAndWait();
    }

    private void updatePostsDisplay() {
        postsContainer.getChildren().clear();
        for (Post post : posts) {
            VBox postBox = new VBox(5);
            postBox.setAlignment(Pos.CENTER);
            postBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5,0,0,1);");

            ImageView postImageView = new ImageView(post.getPostImage());
            postImageView.setPreserveRatio(true);
            postImageView.setFitWidth(200);
            postImageView.setCursor(Cursor.HAND);

            Label captionLabel = new Label(post.getCaption());
            captionLabel.setWrapText(true);
            captionLabel.setMaxWidth(200);
            captionLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #333;");

            postImageView.setOnMouseClicked(event -> {
                Stage popupStage = new Stage();
                popupStage.initOwner(primaryStage);
                popupStage.initModality(Modality.APPLICATION_MODAL);

                ImageView bigImageView = new ImageView(post.getPostImage());
                bigImageView.setPreserveRatio(true);
                bigImageView.setFitWidth(600);

                VBox box = new VBox(bigImageView);
                box.setAlignment(Pos.CENTER);
                box.setPadding(new Insets(20));
                Scene scene = new Scene(box);

                popupStage.setScene(scene);
                popupStage.setTitle("View Image");
                popupStage.show();
            });

            postBox.getChildren().addAll(postImageView, captionLabel);
            postsContainer.getChildren().add(postBox);
        }
    }

    private void resizeImages(double width) {
        postsContainer.setPrefWrapLength(630);
        for (var node : postsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox box = (VBox) node;
                for (var child : box.getChildren()) {
                    if (child instanceof ImageView) {
                        ((ImageView) child).setFitWidth(200);
                    }
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
