public class Instalacion extends Orden {
    private int puntosAInstalar;

    public Instalacion(String id, String nombreCliente, String direccion, float horasEstimadasTrabajo, ZonaTrabajo zona, int puntosAInstalar) {
        super(id, nombreCliente, direccion, horasEstimadasTrabajo, zona);
        if (puntosAInstalar <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos a instalar debe ser un entero positivo.");
        }
        this.puntosAInstalar = puntosAInstalar;
    }

    public int getPuntosAInstalar() {
        return puntosAInstalar;
    }

    public int calcularHorasFacturables() {
        float horasPorPuntos = puntosAInstalar * 0.75f;
        float mayorHoras = Math.max(getHorasEstimadasTrabajo(), horasPorPuntos);
        return (int) Math.ceil(mayorHoras);
    }

    @Override
    public float quote() {
        return tarifaBasePorZona() + (calcularHorasFacturables() * 90.0f);
    }

    @Override
    public String categoria() {
        return "Instalacion";
    }

    @Override
    public int diasEstimadosAtencion() {
        if (getZona() == ZonaTrabajo.METROPOLITANA) {
            return 3;
        }
        return 5;
    }

    @Override
    public int maxVisitasPermitidas() {
        return 3;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Puntos: %d | Hrs Facturables: %d", puntosAInstalar, calcularHorasFacturables());
    }
}