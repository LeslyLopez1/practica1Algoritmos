package com.example.demoxxx;

import com.example.demoxxx.solitaire.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;

public class HelloApplication extends Application {

    private SolitaireGame game;

    //paneles
    private Pane drawPilePane;
    private Pane wastePilePane;
    private ArrayList<Pane> tableauPanes;
    private ArrayList<Pane> foundationPanes;

    //atributos para origen-destino
    private CartaInglesa cartaSeleccionada;
    private OrigenCarta origenSeleccionado;
    private int indiceSeleccionado;

    @Override
    public void start(Stage stage) {
        // Inicializar paneles
        tableauPanes = new ArrayList<>();
        foundationPanes = new ArrayList<>();

        //layout Principal VERDE
        HBox mainLayout = new HBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #0B6623;");

        //1-LEFT (WASTE P Y DRAW P)
        VBox leftColumn = new VBox(15);

        //drawPile
        drawPilePane = new Pane();
        drawPilePane.setPrefSize(100, 150);
        drawPilePane.setStyle("-fx-border-color: transparent; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");
        drawPilePane.setOnMouseClicked(e -> manejarClickDraw());

        //wastePile
        wastePilePane = new Pane();
        wastePilePane.setPrefSize(100, 150);
        wastePilePane.setStyle("-fx-border-color: transparent; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");

        leftColumn.getChildren().addAll(drawPilePane, wastePilePane);

        //2-CENTER (tableusitos 7)
        HBox tableauBox = new HBox(15);
        for (int i = 0; i < 7; i++) {
            Pane tableauPane = new Pane();
            tableauPane.setPrefSize(100, 500);
            tableauPane.setMinWidth(100);
            tableauPane.setStyle("-fx-border-color: rgba(255,255,255,0.15); " +
                    "-fx-border-width: 2; " +
                    "-fx-border-style: dashed; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8;");
            tableauPanes.add(tableauPane);
            tableauBox.getChildren().add(tableauPane);

            final int idx = i;
            tableauPane.setOnMouseClicked(e -> manejarClickTableauVacio(idx));

        }

        //3-RIGHT (foundations)
        VBox rightColumn = new VBox(15);
        for (int i = 0; i < 4; i++) {
            Pane foundationPane = new Pane();
            foundationPane.setPrefSize(100, 150);
            foundationPane.setStyle("-fx-border-color: rgba(255,255,255,0.25); " +
                    "-fx-border-width: 3; " +
                    "-fx-border-style: dashed; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-color: rgba(0,100,0,0.3); " +
                    "-fx-background-radius: 8;");
            foundationPanes.add(foundationPane);
            rightColumn.getChildren().add(foundationPane);

            final int idx = i;
            foundationPane.setOnMouseClicked(e -> manejarClickFoundation(idx)
            );
        }

        //une todo al layout
        mainLayout.getChildren().addAll(leftColumn, tableauBox, rightColumn);

        //boton de reiniciar
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: #0B6623;");

        // Barra superior con botón
        HBox botonReiniciar = new HBox(15);
        botonReiniciar.setPadding(new Insets(10, 20, 0, 20));

        Button botoncitoR = new Button("Reiniciar");
        botoncitoR.setStyle("-fx-background-color: #2d5a1a; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 16; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;");
        botoncitoR.setOnMouseEntered(e ->
                botoncitoR.setStyle(botoncitoR.getStyle().replace("#2d5a1a", "#3d7a29")));
        botoncitoR.setOnMouseExited(e ->
                botoncitoR.setStyle(botoncitoR.getStyle().replace("#3d7a29", "#2d5a1a")));
        botoncitoR.setOnAction(e -> reiniciarJuego());

        botonReiniciar.getChildren().add(botoncitoR);
        root.getChildren().addAll(botonReiniciar, mainLayout);

        //inicializacion game
        game = new SolitaireGame();
        actualizarVista();
        Scene scene = new Scene(root, 1200, 650);
        stage.setTitle("Solitario");
        stage.setScene(scene);
        stage.show();
    }

    private void actualizarVista() {
        actualizarDrawPile();
        actualizarWastePile();
        actualizarFoundations();
        actualizarTableaux();
        verificarVictoria();
    }

    private void actualizarDrawPile() {
        drawPilePane.getChildren().clear();

        if (game.getDrawPile().hayCartas()) {
            CartaInglesa dorso = new CartaInglesa(1, Palo.CORAZON, "rojo");
            dorso.makeFaceDown();
            drawPilePane.getChildren().add(new CartaGrafica(dorso).getImagenVista());
        }
    }

    private void actualizarWastePile() {
        wastePilePane.getChildren().clear();

        CartaInglesa carta = game.getWastePile().verCarta();
        if (carta != null) {
            CartaGrafica cartaGrafica = new CartaGrafica(carta);

            //resaltado seleccion
            if (origenSeleccionado == OrigenCarta.WASTE) {
                cartaGrafica.getImagenVista().setEffect(new javafx.scene.effect.DropShadow(25, Color.YELLOW));
                cartaGrafica.getImagenVista().setScaleX(1.05);
                cartaGrafica.getImagenVista().setScaleY(1.05);
            }
            cartaGrafica.getImagenVista().setOnMouseClicked(e -> manejarClickWaste());
            cartaGrafica.getImagenVista().setStyle("-fx-cursor: hand;");
            wastePilePane.getChildren().add(cartaGrafica.getImagenVista());
        }
    }

    private void actualizarFoundations() {
        ArrayList<FoundationDeck> foundations = game.foundation;

        for (int i = 0; i < foundations.size(); i++) {
            Pane pane = foundationPanes.get(i);
            pane.getChildren().clear();

            FoundationDeck foundation = foundations.get(i);
            CartaInglesa carta = foundation.getUltimaCarta();

            if (carta != null) {
                CartaGrafica cartaGrafica = new CartaGrafica(carta);
                pane.getChildren().add(cartaGrafica.getImagenVista());
            }
        }
    }

    private void actualizarTableaux() {
        ArrayList<TableauDeck> tableaux = game.getTableau();

        for (int i = 0; i < tableaux.size(); i++) {
            Pane pane = tableauPanes.get(i);
            pane.getChildren().clear();

            TableauDeck tableau = tableaux.get(i);
            ArrayList<CartaInglesa> cartas = tableau.getCards();

            double contY = 0;
            for (int j = 0; j < cartas.size(); j++) {
                CartaInglesa carta = cartas.get(j);
                CartaGrafica cartaGrafica = new CartaGrafica(carta);

                cartaGrafica.getImagenVista().setLayoutX(0);
                cartaGrafica.getImagenVista().setLayoutY(contY);

                //boca arriba son click
                if (carta.isFaceup()) {
                    final int tableauIdx = i;
                    if (origenSeleccionado == OrigenCarta.TABLEAU &&
                            indiceSeleccionado == tableauIdx) {
                        CartaInglesa ultimaCarta = tableau.getUltimaCarta();
                        if (carta == ultimaCarta || (cartaSeleccionada != null && carta.getValor() <= cartaSeleccionada.getValor())) {
                            cartaGrafica.getImagenVista().setEffect(new javafx.scene.effect.DropShadow(25, Color.YELLOW));
                            System.out.println("entre a condicion");
                            tableauPanes.get(tableauIdx).getChildren().add(cartaGrafica.getImagenVista());
                        }
                    }
                    cartaGrafica.getImagenVista().setOnMouseClicked(e ->
                            {
                                manejarClickTableau(tableauIdx, carta);
                            }
                    );
                    cartaGrafica.getImagenVista().setStyle("-fx-cursor: hand;");
                }
                pane.getChildren().add(cartaGrafica.getImagenVista());
                contY += carta.isFaceup() ? 30 : 20;
            }
        }
    }
//seccion de clicks
    private void manejarClickDraw() {
        cancelarSeleccion();
        if (game.getDrawPile().hayCartas()) {
            game.drawCards();
        } else {
            game.reloadDrawPile();
        }
        actualizarVista();
    }

    private void manejarClickWaste() {
        if (origenSeleccionado == OrigenCarta.WASTE) {
            cancelarSeleccion();
        } else {
            cancelarSeleccion();
            origenSeleccionado = OrigenCarta.WASTE;
            cartaSeleccionada = game.getWastePile().verCarta();
        }
        actualizarVista();
    }

    private void manejarClickTableau(int tableauIdx, CartaInglesa carta) {
        System.out.println("CLICK DETECTADO EN TABLEU "+ tableauIdx);
        if (origenSeleccionado == null) {
            origenSeleccionado = OrigenCarta.TABLEAU;
            indiceSeleccionado = tableauIdx;
            cartaSeleccionada = carta;
            actualizarVista();
        } else {
            boolean exito = false;

            if (origenSeleccionado == OrigenCarta.WASTE) {
                exito = game.moveWasteToTableau(tableauIdx + 1);
            } else if (origenSeleccionado == OrigenCarta.TABLEAU) {
                exito = game.moveTableauToTableau(
                        indiceSeleccionado + 1, tableauIdx + 1);
            }
            cancelarSeleccion();
            actualizarVista();
        }
    }

    private void manejarClickTableauVacio(int tableauIdx) {
        if (origenSeleccionado == null) return;

        boolean exito = false;

        if (origenSeleccionado == OrigenCarta.WASTE) {
            exito = game.moveWasteToTableau(tableauIdx + 1);
        } else if (origenSeleccionado == OrigenCarta.TABLEAU) {
            exito = game.moveTableauToTableau(
                    indiceSeleccionado + 1, tableauIdx + 1);
        }
        cancelarSeleccion();
        actualizarVista();
    }

    private void manejarClickFoundation(int foundationIdx) {
        if (origenSeleccionado == null) return;

        boolean exito = false;

        if (origenSeleccionado == OrigenCarta.WASTE) {
            exito = game.moveWasteToFoundation();
        } else if (origenSeleccionado == OrigenCarta.TABLEAU) {
            exito = game.moveTableauToFoundation(indiceSeleccionado + 1);
        }
        cancelarSeleccion();
        actualizarVista();
    }

    private void cancelarSeleccion() {
        origenSeleccionado = null;
        cartaSeleccionada = null;
        indiceSeleccionado = -1;
    }

    private void reiniciarJuego() {
        cancelarSeleccion();
        game = new SolitaireGame();
        actualizarVista();
    }

    private void verificarVictoria() {
        if (game.isGameOver()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wuju congrats!");
            alert.setHeaderText("Has ganado:)!");
            alert.setContentText("¿Otra partida?");
            alert.showAndWait();
            reiniciarJuego();
        }
    }

    private enum OrigenCarta {
        WASTE, TABLEAU, FOUNDATION
    }

    public static void main(String[] args) {
        launch(args);
    }
}


