package mx.limpieza.vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Gráfica de barras verticales dibujada con Java2D, sin librerías externas.
 * Recibe una etiqueta y un valor por barra; la barra más alta corresponde
 * al valor máximo y las demás se dibujan en proporción a él.
 */
public class GraficaBarras extends JPanel {

    private static final String TITULO = "Actividades por cuadrilla";
    private static final String SIN_DATOS = "Sin datos para mostrar";

    private static final Color COLOR_BARRA = new Color(52, 120, 186);
    private static final Color COLOR_TEXTO = new Color(50, 50, 50);
    private static final Color COLOR_EJE = Color.GRAY;

    private static final int MARGEN = 12;
    // Con pocas barras, cada una mide como máximo esto para no verse demasiado gruesa
    private static final int ANCHO_MAXIMO_BARRA = 70;
    // Espacio libre entre etiquetas vecinas
    private static final int SEPARACION_ETIQUETAS = 6;

    private List<String> etiquetas = new ArrayList<>();
    private List<Integer> valores = new ArrayList<>();

    public GraficaBarras() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    /** Cambia los datos de la gráfica y la vuelve a dibujar. */
    public void setDatos(List<String> etiquetas, List<Integer> valores) {
        this.etiquetas = new ArrayList<>(etiquetas);
        this.valores = new ArrayList<>(valores);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Pinta el fondo blanco
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Antialiasing: suaviza los bordes de las figuras y del texto
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Área disponible para dibujar, sin el borde ni los márgenes
        Insets bordes = getInsets();
        int izquierda = bordes.left + MARGEN;
        int arriba = bordes.top + MARGEN;
        int ancho = getWidth() - bordes.left - bordes.right - 2 * MARGEN;
        int alto = getHeight() - bordes.top - bordes.bottom - 2 * MARGEN;
        int centroX = izquierda + ancho / 2;

        // Título centrado arriba
        g2.setColor(COLOR_TEXTO);
        g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics metricasTitulo = g2.getFontMetrics();
        dibujarCentrado(g2, TITULO, centroX, arriba + metricasTitulo.getAscent());

        if (valores.isEmpty()) {
            g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            dibujarCentrado(g2, SIN_DATOS, centroX, arriba + alto / 2);
            g2.dispose();
            return;
        }

        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        FontMetrics metricas = g2.getFontMetrics();
        int altoTexto = metricas.getHeight();

        // Cada barra ocupa una "ranura" del mismo ancho; la barra va centrada en ella
        int numBarras = valores.size();
        int ranura = ancho / numBarras;
        int anchoBarra = Math.min(ANCHO_MAXIMO_BARRA, ranura * 6 / 10);

        // La línea base queda arriba de las etiquetas, y la barra más alta
        // deja lugar para escribir su valor encima
        int lineaBase = arriba + alto - altoTexto - 4;
        int inicioBarras = arriba + metricasTitulo.getHeight() + altoTexto + 8;
        int altoMaximo = lineaBase - inicioBarras;
        int maximo = Math.max(1, Collections.max(valores));

        g2.setColor(COLOR_EJE);
        g2.drawLine(izquierda, lineaBase, izquierda + ancho, lineaBase);

        for (int i = 0; i < numBarras; i++) {
            int valor = valores.get(i);
            int centro = izquierda + ranura * i + ranura / 2;
            // Escala: el valor máximo usa toda la altura disponible
            int altoBarra = valor * altoMaximo / maximo;

            g2.setColor(COLOR_BARRA);
            g2.fillRect(centro - anchoBarra / 2, lineaBase - altoBarra, anchoBarra, altoBarra);

            g2.setColor(COLOR_TEXTO);
            dibujarCentrado(g2, String.valueOf(valor), centro, lineaBase - altoBarra - 4);
            String etiqueta = recortar(etiquetas.get(i), ranura - SEPARACION_ETIQUETAS, metricas);
            dibujarCentrado(g2, etiqueta, centro, lineaBase + 4 + metricas.getAscent());
        }
        g2.dispose();
    }

    /** Escribe el texto centrado horizontalmente en x; y es la línea base del texto. */
    private static void dibujarCentrado(Graphics2D g2, String texto, int x, int y) {
        int anchoTexto = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto, x - anchoTexto / 2, y);
    }

    /** Si el texto no cabe en el ancho indicado, le quita letras y le agrega "…". */
    private static String recortar(String texto, int anchoMaximo, FontMetrics metricas) {
        if (metricas.stringWidth(texto) <= anchoMaximo) {
            return texto;
        }
        String recortado = texto;
        while (!recortado.isEmpty() && metricas.stringWidth(recortado + "…") > anchoMaximo) {
            recortado = recortado.substring(0, recortado.length() - 1);
        }
        return recortado.strip() + "…";
    }
}
