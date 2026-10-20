public class InstalacionCertificada extends Instalacion {
    private float costoMateriales;

    public InstalacionCertificada(String id, String nombreCliente, String direccion, float horasEstimadasTrabajo, ZonaTrabajo zona, int puntosAInstalar, float costoMateriales) {
        super(id, nombreCliente, direccion, horasEstimadasTrabajo, zona, puntosAInstalar);
        if (costoMateriales <= 0) {
            throw new IllegalArgumentException("El valor declarado de los materiales debe ser positivo.");
        }
        this.costoMateriales = costoMateriales;
    }

    public float getCostoMateriales() {
        return costoMateriales;
    }

    @Override
    public float quote() {
        return super.quote() + 120.0f + (0.05f * costoMateriales);
    }

    @Override
    public String categoria() {
        return "Instalacion Electrica Certificada";
    }

    @Override
    public int diasEstimadosAtencion() {
        return super.diasEstimadosAtencion() + 2;
    }

    @Override
    public int maxVisitasPermitidas() {
        return 2;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Materiales: Q%.2f", costoMateriales);
    }
}