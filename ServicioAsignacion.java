package traslados.servicios;
import java.util.*;
import traslados.modelo.*;
public class ServicioAsignacion {
 private final List<Asignacion> asignaciones=new ArrayList<>();
 private final ServicioDisponibilidad disponibilidad=new ServicioDisponibilidad(asignaciones);
 public Asignacion asignarPrincipal(Recorrido r,Vehiculo v){
  if(buscar(r)!=null)throw new IllegalStateException("El recorrido ya tiene asignación");
  if(!disponibilidad.verificarDisponibilidad(v,r))throw new IllegalStateException("Vehículo principal no disponible");
  Asignacion a=new Asignacion(r,v);asignaciones.add(a);v.cambiarEstado(EstadoVehiculo.ASIGNADO);return a;
 }
 public void asignarRespaldo(Recorrido r,Vehiculo v){
  Asignacion a=buscar(r);if(a==null)throw new IllegalStateException("Primero asigne el principal");
  if(a.getRespaldo()!=null)throw new IllegalStateException("Ya existe respaldo");
  if(v==a.getPrincipal()||!disponibilidad.verificarDisponibilidad(v,r))throw new IllegalStateException("Respaldo inválido o no disponible");
  a.asignarRespaldo(v);
 }
 public void activarVehiculoRespaldo(Recorrido r){
  Asignacion a=buscar(r);if(a==null||a.getRespaldo()==null)throw new IllegalStateException("No hay asignación o respaldo");
  Vehiculo reemplazo=a.getRespaldo();
  if(!disponibilidad.verificarDisponibilidad(reemplazo,r))throw new IllegalStateException("El respaldo ya no está disponible");
  a.getPrincipal().cambiarEstado(EstadoVehiculo.EN_MANTENIMIENTO);
  Vehiculo anterior=a.activarRespaldo(); // Devuelve el principal anterior.
  reemplazo.cambiarEstado(EstadoVehiculo.ASIGNADO);
 }
 public List<Asignacion> consultarCronograma(){
  List<Asignacion> copia=new ArrayList<>(asignaciones);
  copia.sort(Comparator.comparing(a->a.getRecorrido().getInicio()));return copia;
 }
 public List<Asignacion> getAsignaciones(){return new ArrayList<>(asignaciones);}
 private Asignacion buscar(Recorrido r){for(Asignacion a:asignaciones)if(a.getRecorrido()==r)return a;return null;}
}
