package dominio;

public enum EstadoRecorrido {

    PLANIFICADO("Planificado"),
    EN_CURSO("En curso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    /** Guarda el nombre visible del estado. */
    EstadoRecorrido(String descripcion) {
        this.descripcion = descripcion;
    }

    /** Devuelve la descripción del estado. */
    public String getDescripcion() {
        return descripcion;
    }

    /** Indica si el estado es definitivo. */
    public boolean esTerminal() {
        return this == FINALIZADO || this == CANCELADO;
    }

    /** Indica si se permite pasar al estado indicado. */
    public boolean puedePasarA(EstadoRecorrido destino) {
        switch (this) {
            case PLANIFICADO:
                return destino == EN_CURSO || destino == CANCELADO;
            case EN_CURSO:
                return destino == FINALIZADO || destino == CANCELADO;
            default:
                return false;
        }
    }
}
