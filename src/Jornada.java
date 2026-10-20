import java.util.ArrayList;
import java.util.List;

public class Jornada {
    private int idJornada;
    private Cuadrilla cuadrilla;
    private List<Orden> ordenes;
    private List<Orden> ordenesPendientesDeResultado;
    private EstadoJornada estado;

    public Jornada(int idJornada, Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException("La cuadrilla no puede ser nula.");
        }
        this.idJornada = idJornada;
        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<>();
        this.ordenesPendientesDeResultado = new ArrayList<>();
        this.estado = EstadoJornada.EN_PREPARACION;
    }

    public int getIdJornada() {
        return idJornada;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public List<Orden> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public float getHorasAsignadas() {
        float suma = 0.0f;
        for (Orden o : ordenes) {
            suma += o.getHorasEstimadasTrabajo();
        }
        return suma;
    }

    public float getCapacidadDisponible() {
        return cuadrilla.getCapacidadHorasPorJornada() - getHorasAsignadas();
    }

    public void asignarOrden(Orden o) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden asignar ordenes a una jornada en preparacion.");
        }
        if (o == null) {
            throw new IllegalArgumentException("La orden no existe.");
        }
        if (!o.puedeAsignarse()) {
            throw new IllegalArgumentException("La orden no esta disponible para asignarse (debe estar PENDIENTE y no asignada a otra jornada).");
        }
        if (!o.esCompatibleConCuadrilla(cuadrilla)) {
            throw new IllegalArgumentException("La orden requiere una cuadrilla certificada en refrigeracion.");
        }
        if (getHorasAsignadas() + o.getHorasEstimadasTrabajo() > cuadrilla.getCapacidadHorasPorJornada() + 0.0001f) {
            throw new IllegalArgumentException("La orden excede la capacidad de horas disponibles de la cuadrilla.");
        }
        ordenes.add(o);
        o.setAsignadaAJornada(true);
    }

    public void retirarOrden(String idOrden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden retirar ordenes mientras la jornada esta en preparacion.");
        }
        Orden encontrada = null;
        for (Orden o : ordenes) {
            if (o.getId().equalsIgnoreCase(idOrden)) {
                encontrada = o;
                break;
            }
        }
        if (encontrada == null) {
            throw new IllegalArgumentException("La orden indicada no pertenece a esta jornada.");
        }
        ordenes.remove(encontrada);
        encontrada.setAsignadaAJornada(false);
    }

    public List<Orden> iniciarJornada() {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("La jornada ya fue iniciada o cerrada.");
        }
        if (ordenes.isEmpty()) {
            throw new IllegalStateException("No se puede iniciar una jornada vacia.");
        }
        estado = EstadoJornada.EN_CURSO;
        ordenesPendientesDeResultado.clear();
        ordenesPendientesDeResultado.addAll(ordenes);
        return new ArrayList<>(ordenes);
    }

    public void registrarResultadoOrden(String idOrden, boolean completada) {
        if (estado != EstadoJornada.EN_CURSO) {
            throw new IllegalStateException("Solo se pueden registrar resultados en una jornada en curso.");
        }
        Orden encontrada = null;
        for (Orden o : ordenesPendientesDeResultado) {
            if (o.getId().equalsIgnoreCase(idOrden)) {
                encontrada = o;
                break;
            }
        }
        if (encontrada == null) {
            throw new IllegalArgumentException("La orden no pertenece a esta jornada o ya se registro su resultado.");
        }
        encontrada.registrarResultadoVisita(completada);
        ordenesPendientesDeResultado.remove(encontrada);
        if (ordenesPendientesDeResultado.isEmpty()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    @Override
    public String toString() {
        return String.format("Jornada #%d | Estado: %s | Cuadrilla: %s | Horas Asignadas: %.2f/%.2f | Disponible: %.2f hrs | Ordenes: %d",
                idJornada, estado, cuadrilla.getId(), getHorasAsignadas(), cuadrilla.getCapacidadHorasPorJornada(), getCapacidadDisponible(), ordenes.size());
    }
}