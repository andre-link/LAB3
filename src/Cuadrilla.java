public class Cuadrilla {
    private String id;
    private float capacidadHorasPorJornada;
    private boolean certRefrigeracion;

    public Cuadrilla(String id, float capacidadHorasPorJornada, boolean certRefrigeracion) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la cuadrilla no puede estar vacio.");
        }
        if (capacidadHorasPorJornada <= 0) {
            throw new IllegalArgumentException("La capacidad de horas por jornada debe ser un valor positivo.");
        }
        this.id = id.trim();
        this.capacidadHorasPorJornada = capacidadHorasPorJornada;
        this.certRefrigeracion = certRefrigeracion;
    }

    public String getId() {
        return id;
    }

    public float getCapacidadHorasPorJornada() {
        return capacidadHorasPorJornada;
    }

    public boolean isCertRefrigeracion() {
        return certRefrigeracion;
    }

    @Override
    public String toString() {
        return String.format("Cuadrilla [%s] | Capacidad: %.2f hrs | Cert. Refrigeracion: %s",
                id, capacidadHorasPorJornada, certRefrigeracion ? "Si" : "No");
    }
}