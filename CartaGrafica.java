package com.example.demoxxx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CartaGrafica {
    private Carta cartaGrafica;
    private ImageView imagenVista;
    private static Image imagenReverso = null;

    public CartaGrafica(Carta carta) {
        this.cartaGrafica = carta;

        if (!carta.isFaceup()) {
            cargarReverso();
            return;
        }

        String inicial = switch (carta.getPalo()) {
            case CORAZON -> "c";
            case DIAMANTE -> "d";
            case TREBOL -> "t";
            case PICA -> "p";
        };

        String valorStr = switch (carta.getValor()) {
            case 14 -> "14";
            case 11 -> "j";
            case 12 -> "q";
            case 13 -> "k";
            default -> String.valueOf(carta.getValor());
        };

        String nombreArchivo = inicial + valorStr + ".png";

        Image imagen = new Image(getClass().getResourceAsStream("/cartas/" + nombreArchivo));
        this.imagenVista = new ImageView(imagen);
        imagenVista.setFitWidth(100);
        imagenVista.setFitHeight(150);
        imagenVista.setPreserveRatio(true);
    }

    private void cargarReverso() {
        if (imagenReverso == null) {
            imagenReverso = new Image(getClass().getResourceAsStream("/cartas/back2.png"));
        }
        this.imagenVista = new ImageView(imagenReverso);
        imagenVista.setFitWidth(100);
        imagenVista.setFitHeight(150);
        imagenVista.setPreserveRatio(true);
    }

    public ImageView getImagenVista() {
        return imagenVista;
    }
}