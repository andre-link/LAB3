public class MantenimientoAC extends Orden {
    private int cantidadBTU;

    public MantenimientoAC(String id, String nombreCliente, String direccion, float horasEstimadasTrabajo, ZonaTrabajo zona, int cantidadBTU) {
        super(id, nombreCliente, direccion, horasEstimadasTrabajo, zona);
        if (zona != ZonaTrabajo.METROPOLITANA) {
            throw new IllegalArgumentException("Solo se aceptan mantenimientos de aire acondicionado en la zona metropolitana.");
        }
        if (cantidadBTU < 9000 || cantidadBTU > 60000) {
            throw new IllegalArgumentException("La capacidad del equipo debe estar entre 9,000 y 60,000 BTU.");
        }
        this.cantidadBTU = cantidadBTU;
    }

    public int getCantidadBTU() {
        return cantidadBTU;
    }

    @Override
    public float quote() {
        return tarifaBasePorZona() + (getHorasEstimadasTrabajo() * 110.0f) + 80.0f;
    }

    @Override
    public String categoria() {
        return "Mantenimiento de Aire Acondicionado";
    }

    @Override
    public int diasEstimadosAtencion() {
        return 2;
    }

    @Override
    public int maxVisitasPermitidas() {
        return 1;
    }

    @Override
    public boolean requiereCertificacionRefrigeracion() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Capacidad: %d BTU", cantidadBTU);
    }
}