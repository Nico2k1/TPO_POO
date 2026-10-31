import dominio.Recorrido;
import excepciones.TransicionInvalidaException;

import java.time.LocalDateTime;

public class PruebaRecorrido {

    /** Ejecuta pruebas básicas del recorrido. */
    public static void main(String[] args) {
        Recorrido r1 = new Recorrido(1, "Nunciatura", "Catedral",
                LocalDateTime.of(2026, 11, 20, 9, 0), LocalDateTime.of(2026, 11, 20, 10, 0));
        Recorrido r2 = new Recorrido(2, "Catedral", "Estadio",
                LocalDateTime.of(2026, 11, 20, 9, 30), LocalDateTime.of(2026, 11, 20, 10, 30));
        Recorrido r3 = new Recorrido(3, "Catedral", "Plaza",
                LocalDateTime.of(2026, 11, 20, 10, 0), LocalDateTime.of(2026, 11, 20, 11, 0));

        System.out.println(r1);
        System.out.println("r1 y r2 se superponen (esperado true): " + r1.seSuperponeCon(r2));
        System.out.println("r1 y r3 se superponen (esperado false): " + r1.seSuperponeCon(r3));

        r1.iniciar();
        r1.finalizar();
        System.out.println("Estado de r1 (esperado Finalizado): " + r1.getEstado().getDescripcion());
        System.out.println("r1 activo (esperado false): " + r1.estaActivo());

        try {
            r1.cancelar();
            System.out.println("ERROR: debio rechazar la cancelacion");
        } catch (TransicionInvalidaException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }

        try {
            r2.finalizar();
            System.out.println("ERROR: no se puede finalizar sin haber iniciado");
        } catch (TransicionInvalidaException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }

        try {
            new Recorrido(4, "A", "B",
                    LocalDateTime.of(2026, 11, 20, 12, 0), LocalDateTime.of(2026, 11, 20, 11, 0));
            System.out.println("ERROR: debio rechazar el horario");
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }

        try {
            r1.setDestino("Otro lugar");
            System.out.println("ERROR: no se debe modificar un recorrido finalizado");
        } catch (IllegalStateException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }
        // ---- Pruebas de modificar y eliminar ----
        Recorrido r5 = new Recorrido(5, "Plaza", "Aeropuerto",
                LocalDateTime.of(2026, 11, 20, 14, 0), LocalDateTime.of(2026, 11, 20, 15, 0));

        // modificar con datos correctos
        r5.modificar("Plaza Mayor", "Aeroparque",
                LocalDateTime.of(2026, 11, 20, 14, 30), LocalDateTime.of(2026, 11, 20, 15, 30));
        System.out.println("Despues de modificar: " + r5);

        // modificar con un dato malo: no debe cambiar nada
        try {
            r5.modificar("Otro origen", "",
                    LocalDateTime.of(2026, 11, 20, 14, 30), LocalDateTime.of(2026, 11, 20, 15, 30));
            System.out.println("ERROR: debio rechazar el destino vacio");
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
            System.out.println("El origen quedo igual (esperado Plaza Mayor): " + r5.getOrigen());
        }

        // eliminar un recorrido planificado
        r5.eliminar();
        System.out.println("Eliminado (esperado true): " + r5.estaEliminado());
        System.out.println("Activo (esperado false): " + r5.estaActivo());
        System.out.println(r5);

        // un recorrido eliminado no se puede eliminar de nuevo, ni modificar, ni iniciar
        try {
            r5.eliminar();
            System.out.println("ERROR: no se puede eliminar dos veces");
        } catch (IllegalStateException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }
        try {
            r5.modificar("A", "B",
                    LocalDateTime.of(2026, 11, 20, 14, 30), LocalDateTime.of(2026, 11, 20, 15, 30));
            System.out.println("ERROR: no se puede modificar uno eliminado");
        } catch (IllegalStateException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }
        try {
            r5.iniciar();
            System.out.println("ERROR: no se puede iniciar uno eliminado");
        } catch (TransicionInvalidaException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }

        // un recorrido en curso o finalizado no se puede eliminar
        Recorrido r6 = new Recorrido(6, "Catedral", "Plaza",
                LocalDateTime.of(2026, 11, 20, 16, 0), LocalDateTime.of(2026, 11, 20, 17, 0));
        r6.iniciar();
        try {
            r6.eliminar();
            System.out.println("ERROR: no se puede eliminar uno en curso");
        } catch (IllegalStateException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }
        r6.finalizar();
        try {
            r6.eliminar();
            System.out.println("ERROR: no se puede eliminar uno finalizado");
        } catch (IllegalStateException e) {
            System.out.println("Rechazado como corresponde: " + e.getMessage());
        }

        // un recorrido cancelado si se puede eliminar
        Recorrido r7 = new Recorrido(7, "Plaza", "Estadio",
                LocalDateTime.of(2026, 11, 20, 18, 0), LocalDateTime.of(2026, 11, 20, 19, 0));
        r7.cancelar();
        r7.eliminar();
        System.out.println("Cancelado y eliminado (esperado true): " + r7.estaEliminado());
    }
}
