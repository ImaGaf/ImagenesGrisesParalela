package com.mycompany.imagenessinhilos;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author andrespillajo
 */
public class ImagenesSinHilos {

    public static void main(String[] args) {
        try {
            // Directorios de entrada y salida
            File carpetaEntrada = new File("/Users/mateo/Desktop/paralela/PDI-secuencial-main/imagenes_color"); // Cambia esta ruta por la de tu carpeta de entrada
            File carpetaSalida = new File("/Users/mateo/Desktop/paralela/PDI-secuencial-main/imagenes_gris");   // Cambia esta ruta por la de tu carpeta de salida

            // Crear la carpeta de salida si no existe
            if (!carpetaSalida.exists()) {
                carpetaSalida.mkdirs();
            }

            // Verificar que la carpeta de entrada existe y es un directorio
            if (!carpetaEntrada.exists() || !carpetaEntrada.isDirectory()) {
                System.out.println("La carpeta de entrada no existe o no es un directorio.");
                return;
            }

            // Procesar cada archivo en la carpeta de entrada
            File[] archivos = carpetaEntrada.listFiles((dir, name) -> {
                // Filtrar solo imágenes con extensiones comunes
                return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg");
            });

            if (archivos == null || archivos.length == 0) {
                System.out.println("No se encontraron imágenes en la carpeta de entrada.");
                return;
            }

            for (File archivoEntrada : archivos) {
                // Cargar la imagen
                BufferedImage imagen = ImageIO.read(archivoEntrada);

                if (imagen == null) {
                    System.out.println("No se pudo cargar la imagen: " + archivoEntrada.getName());
                    continue;
                }

                // Obtener dimensiones de la imagen
                int ancho = imagen.getWidth();
                int alto = imagen.getHeight();

                System.out.println("Procesando imagen: " + archivoEntrada.getName() + " (" + ancho + "x" + alto + " píxeles)");

                long inicio = System.nanoTime(); // Registrar tiempo inicial

                // Recorrer cada píxel de la imagen
                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {
                        // Obtener el valor ARGB del píxel
                        int pixel = imagen.getRGB(x, y);

                        // Extraer componentes de color
                        int alpha = (pixel >> 24) & 0xff; // Componente Alpha
                        int red = (pixel >> 16) & 0xff;   // Componente Rojo
                        int green = (pixel >> 8) & 0xff;  // Componente Verde
                        int blue = pixel & 0xff;          // Componente Azul

                        // Calcular el promedio para escala de grises
                        int gris = (red + green + blue) / 3;

                        // Crear el nuevo color en escala de grises
                        int nuevoPixel = (alpha << 24) | (gris << 16) | (gris << 8) | gris;

                        // Asignar el nuevo color al píxel
                        imagen.setRGB(x, y, nuevoPixel);
                    }
                }

                long fin = System.nanoTime(); // Registrar tiempo final

                // Guardar la imagen resultante en la carpeta de salida
                File archivoSalida = new File(carpetaSalida, archivoEntrada.getName());
                ImageIO.write(imagen, "png", archivoSalida);

                System.out.println("Imagen convertida y guardada como: " + archivoSalida.getPath());
                System.out.println("Tiempo de procesamiento: " + (fin - inicio) / 1_000_000 + " ms");
            }

            System.out.println("Procesamiento completado. Las imágenes procesadas están en: " + carpetaSalida.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
