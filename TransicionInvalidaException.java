package excepciones;

public class TransicionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Crea una excepción con el mensaje indicado. */
    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
