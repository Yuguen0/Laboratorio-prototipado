import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Interfaz grafica que modela:
 *   Actor: Estudiante
 *   Caso de uso: Solicitar saludo  --«include»-->  Pedir datos
 *
 * Flujo:
 *   1. El estudiante hace clic en "Solicitar saludo".
 *   2. El sistema pide nombre, edad y hora (AM/PM).
 *   3. El estudiante ingresa esos datos.
 *   4. El sistema saluda por nombre y menciona la edad.
 *
 * Solo usa la biblioteca estandar de Java (Swing + AWT). No hay librerias externas.
 */
public class SaludoEstudiante extends JFrame {

    private static final String PANTALLA_INICIO = "inicio";
    private static final String PANTALLA_DATOS = "datos";
    private static final String PANTALLA_SALUDO = "saludo";

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelCentral = new JPanel(tarjetas);

    private final JTextField campoNombre = new JTextField(18);
    private final JTextField campoEdad = new JTextField(18);
    private final JRadioButton radioAm = new JRadioButton("AM", true);
    private final JRadioButton radioPm = new JRadioButton("PM");
    private final JLabel etiquetaSaludo = new JLabel("", SwingConstants.CENTER);

    public SaludoEstudiante() {
        super("Sistema de saludo al estudiante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Saludo al estudiante", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBorder(new EmptyBorder(16, 12, 8, 12));
        add(titulo, BorderLayout.NORTH);

        panelCentral.add(crearPantallaInicio(), PANTALLA_INICIO);
        panelCentral.add(crearPantallaDatos(), PANTALLA_DATOS);
        panelCentral.add(crearPantallaSaludo(), PANTALLA_SALUDO);
        add(panelCentral, BorderLayout.CENTER);

        tarjetas.show(panelCentral, PANTALLA_INICIO);
    }

    /** Pantalla inicial: el estudiante hace clic en Solicitar saludo. */
    private JPanel crearPantallaInicio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel ayuda = new JLabel(
                "<html><center>Haz clic en el boton para iniciar el caso de uso<br>"
                        + "<b>Solicitar saludo</b>.</center></html>",
                SwingConstants.CENTER);
        panel.add(ayuda, BorderLayout.CENTER);

        JButton botonSolicitar = new JButton("Solicitar saludo");
        botonSolicitar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                // Paso 1: clic en Solicitar saludo.
                solicitarSaludo();
            }
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.add(botonSolicitar);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Caso de uso principal. Incluye pedir datos (relacion «include»).
     * Primero muestra el formulario; el saludo ocurre despues de confirmar.
     */
    private void solicitarSaludo() {
        campoNombre.setText("");
        campoEdad.setText("");
        radioAm.setSelected(true);
        tarjetas.show(panelCentral, PANTALLA_DATOS);
        campoNombre.requestFocusInWindow();
    }

    /** Caso de uso incluido: Pedir datos (nombre, edad, AM/PM). */
    private JPanel crearPantallaDatos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel encabezado = new JLabel("Pedir datos", SwingConstants.CENTER);
        encabezado.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(encabezado, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        formulario.add(campoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        formulario.add(campoEdad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(new JLabel("Hora (AM/PM):"), gbc);

        ButtonGroup grupoHora = new ButtonGroup();
        grupoHora.add(radioAm);
        grupoHora.add(radioPm);
        JPanel panelHora = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelHora.add(radioAm);
        panelHora.add(radioPm);
        gbc.gridx = 1;
        formulario.add(panelHora, gbc);

        panel.add(formulario, BorderLayout.CENTER);

        JButton botonConfirmar = new JButton("Confirmar datos");
        botonConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                DatosEstudiante datos = leerDatosIngresados();
                if (datos != null) {
                    mostrarSaludo(datos);
                }
            }
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.add(botonConfirmar);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Valida lo que escribio el estudiante (paso 3) y arma los datos.
     * Devuelve null si hay error; en ese caso se muestra un aviso.
     */
    private DatosEstudiante leerDatosIngresados() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre no puede estar vacio.",
                    "Dato incompleto",
                    JOptionPane.WARNING_MESSAGE);
            campoNombre.requestFocusInWindow();
            return null;
        }

        int edad;
        try {
            edad = Integer.parseInt(campoEdad.getText().trim());
            if (edad < 0) {
                JOptionPane.showMessageDialog(this,
                        "La edad no puede ser negativa.",
                        "Dato invalido",
                        JOptionPane.WARNING_MESSAGE);
                campoEdad.requestFocusInWindow();
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Escribe la edad en numeros (ejemplo: 20).",
                    "Dato invalido",
                    JOptionPane.WARNING_MESSAGE);
            campoEdad.requestFocusInWindow();
            return null;
        }

        String horaAmPm = radioAm.isSelected() ? "AM" : "PM";
        return new DatosEstudiante(nombre, edad, horaAmPm);
    }

    /** Paso 4: saluda por nombre y menciona la edad. */
    private void mostrarSaludo(DatosEstudiante datos) {
        String momento = "AM".equals(datos.horaAmPm)
                ? "Buenos d\u00edas"
                : "Buenas tardes";
        etiquetaSaludo.setText("<html><center>" + momento + ", <b>" + datos.nombre
                + "</b>.<br>Tienes " + datos.edad + " a\u00f1os.</center></html>");
        tarjetas.show(panelCentral, PANTALLA_SALUDO);
    }

    private JPanel crearPantallaSaludo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        etiquetaSaludo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(etiquetaSaludo, BorderLayout.CENTER);

        JButton botonOtro = new JButton("Solicitar otro saludo");
        botonOtro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                solicitarSaludo();
            }
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.add(botonOtro);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Punto de entrada. SwingUtilities.invokeLater pone la ventana
     * en el hilo de eventos de Swing (EDT), que es el hilo correcto para la GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SaludoEstudiante().setVisible(true);
            }
        });
    }

    /** Contenedor de los datos del estudiante. */
    private static class DatosEstudiante {
        final String nombre;
        final int edad;
        final String horaAmPm;

        DatosEstudiante(String nombre, int edad, String horaAmPm) {
            this.nombre = nombre;
            this.edad = edad;
            this.horaAmPm = horaAmPm;
        }
    }
}
