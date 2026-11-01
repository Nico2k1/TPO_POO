public class Asignacion {
    private Recorrido recorrido;
    private Vehiculo vehiculoPrincipal;
    private Vehiculo vehiculoRespaldo;

    public Asignacion(Recorrido recorrido) {
        if (recorrido == null) {
            throw new IllegalArgumentException("El recorrido no puede ser nulo.");
        }
        this.recorrido = recorrido;
    }

    public void asignarPrincipal(Vehiculo vehiculo) {
        if (vehiculo == null || !vehiculo.estaDisponible()) {
            throw new IllegalArgumentException("El vehículo principal debe estar disponible.");
        }
        this.vehiculoPrincipal = vehiculo;
        this.vehiculoPrincipal.cambiarEstado(EstadoVehiculo.ASIGNADO);
    }

    public void asignarRespaldo(Vehiculo vehiculo) {
        if (vehiculo == null || !vehiculo.estaDisponible()) {
            throw new IllegalArgumentException("El vehículo de respaldo debe estar disponible.");
        }
        this.vehiculoRespaldo = vehiculo;
    }

    public void activarRespaldo() {
        if (vehiculoRespaldo == null) {
            throw new IllegalStateException("No hay un vehículo de respaldo configurado.");
        }

        // El principal pasa a mantenimiento y el de respaldo pasa a asignado
        if (vehiculoPrincipal != null) {
            vehiculoPrincipal.enviarAMantenimiento();
        }
        vehiculoRespaldo.cambiarEstado(EstadoVehiculo.ASIGNADO);
    }

    public Recorrido getRecorrido() { return recorrido; }
    public Vehiculo getVehiculoPrincipal() { return vehiculoPrincipal; }
    public Vehiculo getVehiculoRespaldo() { return vehiculoRespaldo; }
}
