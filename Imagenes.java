import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Imagenes {
    public static void main(String[] args) {
        // Ruta de la carpeta que contiene las imágenes originales
        String rutaCarpetaEntrada = "C://Users//LNV//Documents//Paralela//Parcial 1//PDI-concurrente-master//imagenes_color";

        // Ruta de la carpeta donde se guardarán las imágenes procesadas
        String rutaCarpetaSalida = "C://Users//LNV//Documents//Paralela//Parcial 1//PDI-concurrente-master//imagenes_grises";

        // Crear objeto File para la carpeta de entrada
        File carpetaEntrada = new File(rutaCarpetaEntrada);

        // Crear carpeta de salida si no existe
        File carpetaSalida = new File(rutaCarpetaSalida);
        if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs();
        }

        // Verificar si es una carpeta de entrada válida
        if (!carpetaEntrada.isDirectory()) {
            System.out.println("La ruta de entrada especificada no es una carpeta válida.");
            return;
        }

        // Obtener lista de archivos de imagen en la carpeta
        File[] archivos = carpetaEntrada.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg") ||
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".gif"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron imágenes en la carpeta de entrada.");
            return;
        }

        // Procesar cada imagen
        for (File archivo : archivos) {
            try {
                // Cargar la imagen
                BufferedImage imagen = ImageIO.read(archivo);

                int altura = imagen.getHeight();
                int ancho = imagen.getWidth();
                System.out.println("Procesando imagen: " + archivo.getName() + " - " + ancho + "x" + altura);

                // Crear y asignar hilos
                int numeroHilos = 3; // Dividir en 4 partes
                Thread[] hilos = new Thread[numeroHilos];
                int filasPorHilo = altura / numeroHilos;
                int finFila;

                long inicio = System.nanoTime(); // Registrar tiempo inicial

                for (int i = 0; i < numeroHilos; i++) {
                    int inicioFila = i * filasPorHilo;

                    if (i == numeroHilos - 1) {
                        finFila = altura;
                    } else {
                        finFila = inicioFila + filasPorHilo;
                    }
                    hilos[i] = new Thread(new FiltroGris(imagen, inicioFila, finFila));
                    hilos[i].start();
                }

                // Esperar a que todos los hilos terminen
                for (Thread hilo : hilos) {
                    hilo.join();
                }

                // Crear nombre de archivo de salida
                String nombreArchivo = archivo.getName();
                String nombreSinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
                String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1);

                // Guardar la nueva imagen en la carpeta de salida
                File archivoSalida = new File(carpetaSalida, nombreSinExtension + "_gris." + extension);
                ImageIO.write(imagen, extension, archivoSalida);

                long fin = System.nanoTime(); // Registrar tiempo final
                System.out.println("Imagen procesada y guardada como '" + archivoSalida.getName() + "'");
                System.out.println("Tiempo de ejecución: " + (fin - inicio) / 1_000_000 + " ms");

            } catch (IOException | InterruptedException e) {
                System.out.println("Error procesando " + archivo.getName() + ": " + e.getMessage());
            }
        }
    }
}