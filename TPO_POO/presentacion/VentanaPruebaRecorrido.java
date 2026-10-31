package presentacion;

import dominio.Recorrido;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ventana para probar la clase Recorrido.
 * No tiene reglas propias: cada botón llama a un método de Recorrido
 * y la ventana solo muestra si salió bien o qué error devolvió.
 */
public class VentanaPruebaRecorrido extends JFrame {

    private static final long serialVersionUID = 1L;

    // Formato en que se escriben las fechas: 2026-11-20 09:30
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Lista donde se guardan los recorridos
    private final List<Recorrido> recorridos = new ArrayList<>();

    // Número que se le da al próximo recorrido que se crea
    private int siguienteId = 1;

    // Cuadros de texto del formulario
    private final JTextField campoOrigen = new JTextField(16);
    private final JTextField campoDestino = new JTextField(16);
    private final JTextField campoInicio = new JTextField("2026-11-20 12:00", 16);
    private final JTextField campoFin = new JTextField("2026-11-20 13:00", 16);

    // Tabla (no se puede escribir adentro, solo mostrar)
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Id", "Origen", "Destino", "Inicio", "Fin", "Estado", "Activo"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    // Cuadro de abajo donde se escribe qué pasó con cada botón
    private final JTextArea registro = new JTextArea();

    public VentanaPruebaRecorrido() {
        super("Prueba de Recorrido");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Arriba el formulario, en el medio la tabla con los botones, abajo el registro
        add(armarFormulario(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        centro.add(armarBotones(), BorderLayout.SOUTH);

        registro.setEditable(false);
        registro.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane panelRegistro = new JScrollPane(registro);
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Qué pasó con cada botón"));

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centro, panelRegistro);
        divisor.setResizeWeight(0.62);
        add(divisor, BorderLayout.CENTER);

        configurarTabla();
        cargarEjemplos();

        setSize(1000, 660);
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------ armado de la ventana

    // Formulario con los datos del recorrido
    private JPanel armarFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                "Datos del recorrido (al elegir una fila de la tabla se cargan acá)"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        agregarCampo(panel, gc, 0, 0, "Origen", campoOrigen);
        agregarCampo(panel, gc, 0, 2, "Destino", campoDestino);
        agregarCampo(panel, gc, 1, 0, "Inicio (aaaa-MM-dd HH:mm)", campoInicio);
        agregarCampo(panel, gc, 1, 2, "Fin (aaaa-MM-dd HH:mm)", campoFin);

        JButton crear = new JButton("Crear recorrido");
        crear.addActionListener(e -> crear());
        JButton modificar = new JButton("Modificar el seleccionado");
        modificar.addActionListener(e -> modificar());
        JButton eliminar = new JButton("Eliminar el seleccionado");
        eliminar.addActionListener(e -> eliminar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.add(crear);
        botones.add(modificar);
        botones.add(eliminar);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 4;
        panel.add(botones, gc);
        return panel;
    }

    // Pone una etiqueta con su cuadro de texto al lado
    private void agregarCampo(JPanel panel, GridBagConstraints gc, int fila, int columna,
                              String etiqueta, JTextField campo) {
        gc.gridwidth = 1;
        gc.gridy = fila;
        gc.gridx = columna;
        panel.add(new JLabel(etiqueta), gc);
        gc.gridx = columna + 1;
        panel.add(campo, gc);
    }

    // Botones que están debajo de la tabla
    private JPanel armarBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton iniciar = new JButton("Iniciar");
        iniciar.addActionListener(e -> cambiarEstado("iniciar", Recorrido::iniciar));
        JButton finalizar = new JButton("Finalizar");
        finalizar.addActionListener(e -> cambiarEstado("finalizar", Recorrido::finalizar));
        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(e -> cambiarEstado("cancelar", Recorrido::cancelar));
        JButton superposicion = new JButton("¿Se superponen? (elegir 2 filas)");
        superposicion.addActionListener(e -> consultarSuperposicion());
        JButton restaurar = new JButton("Restaurar ejemplos");
        restaurar.addActionListener(e -> cargarEjemplos());

        panel.add(iniciar);
        panel.add(finalizar);
        panel.add(cancelar);
        panel.add(superposicion);
        panel.add(restaurar);
        return panel;
    }

    private void configurarTabla() {
        // Se pueden elegir varias filas a la vez (con Cmd o Ctrl + clic)
        tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        tabla.getColumnModel().getColumn(6).setMaxWidth(70);
        tabla.getColumnModel().getColumn(5).setCellRenderer(new ColorDeEstado());

        // Cuando se elige una sola fila, sus datos pasan al formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRowCount() == 1) {
                cargarFormulario(recorridos.get(tabla.getSelectedRow()));
            }
        });
    }

    // Le pone un color distinto a cada estado
    private static class ColorDeEstado extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            Component c = super.getTableCellRendererComponent(t, valor, seleccionada, foco, fila, columna);
            if (!seleccionada) {
                String texto = String.valueOf(valor);
                if (texto.equals("En curso")) {
                    c.setForeground(new Color(0xB36B00));
                } else if (texto.equals("Finalizado")) {
                    c.setForeground(new Color(0x1B7F3B));
                } else if (texto.equals("Cancelado")) {
                    c.setForeground(new Color(0xB3261E));
                } else if (texto.equals("Eliminado")) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.DARK_GRAY);
                }
            }
            return c;
        }
    }

    // ------------------------------------------------------------ lo que hace cada botón

    // Crea un recorrido con los datos del formulario
    private void crear() {
        try {
            Recorrido nuevo = new Recorrido(siguienteId, campoOrigen.getText(), campoDestino.getText(),
                    leerFecha(campoInicio, "inicio"), leerFecha(campoFin, "fin"));
            recorridos.add(nuevo);
            siguienteId++;
            refrescarTabla();
            informarOk("Se creó " + nuevo);
        } catch (RuntimeException ex) {
            informarRechazo(ex);
        }
    }

    // Cambia los datos del recorrido elegido usando el método modificar
    private void modificar() {
        if (tabla.getSelectedRowCount() != 1) {
            informarAviso("Para modificar, elegí una sola fila de la tabla.");
            return;
        }
        Recorrido r = recorridos.get(tabla.getSelectedRow());
        try {
            r.modificar(campoOrigen.getText(), campoDestino.getText(),
                    leerFecha(campoInicio, "inicio"), leerFecha(campoFin, "fin"));
            refrescarTabla();
            informarOk("Se modificó " + r);
        } catch (RuntimeException ex) {
            informarRechazo(ex);
        }
    }

    // Elimina los recorridos elegidos usando el método eliminar
    private void eliminar() {
        int[] filas = tabla.getSelectedRows();
        if (filas.length == 0) {
            informarAviso("Elegí al menos una fila de la tabla para eliminar.");
            return;
        }
        for (int fila : filas) {
            Recorrido r = recorridos.get(fila);
            try {
                r.eliminar();
                informarOk("Recorrido " + r.getId() + " eliminado. Sigue en la tabla, marcado como Eliminado, "
                        + "para poder probar que ya no se puede usar.");
            } catch (RuntimeException ex) {
                informarRechazo(ex);
            }
        }
        refrescarTabla();
    }

    // Inicia, finaliza o cancela los recorridos elegidos
    private void cambiarEstado(String nombre, Consumer<Recorrido> accion) {
        int[] filas = tabla.getSelectedRows();
        if (filas.length == 0) {
            informarAviso("Elegí al menos una fila de la tabla para " + nombre + ".");
            return;
        }
        for (int fila : filas) {
            Recorrido r = recorridos.get(fila);
            try {
                accion.accept(r);
                informarOk("Recorrido " + r.getId() + ": " + nombre + " permitido. Ahora está "
                        + r.getEstado().getDescripcion() + ".");
            } catch (RuntimeException ex) {
                informarRechazo(ex);
            }
        }
        refrescarTabla();
    }

    // Dice si dos recorridos elegidos se superponen
    private void consultarSuperposicion() {
        if (tabla.getSelectedRowCount() != 2) {
            informarAviso("Elegí exactamente 2 filas (con Cmd o Ctrl + clic) para compararlas.");
            return;
        }
        int[] filas = tabla.getSelectedRows();
        Recorrido a = recorridos.get(filas[0]);
        Recorrido b = recorridos.get(filas[1]);
        boolean seSuperponen = a.seSuperponeCon(b);
        informarOk("Recorridos " + a.getId() + " y " + b.getId() + ": "
                + (seSuperponen ? "SE SUPERPONEN" : "no se superponen")
                + " (solo compara los horarios).");
    }

    // Deja la tabla con 3 recorridos de ejemplo
    private void cargarEjemplos() {
        recorridos.clear();
        siguienteId = 1;
        recorridos.add(new Recorrido(siguienteId++, "Nunciatura", "Catedral",
                LocalDateTime.of(2026, 11, 20, 9, 0), LocalDateTime.of(2026, 11, 20, 10, 0)));
        recorridos.add(new Recorrido(siguienteId++, "Catedral", "Estadio",
                LocalDateTime.of(2026, 11, 20, 9, 30), LocalDateTime.of(2026, 11, 20, 10, 30)));
        recorridos.add(new Recorrido(siguienteId++, "Catedral", "Plaza",
                LocalDateTime.of(2026, 11, 20, 10, 0), LocalDateTime.of(2026, 11, 20, 11, 0)));
        refrescarTabla();
        registro.setText("");
        informarOk("Se cargaron 3 recorridos de ejemplo. El 1 y el 2 se superponen; el 3 empieza justo cuando termina el 1.");
    }

    // ------------------------------------------------------------ ayudas

    // Convierte el texto de un cuadro en una fecha. Si está mal escrito, avisa.
    private LocalDateTime leerFecha(JTextField campo, String nombre) {
        try {
            return LocalDateTime.parse(campo.getText().trim().replace('T', ' '), FORMATO);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("El " + nombre
                    + " debe escribirse así: aaaa-MM-dd HH:mm (por ejemplo 2026-11-20 09:30).");
        }
    }

    // Copia los datos de un recorrido a los cuadros del formulario
    private void cargarFormulario(Recorrido r) {
        campoOrigen.setText(r.getOrigen());
        campoDestino.setText(r.getDestino());
        campoInicio.setText(r.getInicio().format(FORMATO));
        campoFin.setText(r.getFin().format(FORMATO));
    }

    // Vuelve a dibujar la tabla con lo que hay en la lista
    private void refrescarTabla() {
        int[] seleccion = tabla.getSelectedRows();
        modeloTabla.setRowCount(0);
        for (Recorrido r : recorridos) {
            String estado = r.estaEliminado() ? "Eliminado" : r.getEstado().getDescripcion();
            modeloTabla.addRow(new Object[]{
                    r.getId(), r.getOrigen(), r.getDestino(),
                    r.getInicio().format(FORMATO), r.getFin().format(FORMATO),
                    estado, r.estaActivo() ? "Sí" : "No"});
        }
        // Se vuelven a elegir las filas que estaban elegidas
        for (int fila : seleccion) {
            if (fila < modeloTabla.getRowCount()) {
                tabla.addRowSelectionInterval(fila, fila);
            }
        }
    }

    // Escribe una línea en el cuadro de abajo
    private void escribir(String linea) {
        registro.append(linea + "\n");
        registro.setCaretPosition(registro.getDocument().getLength());
    }

    private void informarOk(String mensaje) {
        escribir("[OK]         " + mensaje);
    }

    private void informarRechazo(RuntimeException ex) {
        escribir("[RECHAZADO]  " + ex.getMessage());
    }

    private void informarAviso(String mensaje) {
        escribir("[AVISO]      " + mensaje);
    }

    // ------------------------------------------------------------ arranque

    // Abre la ventana
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // si no se puede cambiar el aspecto, se usa el de siempre
        }
        SwingUtilities.invokeLater(() -> new VentanaPruebaRecorrido().setVisible(true));
    }
}
