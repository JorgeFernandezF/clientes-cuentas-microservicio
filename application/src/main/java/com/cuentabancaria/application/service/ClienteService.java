package com.cuentabancaria.application.service;

import com.cuentabancaria.application.in.*;
import com.cuentabancaria.application.out.ClienteRepositoryPort;
import com.cuentabancaria.domain.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService implements 
        ObtenerClientesUseCase,
        ObtenerClientePorDniUseCase,
        ObtenerMayoresDeEdadUseCase,
        ObtenerClientesConSaldoSuperiorUseCase {

    private final ClienteRepositoryPort clientes;

    //Busqueda de todos los clientes
    @Override
    public List<Cliente> handle() {
        return clientes.findAllWithCuentas();
    }

    //Busqueda de cliente por dni
    @Override
    public Cliente handle(String dni) throws InstanceNotFoundException {
        return clientes.findByDniWithCuentas(dni)
                .orElseThrow(() -> new InstanceNotFoundException("Cliente no encontrado: " + dni));
    }

    //Búsqueda de clientes mayores a 18 años
    @Override
    public List<Cliente> handleMayores() {
        return clientes.findAllWithCuentas().stream()
                .filter(c -> c.getEdad() >= 18)
                .toList();
    }

    //Busca los clientes con la cantidad mayor a la indicada
    @Override
    public List<Cliente> handle(double cantidad) {
        return clientes.findAllWithCuentas().stream()
                .filter(c -> c.getSumaTotalCuentas() > cantidad)
                .toList();
    }
}
