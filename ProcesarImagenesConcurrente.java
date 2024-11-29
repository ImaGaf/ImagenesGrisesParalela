import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ProcesarImagenesConcurrente {

    public static void main(String[] args) {
        try {
            // Directorios de entrada y salida
            File carpetaEntrada = new File(
                    "C://Users//oswal//OneDrive//Escritorio//Code//Materias2024//Paralela//lab//paralela");
            File carpetaSalida = new File(
                    "C://Users//oswal//OneDrive//Escritorio//Code//Materias2024//Paralela//lab//paralela_grises");

            // Crear la carpeta de salida si no existe
            if (!carpetaSalida.exists()) {
                carpetaSalida.mkdirs();
            }

            // Verificar que la carpeta de entrada existe y es un directorio
            if (!carpetaEntrada.exists() || !carpetaEntrada.isDirectory()) {
                System.out.println("La carpeta de entrada no existe o no es un directorio.");
                return;
            }

            // Seleccionar las imágenes a procesar
            File[] archivos = carpetaEntrada.listFiles((dir, name) -> {
                return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".jpeg");
            });

            if (archivos == null || archivos.length == 0) {
                System.out.println("No se encontraron imágenes en la carpeta de entrada.");
                return;
            }

            System.out.println("Procesando " + archivos.length + " imágenes de forma concurrente...");
            long inicio = System.nanoTime(); // Tiempo inicial

            // Crear un hilo para cada imagen
            Thread[] hilos = new Thread[archivos.length];
            for (int i = 0; i < archivos.length; i++) {
                File archivoEntrada = archivos[i];
                hilos[i] = new Thread(() -> procesarImagen(archivoEntrada, carpetaSalida));
                hilos[i].start();
            }

            // Esperar a que todos los hilos terminen
            for (Thread hilo : hilos) {
                hilo.join();
            }

            long fin = System.nanoTime(); // Tiempo final
            System.out.println("Todas las imágenes procesadas en: " + (fin - inicio) / 1_000_000 + " ms");
            System.out.println("Las imágenes procesadas están en: " + carpetaSalida.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void procesarImagen(File archivoEntrada, File carpetaSalida) {
        try {
            // Leer la imagen
            BufferedImage imagen = ImageIO.read(archivoEntrada);
            if (imagen == null) {
                System.out.println("No se pudo cargar la imagen: " + archivoEntrada.getName());
                return;
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();
            System.out.println(
                    "Procesando imagen: " + archivoEntrada.getName() + " (" + ancho + "x" + alto + " píxeles)");

            // Convertir la imagen a escala de grises
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = imagen.getRGB(x, y);

                    int alpha = (pixel >> 24) & 0xff;
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;

                    int gris = (red + green + blue) / 3;
                    int nuevoPixel = (alpha << 24) | (gris << 16) | (gris << 8) | gris;

                    imagen.setRGB(x, y, nuevoPixel);
                }
            }

            // Guardar la imagen procesada
            File archivoSalida = new File(carpetaSalida, archivoEntrada.getName());
            ImageIO.write(imagen, "png", archivoSalida);

            System.out.println("Imagen convertida y guardada como: " + archivoSalida.getPath());
        } catch (Exception e) {
            System.out.println("Error al procesar la imagen: " + archivoEntrada.getName());
            e.printStackTrace();
        }
    }
}
