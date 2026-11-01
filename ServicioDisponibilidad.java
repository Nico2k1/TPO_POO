import java.time.LocalDateTime;
import java.util.List;

public class Vehiculo {
    private String patente;
    private String modelo;
    private int capacidad;
    private EstadoVehiculo estado;

    private List<Recorrido> recorridosAsignados;

    public boolean estaDisponibleEn(LocalDateTime inicioConsulta, LocalDateTime finConsulta) {
        if (this.estado != EstadoVehiculo.DISPONIBLE) {
            return false;
        }


        Recorrido recorridoConsulta = new Recorrido(inicioConsulta, finConsulta);

    
        for (Recorrido recorridoExistente : recorridosAsignados) {
            if (recorridoExistente.seSuperponeCon(recorridoConsulta)) {
                return false; // Existe un conflicto de horario
            }
        }

        return true;
    }

    
}