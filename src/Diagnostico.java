public class Diagnostico extends Orden {

    public Diagnostico(String id, String nombreCliente, String direccion, float horasEstimadasTrabajo, ZonaTrabajo zona) {
        super(id, nombreCliente, direccion, horasEstimadasTrabajo, zona);
        if (horasEstimadasTrabajo > 1.5f) {
            throw new IllegalArgumentException("Las horas estimadas de un diagnostico no pueden superar 1.5 horas.");
        }
    }

    @Override
    public float quote() {
        if (getZona() == ZonaTrabajo.METROPOLITANA) {
            return 150.0f;
        }
        return 225.0f;
    }

    @Override
    public String categoria() {
        return "Diagnostico";
    }

    @Override
    public int diasEstimadosAtencion() {
        if (getZona() == ZonaTrabajo.METROPOLITANA) {
            return 1;
        }
        return 2;
    }

    @Override
    public int maxVisitasPermitidas() {
        return 3;
    }
}