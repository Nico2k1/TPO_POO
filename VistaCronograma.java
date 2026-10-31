package traslados.vistas;
import java.time.format.DateTimeFormatter;
import java.util.List;
import traslados.modelo.*;
public class VistaCronograma {
 private static final DateTimeFormatter FECHA=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
 public void mostrar(List<Asignacion> lista){
  System.out.println("\\n===== CRONOGRAMA DE TRASLADOS =====");
  if(lista==null||lista.isEmpty()){System.out.println("No hay traslados registrados.");return;}
  for(Asignacion a:lista){Recorrido r=a.getRecorrido();
   System.out.println("-----------------------------------");
   System.out.println("Recorrido: "+r.getId()+" | "+r.getOrigen()+" -> "+r.getDestino());
   System.out.println("Horario: "+r.getInicio().format(FECHA)+" a "+r.getFin().format(DateTimeFormatter.ofPattern("HH:mm")));
   System.out.println("Estado: "+r.getEstado());
   System.out.println("Principal: "+a.getPrincipal());
   System.out.println("Respaldo: "+(a.getRespaldo()==null?"No asignado":a.getRespaldo()));
  }
 }
}
