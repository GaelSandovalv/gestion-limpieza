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
 * Por cada cuadrilla dibuja dos barras: sus actividades terminadas y sus
 * actividades pendientes. La barra más alta corresponde al valor máximo y
 * las demás se dibujan en proporción a él.
 */
public class GraficaBarras extends JPanel {

    private static final String TITULO = "Actividades por cuadrilla";
    private static final String SIN_DATOS = "Sin datos para mostrar";
    private static final String TEXTO_TERMINADAS = "Terminadas";
    private static final String TEXTO_PENDIENTES = "Pendientes";

    private static final Color COLOR_TERMINADAS = new Color(46, 139, 87);
    private static final Color COLOR_PENDIENTES = new Color(230, 126, 34);
    private static final Color COLOR_TEXTO = new Color(50, 50, 50);
    private static final Color COLOR_EJE = Color.GRAY;

    private static final int MARGEN = 12;
    // Con pocas cuadrillas, cada barra mide como máximo esto para no verse demasiado gruesa
    private static final int ANCHO_MAXIMO_BARRA = 40;
    // Espacio entre las dos barras de una misma cuadrilla
    private static final int SEPARACION_BARRAS = 4;
    // Espacio libre entre etiquetas vecinas
    private static final int SEPARACION_ETIQUETAS = 6;
    private static final int LADO_CUADRO_LEYENDA = 10;

    private List<String> etiquetas = new ArrayList<>();
    private List<Integer> terminadas = new ArrayList<>();
    private List<Integer> pendientes = new ArrayList<>();

    public GraficaBarras() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    /** Cambia los datos de la gráfica (un valor de cada lista por cuadrilla) y la vuelve a dibujar. */
    public void setDatos(List<String> etiquetas, List<Integer> terminadas, List<Integer> pendientes) {
        this.etiquetas = new ArrayList<>(etiquetas);
        this.terminadas = new ArrayList<>(terminadas);
        this.pendientes = new ArrayList<>(pendientes);
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

        if (etiquetas.isEmpty()) {
            g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            dibujarCentrado(g2, SIN_DATOS, centroX, arriba + alto / 2);
            g2.dispose();
            return;
        }

        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        FontMetrics metricas = g2.getFontMetrics();
        int altoTexto = metricas.getHeight();

        // La leyenda va debajo del título
        int arribaLeyenda = arriba + metricasTitulo.getHeight() + 4;
        dibujarLeyenda(g2, centroX, arribaLeyenda);

        // Cada cuadrilla ocupa una "ranura" del mismo ancho; sus dos barras van centradas en ella
        int numCuadrillas = etiquetas.size();
        int ranura = ancho / numCuadrillas;
        int anchoBarra = Math.max(2, Math.min(ANCHO_MAXIMO_BARRA, (ranura * 6 / 10 - SEPARACION_BARRAS) / 2));

        // La línea base queda arriba de las etiquetas, y la barra más alta
        // deja lugar para escribir su valor encima
        int lineaBase = arriba + alto - altoTexto - 4;
        int inicioBarras = arribaLeyenda + 2 * altoTexto + 6;
        int altoMaximo = lineaBase - inicioBarras;
        int maximo = Math.max(1, Math.max(Collections.max(terminadas), Collections.max(pendientes)));

        g2.setColor(COLOR_EJE);
        g2.drawLine(izquierda, lineaBase, izquierda + ancho, lineaBase);

        for (int i = 0; i < numCuadrillas; i++) {
            int centro = izquierda + ranura * i + ranura / 2;
            // Terminadas a la izquierda del centro de la ranura y pendientes a la derecha
            dibujarBarra(g2, centro - SEPARACION_BARRAS / 2 - anchoBarra, anchoBarra, terminadas.get(i),
                    COLOR_TERMINADAS, lineaBase, altoMaximo, maximo);
            dibujarBarra(g2, centro + SEPARACION_BARRAS / 2, anchoBarra, pendientes.get(i),
                    COLOR_PENDIENTES, lineaBase, altoMaximo, maximo);

            g2.setColor(COLOR_TEXTO);
            String etiqueta = recortar(etiquetas.get(i), ranura - SEPARACION_ETIQUETAS, metricas);
            dibujarCentrado(g2, etiqueta, centro, lineaBase + 4 + metricas.getAscent());
        }
        g2.dispose();
    }

    /** Dibuja una barra con su valor encima. Escala: el valor máximo usa toda la altura disponible. */
    private static void dibujarBarra(Graphics2D g2, int x, int anchoBarra, int valor, Color color,
                                     int lineaBase, int altoMaximo, int maximo) {
        int altoBarra = valor * altoMaximo / maximo;
        g2.setColor(color);
        g2.fillRect(x, lineaBase - altoBarra, anchoBarra, altoBarra);
        g2.setColor(COLOR_TEXTO);
        dibujarCentrado(g2, String.valueOf(valor), x + anchoBarra / 2, lineaBase - altoBarra - 4);
    }

    /** Dibuja la leyenda centrada en x: un cuadro de color y su texto para cada tipo de barra. */
    private static void dibujarLeyenda(Graphics2D g2, int x, int arriba) {
        FontMetrics metricas = g2.getFontMetrics();
        int espacio = 6;
        int anchoTotal = 2 * (LADO_CUADRO_LEYENDA + espacio) + metricas.stringWidth(TEXTO_TERMINADAS)
                + 3 * espacio + metricas.stringWidth(TEXTO_PENDIENTES);
        int inicio = x - anchoTotal / 2;
        int arribaCuadro = arriba + (metricas.getHeight() - LADO_CUADRO_LEYENDA) / 2;
        int lineaTexto = arriba + metricas.getAscent();

        g2.setColor(COLOR_TERMINADAS);
        g2.fillRect(inicio, arribaCuadro, LADO_CUADRO_LEYENDA, LADO_CUADRO_LEYENDA);
        g2.setColor(COLOR_TEXTO);
        int textoX = inicio + LADO_CUADRO_LEYENDA + espacio;
        g2.drawString(TEXTO_TERMINADAS, textoX, lineaTexto);

        int segundo = textoX + metricas.stringWidth(TEXTO_TERMINADAS) + 3 * espacio;
        g2.setColor(COLOR_PENDIENTES);
        g2.fillRect(segundo, arribaCuadro, LADO_CUADRO_LEYENDA, LADO_CUADRO_LEYENDA);
        g2.setColor(COLOR_TEXTO);
        g2.drawString(TEXTO_PENDIENTES, segundo + LADO_CUADRO_LEYENDA + espacio, lineaTexto);
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
