/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectoprogra;

import proyectoprogra.model.Viaje;
import proyectoprogra.service.ViajeService;
import java.util.Date;

/**
 *
 * @author rjrob
 */
public class ProyectoProgra {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Demo rápido del modelo y servicio Viaje
        ViajeService service = new ViajeService();

        Viaje v1 = new Viaje(1, "San José", "Alajuela", new Date(System.currentTimeMillis() + 3600_000),
                new Date(System.currentTimeMillis() + 7200_000), Viaje.ESTADO_PENDIENTE);
        Viaje v2 = new Viaje(2, "Cartago", "Heredia", new Date(System.currentTimeMillis() + 10_800_000),
                new Date(System.currentTimeMillis() + 14_400_000), Viaje.ESTADO_EN_CURSO);

        service.create(v1);
        service.create(v2);

        System.out.println("Viajes registrados:");
        for (Viaje v : service.listAll()) {
            System.out.println(v);
        }
    }
    
}
