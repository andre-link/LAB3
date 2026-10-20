import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaTecniHogar {
    private List<Orden> ordenes;
    private List<Cuadrilla> cuadrillas;
    private List<Jornada> jornadas;
    private float ingresosPorRegistro;
    private float ingresosPorVisitasAdicionales;
    private Map<String, Float> ingresosPorCategoria;
    private int contadorJornadas;

    public SistemaTecniHogar() {
        this.ordenes = new ArrayList<>();
        this.cuadrillas = new ArrayList<>();
        this.jornadas = new ArrayList<>();
        this.ingresosPorRegistro = 0.0f;
        this.ingresosPorVisitasAdicionales = 0.0f;
        this.ingresosPorCategoria = new LinkedHashMap<>();
        this.contadorJornadas = 1;
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        registrarCuadrilla("CUA-01", 8.0f, true);
        registrarCuadrilla("CUA-02", 6.0f, false);
        registrarCuadrilla("CUA-03", 10.0f, true);

        agregarOrdenInicialSinIngreso(new Diagnostico("ORD-01", "Carlos Perez", "Zona 10, Ciudad", 1.0f, ZonaTrabajo.METROPOLITANA));
        agregarOrdenInicialSinIngreso(new Diagnostico("ORD-02", "Maria Lopez", "Carretera a El Salvador", 1.5f, ZonaTrabajo.PERIFERICA));

        agregarOrdenInicialSinIngreso(new Instalacion("ORD-03", "Jorge Ramirez", "Zona 15, Ciudad", 2.5f, ZonaTrabajo.METROPOLITANA, 5));
        agregarOrdenInicialSinIngreso(new Instalacion("ORD-04", "Ana Morales", "Mixco", 4.0f, ZonaTrabajo.PERIFERICA, 2));

        agregarOrdenInicialSinIngreso(new InstalacionCertificada("ORD-05", "Empresa XYZ", "Zona 4, Ciudad", 2.5f, ZonaTrabajo.METROPOLITANA, 5, 2000.0f));
        agregarOrdenInicialSinIngreso(new InstalacionCertificada("ORD-06", "Roberto Diaz", "Villa Nueva", 3.0f, ZonaTrabajo.PERIFERICA, 3, 1500.0f));

        agregarOrdenInicialSinIngreso(new MantenimientoAC("ORD-07", "Lucia Fernandez", "Zona 14, Ciudad", 2.0f, ZonaTrabajo.METROPOLITANA, 12000));
        agregarOrdenInicialSinIngreso(new MantenimientoAC("ORD-08", "Hotel Central", "Zona 1, Ciudad", 3.5f, ZonaTrabajo.METROPOLITANA, 36000));
    }

    private void agregarOrdenInicialSinIngreso(Orden orden) {
        ordenes.add(orden);
        if (!ingresosPorCategoria.containsKey(orden.categoria())) {
            ingresosPorCategoria.put(orden.categoria(), 0.0f);
        }
    }

    public Orden buscarOrden(String id) {
        for (Orden o : ordenes) {
            if (o.getId().equalsIgnoreCase(id)) {
                return o;
            }
        }
        return null;
    }

    public Cuadrilla buscarCuadrilla(String id) {
        for (Cuadrilla c : cuadrillas) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    public Jornada buscarJornada(int idJornada) {
        for (Jornada j : jornadas) {
            if (j.getIdJornada() == idJornada) {
                return j;
            }
        }
        return null;
    }

    public Orden crearInstanciaOrden(int tipoCategoria, String id, String cliente, String direccion, float horas, ZonaTrabajo zona, int datoEntero, float costoMateriales) {
        if (tipoCategoria == 1) {
            return new Diagnostico(id, cliente, direccion, horas, zona);
        } else if (tipoCategoria == 2) {
            return new Instalacion(id, cliente, direccion, horas, zona, datoEntero);
        } else if (tipoCategoria == 3) {
            return new InstalacionCertificada(id, cliente, direccion, horas, zona, datoEntero, costoMateriales);
        } else if (tipoCategoria == 4) {
            return new MantenimientoAC(id, cliente, direccion, horas, zona, datoEntero);
        } else {
            throw new IllegalArgumentException("Categoria de servicio invalida.");
        }
    }

    public void registrarOrdenAceptada(Orden nuevaOrden) {
        if (buscarOrden(nuevaOrden.getId()) != null) {
            throw new IllegalArgumentException("Ya existe una orden registrada con el codigo: " + nuevaOrden.getId());
        }
        float monto = nuevaOrden.quote();
        ordenes.add(nuevaOrden);
        ingresosPorRegistro += monto;
        String cat = nuevaOrden.categoria();
        ingresosPorCategoria.put(cat, ingresosPorCategoria.getOrDefault(cat, 0.0f) + monto);
    }

    public void registrarCuadrilla(String id, float capacidadHoras, boolean certRefrigeracion) {
        if (id != null && buscarCuadrilla(id.trim()) != null) {
            throw new IllegalArgumentException("Ya existe una cuadrilla con el codigo: " + id.trim());
        }
        Cuadrilla nueva = new Cuadrilla(id, capacidadHoras, certRefrigeracion);
        cuadrillas.add(nueva);
    }

    public Jornada crearJornada(String idCuadrilla) {
        Cuadrilla c = buscarCuadrilla(idCuadrilla);
        if (c == null) {
            throw new IllegalArgumentException("No existe una cuadrilla con el codigo: " + idCuadrilla);
        }
        for (Jornada j : jornadas) {
            if (j.getCuadrilla().getId().equalsIgnoreCase(c.getId()) && j.getEstado() != EstadoJornada.CERRADA) {
                throw new IllegalStateException("La cuadrilla ya tiene una jornada sin cerrar (Jornada #" + j.getIdJornada() + ").");
            }
        }
        Jornada nueva = new Jornada(contadorJornadas++, c);
        jornadas.add(nueva);
        return nueva;
    }

    public void asignarOrdenAJornada(int idJornada, String idOrden) {
        Jornada j = buscarJornada(idJornada);
        if (j == null) {
            throw new IllegalArgumentException("No existe la jornada #" + idJornada);
        }
        Orden o = buscarOrden(idOrden);
        if (o == null) {
            throw new IllegalArgumentException("No existe una orden con el codigo: " + idOrden);
        }
        j.asignarOrden(o);
    }

    public void retirarOrdenDeJornada(int idJornada, String idOrden) {
        Jornada j = buscarJornada(idJornada);
        if (j == null) {
            throw new IllegalArgumentException("No existe la jornada #" + idJornada);
        }
        j.retirarOrden(idOrden);
    }

    public float iniciarJornada(int idJornada) {
        Jornada j = buscarJornada(idJornada);
        if (j == null) {
            throw new IllegalArgumentException("No existe la jornada #" + idJornada);
        }
        List<Orden> ordenesIniciadas = j.iniciarJornada();
        float cargosAdicionalesJornada = 0.0f;
        for (Orden o : ordenesIniciadas) {
            float cargoVisita = o.iniciarAtencion();
            if (cargoVisita > 0) {
                ingresosPorVisitasAdicionales += cargoVisita;
                String cat = o.categoria();
                ingresosPorCategoria.put(cat, ingresosPorCategoria.getOrDefault(cat, 0.0f) + cargoVisita);
                cargosAdicionalesJornada += cargoVisita;
            }
        }
        return cargosAdicionalesJornada;
    }

    public void registrarResultadoVisita(int idJornada, String idOrden, boolean completada) {
        Jornada j = buscarJornada(idJornada);
        if (j == null) {
            throw new IllegalArgumentException("No existe la jornada #" + idJornada);
        }
        j.registrarResultadoOrden(idOrden, completada);
    }

    public List<Orden> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    public List<Cuadrilla> getCuadrillas() {
        return new ArrayList<>(cuadrillas);
    }

    public List<Jornada> getJornadas() {
        return new ArrayList<>(jornadas);
    }

    public String generarReporteConteoOrdenes() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE ORDENES POR CATEGORIA Y ESTADO ===\n");
        Map<String, Integer> porCategoria = new LinkedHashMap<>();
        for (String cat : ingresosPorCategoria.keySet()) {
            porCategoria.put(cat, 0);
        }
        for (Orden o : ordenes) {
            porCategoria.put(o.categoria(), porCategoria.getOrDefault(o.categoria(), 0) + 1);
        }
        sb.append("Por Categoria:\n");
        for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
            sb.append(String.format("  - %s: %d\n", entry.getKey(), entry.getValue()));
        }

        sb.append("Por Estado:\n");
        for (EstadoOrden est : EstadoOrden.values()) {
            int cuenta = 0;
            for (Orden o : ordenes) {
                if (o.getEstado() == est) {
                    cuenta++;
                }
            }
            sb.append(String.format("  - %s: %d\n", est.name(), cuenta));
        }
        return sb.toString();
    }

    public String generarReporteIngresos() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE INGRESOS ===\n");
        float total = ingresosPorRegistro + ingresosPorVisitasAdicionales;
        sb.append(String.format("Ingresos por registro de ordenes: Q%.2f\n", ingresosPorRegistro));
        sb.append(String.format("Ingresos por visitas adicionales: Q%.2f\n", ingresosPorVisitasAdicionales));
        sb.append(String.format("INGRESO TOTAL:                    Q%.2f\n", total));
        sb.append("Ingresos desglosados por categoria:\n");
        for (Map.Entry<String, Float> entry : ingresosPorCategoria.entrySet()) {
            sb.append(String.format("  - %s: Q%.2f\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

    public String generarReporteJornadasAbiertas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== JORNADAS NO CERRADAS ===\n");
        boolean hayAbiertas = false;
        for (Jornada j : jornadas) {
            if (j.getEstado() != EstadoJornada.CERRADA) {
                hayAbiertas = true;
                sb.append(String.format("Jornada #%d | Estado: %s | Cuadrilla: %s (Cert. Refrig: %s) | Horas Asignadas: %.2f | Capacidad Disponible: %.2f hrs\n",
                        j.getIdJornada(),
                        j.getEstado(),
                        j.getCuadrilla().getId(),
                        j.getCuadrilla().isCertRefrigeracion() ? "Si" : "No",
                        j.getHorasAsignadas(),
                        j.getCapacidadDisponible()));
                for (Orden o : j.getOrdenes()) {
                    sb.append(String.format("    -> Orden [%s] %s | %.2f hrs | Estado: %s\n",
                            o.getId(), o.categoria(), o.getHorasEstimadasTrabajo(), o.getEstado()));
                }
            }
        }
        if (!hayAbiertas) {
            sb.append("No hay jornadas abiertas en este momento.\n");
        }
        return sb.toString();
    }
}