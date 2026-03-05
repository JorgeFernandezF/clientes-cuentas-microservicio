package com.clientescuentas.unit;

import com.clientescuentas.application.service.CuentaService;
import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.CuentaBancaria;
import com.clientescuentas.domain.port.ClienteRepositoryPort;
import com.clientescuentas.domain.port.CuentaRepositoryPort;

import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaServiceTest {

    private final CuentaRepositoryPort cuentas = mock(CuentaRepositoryPort.class);
    private final ClienteRepositoryPort clientes = mock(ClienteRepositoryPort.class);
    private final CuentaService service = new CuentaService(cuentas, clientes);

    private Cliente cliente(String dni) {
        return Cliente.builder()
                .dni(dni)
                .nombre("Test")
                .apellido1("User")
                .apellido2("Mock")
                .fechaNacimiento(LocalDate.of(1990,1,1))
                .build();
    }

    private CuentaBancaria cuenta(String dni) {
        return CuentaBancaria.builder()
                .dniCliente(dni)
                .tipoCuenta("NORMAL")
                .total(100.0)
                .build();
    }

    @Test
    void guardar_creaCliente_siNoExiste() throws InstanceNotFoundException {

        var nueva = cuenta("999Z");

        when(clientes.findByDni("999Z")).thenReturn(Optional.empty());
        when(clientes.save(any())).thenAnswer(inv -> inv.getArgument(0));

        when(cuentas.save(any())).thenAnswer(inv -> {
            var c = inv.getArgument(0, CuentaBancaria.class);
            return CuentaBancaria.builder()
                    .id(10L)
                    .dniCliente(c.getDniCliente())
                    .tipoCuenta(c.getTipoCuenta())
                    .total(c.getTotal())
                    .build();
        });

        var result = service.guardar(nueva);

        assertEquals(10L, result.getId());
        verify(clientes).findByDni("999Z");
        verify(clientes).save(any());
        verify(cuentas).save(any());
    }

    @Test
    void guardar_noCreaCliente_siExiste() throws InstanceNotFoundException {

        var nueva = cuenta("111A");

        when(clientes.findByDni("111A")).thenReturn(Optional.of(cliente("111A")));

        when(cuentas.save(any())).thenAnswer(inv -> {
            var c = inv.getArgument(0, CuentaBancaria.class);
            return CuentaBancaria.builder()
                    .id(20L)
                    .dniCliente(c.getDniCliente())
                    .tipoCuenta(c.getTipoCuenta())
                    .total(c.getTotal())
                    .build();
        });

        var result = service.guardar(nueva);

        assertEquals(20L, result.getId());
        verify(clientes).findByDni("111A");
        verify(clientes, never()).save(any());
        verify(cuentas).save(any());
    }

    @Test
    void buscarPorId_devuelveCuenta_siExiste() throws InstanceNotFoundException {
        when(cuentas.findById(5L))
                .thenReturn(Optional.of(CuentaBancaria.builder().id(5L).build()));

        var result = service.buscarPorId(5L);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
        verify(cuentas).findById(5L);
        verifyNoInteractions(clientes);
    }

    @Test
    void buscarPorId_lanzaInstanceNotFound_siNoExiste() {
        when(cuentas.findById(999L)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class,
                () -> service.buscarPorId(999L));

        verify(cuentas).findById(999L);
        verifyNoInteractions(clientes);
    }
}