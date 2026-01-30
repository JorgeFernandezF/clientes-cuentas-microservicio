package com.cuentabancaria.infrastructure.out.persistence.adapter;

import com.cuentabancaria.application.out.CuentaRepositoryPort;
import com.cuentabancaria.domain.model.Cliente;
import com.cuentabancaria.domain.model.CuentaBancaria;
import com.cuentabancaria.infrastructure.out.persistence.jpa.ClienteEntity;
import com.cuentabancaria.infrastructure.out.persistence.jpa.CuentaBancariaEntity;
import com.cuentabancaria.infrastructure.out.persistence.mapper.PersistenceMapper;
import com.cuentabancaria.infrastructure.out.persistence.repo.CuentaBancariaJpaRepository;
import com.cuentabancaria.infrastructure.out.persistence.repo.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaBancariaJpaRepository repo;
    private final ClienteJpaRepository clienteRepo;

    //Crea una cuenta bancaria
    @Override
    public CuentaBancaria save(CuentaBancaria cuenta) {
        ClienteEntity clienteEntity = clienteRepo.getReferenceById(cuenta.getDniCliente());
        CuentaBancariaEntity entity = CuentaBancariaEntity.builder()
                .id(cuenta.getId())
                .tipoCuenta(cuenta.getTipoCuenta())
                .total(cuenta.getTotal())
                .cliente(clienteEntity)
                .build();

        CuentaBancariaEntity saved = repo.save(entity);
        // Devuelve dominio con DNI relleno independientemente de si existia antes o es nuevo
        return CuentaBancaria.builder()
                .id(saved.getId())
                .tipoCuenta(saved.getTipoCuenta())
                .total(saved.getTotal())
                .dniCliente(saved.getCliente() != null ? saved.getCliente().getDni() : null)
                .build();
    }


    @Override
    public Optional<CuentaBancaria> findById(Long id) {
        return repo.findById(id).map(ce -> {
            CuentaBancaria cuenta = PersistenceMapper.toDomainCuentaSinCliente(ce);

            // Si existe el cliente rellena el campo de DNI
            if (ce.getCliente() != null) {
                cuenta.setDniCliente(ce.getCliente().getDni());
            }

            return cuenta;
        });
    }
}