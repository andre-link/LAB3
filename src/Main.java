import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SistemaTecniHogar sistema = new SistemaTecniHogar();
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("          \n\nMENU PRINCIPAL\n\n           ");

            System.out.println("1. Cotizar y registrar orden de servicio");
            System.out.println("2. Registrar nueva cuadrilla");
            System.out.println("3. Gestionar jornadas y visitas");
            System.out.println("4. Consultar ordenes y cuadrillas");
            System.out.println("5. Generar reportes de gerencia");
            System.out.println("6. Salir");
            int opcion = leerEntero(scanner, "Seleccione una opcion: ");

            if (opcion == 1) {
                menuCotizarYRegistrarOrden(scanner, sistema);
            } else if (opcion == 2) {
                menuRegistrarCuadrilla(scanner, sistema);
            } else if (opcion == 3) {
                menuGestionarJornadas(scanner, sistema);
            } else if (opcion == 4) {
                menuConsultas(scanner, sistema);
            } else if (opcion == 5) {
                menuReportes(scanner, sistema);
            } else if (opcion == 6) {
                ejecutando = false;
                System.out.println("Saliendo del sistema TecniHogar...");
            } else {
                System.out.println("Error: Opcion no valida. Intente de nuevo.");
            }
        }
        scanner.close();
    }

    private static void menuCotizarYRegistrarOrden(Scanner scanner, SistemaTecniHogar sistema) {
        try {
            System.out.println("\n--- COTIZAR Y REGISTRAR ORDEN ---");
            System.out.println("Categorias disponibles:");
            System.out.println("1. Diagnostico");
            System.out.println("2. Instalacion");
            System.out.println("3. Instalacion Electrica Certificada");
            System.out.println("4. Mantenimiento de Aire Acondicionado");
            int cat = leerEntero(scanner, "Seleccione la categoria (1-4): ");
            if (cat < 1 || cat > 4) {
                System.out.println("Error: Categoria fuera de rango.");
                return;
            }

            String cliente = leerTexto(scanner, "Nombre del cliente: ");
            String direccion = leerTexto(scanner, "Direccion del servicio: ");
            float horas = leerFlotante(scanner, "Horas estimadas de trabajo: ");
            ZonaTrabajo zona = leerZona(scanner);

            int datoEntero = 0;
            float costoMateriales = 0.0f;

            if (cat == 2 || cat == 3) {
                datoEntero = leerEntero(scanner, "Cantidad de puntos a instalar: ");
            }
            if (cat == 3) {
                costoMateriales = leerFlotante(scanner, "Valor declarado de los materiales (Q): ");
            }
            if (cat == 4) {
                datoEntero = leerEntero(scanner, "Capacidad del equipo en BTU (9000 - 60000): ");
            }

            Orden cotizacionTemporal = sistema.crearInstanciaOrden(cat, "COT-TEMP", cliente, direccion, horas, zona, datoEntero, costoMateriales);

            System.out.println("\n--- RESULTADO DE LA COTIZACION ---");
            System.out.printf("Categoria:                   %s%n", cotizacionTemporal.categoria());
            System.out.printf("Costo total del servicio:    Q%.2f%n", cotizacionTemporal.quote());
            System.out.printf("Tiempo estimado de atencion: %d dias habiles%n", cotizacionTemporal.diasEstimadosAtencion());

            String respuesta = leerTexto(scanner, "¿El cliente acepta la cotizacion y desea registrar la orden? (S/N): ");
            if (respuesta.equalsIgnoreCase("S")) {
                String codigoOrden = leerTexto(scanner, "Ingrese el codigo unico para registrar la orden: ");
                Orden ordenDefinitiva = sistema.crearInstanciaOrden(cat, codigoOrden, cliente, direccion, horas, zona, datoEntero, costoMateriales);
                sistema.registrarOrdenAceptada(ordenDefinitiva);
                System.out.printf("Orden [%s] registrada con exito. Se cobro Q%.2f y quedo en estado PENDIENTE.%n",
                        ordenDefinitiva.getId(), ordenDefinitiva.quote());
            } else {
                System.out.println("Cotizacion finalizada sin registrar orden ni alterar ingresos.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion rechazada: " + e.getMessage());
        }
    }

    private static void menuRegistrarCuadrilla(Scanner scanner, SistemaTecniHogar sistema) {
        try {
            System.out.println("\n--- REGISTRAR CUADRILLA ---");
            String id = leerTexto(scanner, "Codigo de la cuadrilla: ");
            float capacidad = leerFlotante(scanner, "Capacidad maxima de horas por jornada: ");
            String certStr = leerTexto(scanner, "¿Esta certificada en refrigeracion? (S/N): ");
            boolean cert = certStr.equalsIgnoreCase("S");

            sistema.registrarCuadrilla(id, capacidad, cert);
            System.out.println("Cuadrilla registrada exitosamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operacion rechazada: " + e.getMessage());
        }
    }

    private static void menuGestionarJornadas(Scanner scanner, SistemaTecniHogar sistema) {
        boolean enSubmenu = true;
        while (enSubmenu) {
            System.out.println("\n--- GESTION DE JORNADAS Y VISITAS ---");
            System.out.println("1. Crear nueva jornada");
            System.out.println("2. Asignar orden a una jornada en preparacion");
            System.out.println("3. Retirar orden de una jornada en preparacion");
            System.out.println("4. Iniciar jornada");
            System.out.println("5. Registrar resultado de visita de una orden");
            System.out.println("6. Ver todas las jornadas");
            System.out.println("7. Volver al menu principal");
            int op = leerEntero(scanner, "Seleccione una opcion: ");

            try {
                if (op == 1) {
                    String idCuadrilla = leerTexto(scanner, "Ingrese el codigo de la cuadrilla: ");
                    Jornada nueva = sistema.crearJornada(idCuadrilla);
                    System.out.println("Jornada creada en preparacion: " + nueva);
                } else if (op == 2) {
                    int idJornada = leerEntero(scanner, "Numero de jornada: ");
                    String idOrden = leerTexto(scanner, "Codigo de la orden a asignar: ");
                    sistema.asignarOrdenAJornada(idJornada, idOrden);
                    System.out.println("Orden asignada correctamente a la jornada #" + idJornada);
                } else if (op == 3) {
                    int idJornada = leerEntero(scanner, "Numero de jornada: ");
                    String idOrden = leerTexto(scanner, "Codigo de la orden a retirar: ");
                    sistema.retirarOrdenDeJornada(idJornada, idOrden);
                    System.out.println("Orden retirada correctamente. Vuelve a estar disponible y PENDIENTE.");
                } else if (op == 4) {
                    int idJornada = leerEntero(scanner, "Numero de jornada a iniciar: ");
                    float cargosAdicionales = sistema.iniciarJornada(idJornada);
                    System.out.printf("Jornada #%d iniciada. Sus ordenes pasaron a EN_ATENCION. Cargos por visitas adicionales generados: Q%.2f%n",
                            idJornada, cargosAdicionales);
                } else if (op == 5) {
                    int idJornada = leerEntero(scanner, "Numero de jornada en curso: ");
                    String idOrden = leerTexto(scanner, "Codigo de la orden atendida: ");
                    System.out.println("Resultado de la visita: 1 = Completada | 2 = Visita Fallida");
                    int res = leerEntero(scanner, "Seleccione resultado (1 o 2): ");
                    if (res != 1 && res != 2) {
                        System.out.println("Operacion rechazada: Debe elegir 1 (Completada) o 2 (Visita Fallida).");
                    } else {
                        boolean completada = (res == 1);
                        sistema.registrarResultadoVisita(idJornada, idOrden, completada);
                        Orden o = sistema.buscarOrden(idOrden);
                        Jornada j = sistema.buscarJornada(idJornada);
                        System.out.println("Resultado registrado. Estado actual de la orden: " + o.getEstado());
                        System.out.println("Estado actual de la jornada #" + idJornada + ": " + j.getEstado());
                    }
                } else if (op == 6) {
                    List<Jornada> lista = sistema.getJornadas();
                    if (lista.isEmpty()) {
                        System.out.println("No hay jornadas registradas.");
                    } else {
                        for (Jornada j : lista) {
                            System.out.println(j);
                            for (Orden o : j.getOrdenes()) {
                                System.out.println("   -> " + o);
                            }
                        }
                    }
                } else if (op == 7) {
                    enSubmenu = false;
                } else {
                    System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operacion rechazada: " + e.getMessage());
            }
        }
    }

    private static void menuConsultas(Scanner scanner, SistemaTecniHogar sistema) {
        boolean enSubmenu = true;
        while (enSubmenu) {
            System.out.println("\n--- CONSULTAR ORDENES Y CUADRILLAS ---");
            System.out.println("1. Listar todas las ordenes");
            System.out.println("2. Buscar una orden por codigo");
            System.out.println("3. Listar todas las cuadrillas");
            System.out.println("4. Volver al menu principal");
            int op = leerEntero(scanner, "Seleccione una opcion: ");

            if (op == 1) {
                List<Orden> ordenes = sistema.getOrdenes();
                System.out.println("\n--- LISTADO DE ORDENES ---");
                for (Orden o : ordenes) {
                    System.out.println(o);
                }
            } else if (op == 2) {
                String codigo = leerTexto(scanner, "Ingrese el codigo de la orden: ");
                Orden o = sistema.buscarOrden(codigo);
                if (o == null) {
                    System.out.println("No se encontro ninguna orden con el codigo: " + codigo);
                } else {
                    System.out.println(o);
                }
            } else if (op == 3) {
                List<Cuadrilla> cuadrillas = sistema.getCuadrillas();
                System.out.println("\n--- LISTADO DE CUADRILLAS ---");
                for (Cuadrilla c : cuadrillas) {
                    System.out.println(c);
                }
            } else if (op == 4) {
                enSubmenu = false;
            } else {
                System.out.println("Opcion invalida.");
            }
        }
    }

    private static void menuReportes(Scanner scanner, SistemaTecniHogar sistema) {
        boolean enSubmenu = true;
        while (enSubmenu) {
            System.out.println("\n--- REPORTES DE GERENCIA ---");
            System.out.println("1. Cantidad de ordenes por categoria y por estado");
            System.out.println("2. Reporte de ingresos (totales y por categoria)");
            System.out.println("3. Jornadas no cerradas y disponibilidad");
            System.out.println("4. Mostrar todos los reportes juntos");
            System.out.println("5. Volver al menu principal");
            int op = leerEntero(scanner, "Seleccione una opcion: ");

            if (op == 1) {
                System.out.print("\n" + sistema.generarReporteConteoOrdenes());
            } else if (op == 2) {
                System.out.print("\n" + sistema.generarReporteIngresos());
            } else if (op == 3) {
                System.out.print("\n" + sistema.generarReporteJornadasAbiertas());
            } else if (op == 4) {
                System.out.print("\n" + sistema.generarReporteConteoOrdenes());
                System.out.print("\n" + sistema.generarReporteIngresos());
                System.out.print("\n" + sistema.generarReporteJornadasAbiertas());
            } else if (op == 5) {
                enSubmenu = false;
            } else {
                System.out.println("Opcion invalida.");
            }
        }
    }

    private static String leerTexto(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("Error: El texto no puede estar vacio.");
        }
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato incorrecto. Ingrese un numero entero valido.");
            }
        }
    }

    private static float leerFlotante(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Float.parseFloat(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato incorrecto. Ingrese un numero decimal valido.");
            }
        }
    }

    private static ZonaTrabajo leerZona(Scanner scanner) {
        while (true) {
            System.out.println("Seleccione la zona de trabajo: 1 = Metropolitana | 2 = Periferica");
            int op = leerEntero(scanner, "Zona (1 o 2): ");
            if (op == 1) {
                return ZonaTrabajo.METROPOLITANA;
            } else if (op == 2) {
                return ZonaTrabajo.PERIFERICA;
            } else {
                System.out.println("Error: Debe elegir 1 para Metropolitana o 2 para Periferica.");
            }
        }
    }
}