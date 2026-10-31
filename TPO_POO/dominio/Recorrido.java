package dominio;

import excepciones.TransicionInvalidaException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Recorrido {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int id;
    private String origen;
    private String destino;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private EstadoRecorrido estado;
    private boolean eliminado;   // pasa a true cuando se elimina el recorrido

    /** Crea un recorrido validando sus datos iniciales. */
    public Recorrido(int id, String origen, String destino, LocalDateTime inicio, LocalDateTime fin) {
        validarTexto(origen, "origen");
        validarTexto(destino, "destino");
        validarHorario(inicio, fin);
        this.id = id;
        this.origen = origen.trim();
        this.destino = destino.trim();
        this.inicio = inicio;
        this.fin = fin;
        this.estado = EstadoRecorrido.PLANIFICADO;
    }

    /** Inicia el recorrido. */
    public void iniciar() {
        cambiarEstado(EstadoRecorrido.EN_CURSO);
    }

    /** Finaliza el recorrido. */
    public void finalizar() {
        cambiarEstado(EstadoRecorrido.FINALIZADO);
    }

    /** Cancela el recorrido. */
    public void cancelar() {
        cambiarEstado(EstadoRecorrido.CANCELADO);
    }

    /** Cambia el estado si la transición está permitida. */
    private void cambiarEstado(EstadoRecorrido nuevo) {
        // un recorrido eliminado ya no se puede tocar
        if (eliminado) {
            throw new TransicionInvalidaException("No se puede cambiar el estado de un recorrido eliminado.");
        }
        if (!estado.puedePasarA(nuevo)) {
            throw new TransicionInvalidaException("No se puede pasar un recorrido de "
                    + estado.getDescripcion() + " a " + nuevo.getDescripcion() + ".");
        }
        estado = nuevo;
    }

    /** Indica si el recorrido todavía está activo. */
    public boolean estaActivo() {
        return !eliminado && !estado.esTerminal();
    }

    /** Indica si los horarios de dos recorridos se superponen. */
    public boolean seSuperponeCon(Recorrido otro) {
        Objects.requireNonNull(otro, "El otro recorrido no puede ser null.");
        return inicio.isBefore(otro.fin) && otro.inicio.isBefore(fin);
    }

    /** Cambia el origen del recorrido. */
    public void setOrigen(String origen) {
        verificarModificable();
        validarTexto(origen, "origen");
        this.origen = origen.trim();
    }

    /** Cambia el destino del recorrido. */
    public void setDestino(String destino) {
        verificarModificable();
        validarTexto(destino, "destino");
        this.destino = destino.trim();
    }

    /** Cambia el horario completo del recorrido. */
    public void reprogramar(LocalDateTime nuevoInicio, LocalDateTime nuevoFin) {
        verificarModificable();
        validarHorario(nuevoInicio, nuevoFin);
        this.inicio = nuevoInicio;
        this.fin = nuevoFin;
    }

    /** Cambia origen, destino y horario juntos. Si algún dato está mal, no cambia nada. */
    public void modificar(String nuevoOrigen, String nuevoDestino,
                          LocalDateTime nuevoInicio, LocalDateTime nuevoFin) {
        verificarModificable();
        // primero se revisan todos los datos
        validarTexto(nuevoOrigen, "origen");
        validarTexto(nuevoDestino, "destino");
        validarHorario(nuevoInicio, nuevoFin);
        // si llegó hasta acá, todo está bien y recién ahora se cambian los datos
        this.origen = nuevoOrigen.trim();
        this.destino = nuevoDestino.trim();
        this.inicio = nuevoInicio;
        this.fin = nuevoFin;
    }

    /** Elimina el recorrido. Solo se puede si está planificado o cancelado. */
    public void eliminar() {
        if (eliminado) {
            throw new IllegalStateException("El recorrido ya está eliminado.");
        }
        // uno en curso o finalizado no se puede eliminar
        if (estado == EstadoRecorrido.EN_CURSO || estado == EstadoRecorrido.FINALIZADO) {
            throw new IllegalStateException("No se puede eliminar un recorrido "
                    + estado.getDescripcion().toLowerCase() + ".");
        }
        eliminado = true;
    }

    /** Indica si el recorrido fue eliminado. */
    public boolean estaEliminado() {
        return eliminado;
    }

    /** Verifica que el recorrido pueda modificarse. */
    private void verificarModificable() {
        if (eliminado) {
            throw new IllegalStateException("No se puede modificar un recorrido eliminado.");
        }
        if (estado.esTerminal()) {
            throw new IllegalStateException("No se puede modificar un recorrido "
                    + estado.getDescripcion().toLowerCase() + ".");
        }
    }

    /** Verifica que un texto sea válido. */
    private static void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + campo + " no puede estar vacio.");
        }
    }

    /** Verifica que el horario sea válido. */
    private static void validarHorario(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("El inicio y el fin son obligatorios.");
        }
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        }
    }

    /** Devuelve el identificador del recorrido. */
    public int getId() {
        return id;
    }

    /** Devuelve el origen del recorrido. */
    public String getOrigen() {
        return origen;
    }

    /** Devuelve el destino del recorrido. */
    public String getDestino() {
        return destino;
    }

    /** Devuelve la fecha y hora de inicio. */
    public LocalDateTime getInicio() {
        return inicio;
    }

    /** Devuelve la fecha y hora de finalización. */
    public LocalDateTime getFin() {
        return fin;
    }

    /** Devuelve el estado actual. */
    public EstadoRecorrido getEstado() {
        return estado;
    }

    /** Compara recorridos usando su identificador. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recorrido)) return false;
        return id == ((Recorrido) o).id;
    }

    /** Genera un código hash usando el identificador. */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /** Genera una descripción del recorrido. */
    @Override
    public String toString() {
        return "Recorrido " + id + ": " + origen + " -> " + destino
                + " (" + inicio.format(FORMATO) + " a " + fin.format(FORMATO) + ") ["
                + (eliminado ? "Eliminado" : estado.getDescripcion()) + "]";
    }
}
