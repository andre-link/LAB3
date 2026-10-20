public abstract class Orden implements Quotable {
    private String id;
    private String nombreCliente;
    private String direccion;
    private float horasEstimadasTrabajo;
    private ZonaTrabajo zona;
    private EstadoOrden estado;
    private int visitasRealizadas;
    private boolean asignadaAJornada;

    public Orden(String id, String nombreCliente, String direccion, float horasEstimadasTrabajo, ZonaTrabajo zona) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la orden no puede estar vacio.");
        }
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacio.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La direccion no puede estar vacia.");
        }
        if (horasEstimadasTrabajo <= 0) {
            throw new IllegalArgumentException("Las horas estimadas de trabajo deben ser mayores a 0.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("La zona de trabajo es obligatoria.");
        }
        this.id = id.trim();
        this.nombreCliente = nombreCliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadasTrabajo = horasEstimadasTrabajo;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
        this.asignadaAJornada = false;
    }

    public String getId() {
        return id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public float getHorasEstimadasTrabajo() {
        return horasEstimadasTrabajo;
    }

    public ZonaTrabajo getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }

    public boolean isAsignadaAJornada() {
        return asignadaAJornada;
    }

    public void setAsignadaAJornada(boolean asignadaAJornada) {
        this.asignadaAJornada = asignadaAJornada;
    }

    protected float tarifaBasePorZona() {
        if (zona == ZonaTrabajo.METROPOLITANA) {
            return 100.0f;
        }
        return 175.0f;
    }

    public boolean puedeAsignarse() {
        return estado == EstadoOrden.PENDIENTE && !asignadaAJornada;
    }

    public boolean esCompatibleConCuadrilla(Cuadrilla cuadrilla) {
        if (requiereCertificacionRefrigeracion()) {
            return cuadrilla.isCertRefrigeracion();
        }
        return true;
    }

    public float iniciarAtencion() {
        if (estado != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("La orden no se encuentra pendiente para iniciar atencion.");
        }
        estado = EstadoOrden.EN_ATENCION;
        if (visitasRealizadas >= 1) {
            return 40.0f;
        }
        return 0.0f;
    }

    public void registrarResultadoVisita(boolean completada) {
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new IllegalStateException("Solo se puede registrar el resultado de una orden que esta en atencion.");
        }
        visitasRealizadas++;
        asignadaAJornada = false;
        if (completada) {
            estado = EstadoOrden.COMPLETA;
        } else {
            if (visitasRealizadas >= maxVisitasPermitidas()) {
                estado = EstadoOrden.CANCELADA;
            } else {
                estado = EstadoOrden.PENDIENTE;
            }
        }
    }

    public abstract String categoria();

    public abstract int diasEstimadosAtencion();

    public abstract int maxVisitasPermitidas();

    public boolean requiereCertificacionRefrigeracion() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("Orden [%s] | Cat: %s | Cliente: %s | Zona: %s | Hrs Est.: %.2f | Estado: %s | Visitas: %d/%d | Costo Base: Q%.2f | Dias Est.: %d",
                id, categoria(), nombreCliente, zona, horasEstimadasTrabajo, estado, visitasRealizadas, maxVisitasPermitidas(), quote(), diasEstimadosAtencion());
    }
}