package mx.limpieza.modelo.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Prepara la imagen de evidencia de una actividad antes de guardarla en la
 * base de datos. El servidor MySQL de XAMPP solo acepta paquetes de 1 MB
 * (max_allowed_packet), y una foto de celular suele pesar varios MB, así que
 * la imagen se reduce y se convierte a JPEG hasta que pese 400 KB o menos.
 */
public final class ImagenUtil {

    public static final int LADO_MAXIMO = 1280;
    public static final int TAMANO_MAXIMO_BYTES = 400 * 1024;

    private static final String MENSAJE_NO_ES_IMAGEN = "El archivo seleccionado no es una imagen válida.";

    // Calidades JPEG que se prueban, de mejor a peor (de 0.85 a 0.35)
    private static final float[] CALIDADES = {0.85f, 0.75f, 0.65f, 0.55f, 0.45f, 0.35f};

    // Si ni con la calidad más baja cabe, se achica al 75% (como máximo 10 veces)
    private static final double FACTOR_REDUCCION = 0.75;
    private static final int MAXIMO_REDUCCIONES = 10;

    private ImagenUtil() {
    }

    /**
     * Lee la imagen del archivo y devuelve los bytes del JPEG reducido, listo
     * para guardarse en la columna evidencia.
     */
    public static byte[] prepararEvidencia(File archivo) throws IOException {
        BufferedImage original = leerImagen(archivo);

        // El lado mayor queda en LADO_MAXIMO como máximo; las imágenes chicas no se agrandan
        int ladoMayor = Math.max(original.getWidth(), original.getHeight());
        double escala = Math.min(1.0, (double) LADO_MAXIMO / ladoMayor);

        for (int reduccion = 0; reduccion <= MAXIMO_REDUCCIONES; reduccion++) {
            BufferedImage imagen = redimensionar(original, escala);
            for (float calidad : CALIDADES) {
                byte[] jpeg = convertirAJpeg(imagen, calidad);
                if (jpeg.length <= TAMANO_MAXIMO_BYTES) {
                    return jpeg;
                }
            }
            escala = escala * FACTOR_REDUCCION;
        }
        throw new IOException("No se pudo reducir la imagen al tamaño permitido.");
    }

    private static BufferedImage leerImagen(File archivo) throws IOException {
        if (archivo == null || !archivo.isFile()) {
            throw new IOException("No se encontró el archivo seleccionado.");
        }
        if (!archivo.canRead()) {
            throw new IOException("No se puede leer el archivo seleccionado.");
        }
        BufferedImage imagen;
        try {
            imagen = ImageIO.read(archivo);
        } catch (IOException e) {
            // El archivo parece imagen pero está dañado o no se puede decodificar
            throw new IOException(MENSAJE_NO_ES_IMAGEN, e);
        }
        // ImageIO devuelve null cuando el archivo no es de ningún formato de imagen
        if (imagen == null) {
            throw new IOException(MENSAJE_NO_ES_IMAGEN);
        }
        return imagen;
    }

    /**
     * Dibuja la imagen con el nuevo tamaño sobre un fondo blanco. JPEG no
     * tiene transparencia: sin el fondo blanco, las partes transparentes de
     * un PNG quedarían negras.
     */
    private static BufferedImage redimensionar(BufferedImage original, double escala) {
        int ancho = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int alto = Math.max(1, (int) Math.round(original.getHeight() * escala));

        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultado.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ancho, alto);
        g.drawImage(original, 0, 0, ancho, alto, null);
        g.dispose();
        return resultado;
    }

    private static byte[] convertirAJpeg(BufferedImage imagen, float calidad) throws IOException {
        ImageWriter escritor = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam parametros = escritor.getDefaultWriteParam();
        parametros.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        parametros.setCompressionQuality(calidad);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ImageOutputStream salida = new MemoryCacheImageOutputStream(bytes)) {
            escritor.setOutput(salida);
            escritor.write(null, new IIOImage(imagen, null, null), parametros);
        } finally {
            escritor.dispose();
        }
        return bytes.toByteArray();
    }
}
