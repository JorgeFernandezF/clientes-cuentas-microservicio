package com.cuentabancaria.infrastructure.out.persistence.adapter;

import com.cuentabancaria.application.out.ClienteRepositoryPort;
import com.cuentabancaria.domain.model.Cliente;
import com.cuentabancaria.infrastructure.out.persistence.jpa.ClienteEntity;
import com.cuentabancaria.infrastructure.out.persistence.mapper.PersistenceMapper;
import com.cuentabancaria.infrastructure.out.persistence.repo.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository repo;

    //Obtiene todas las cuentas
    @Override
    public List<Cliente> findAllWithCuentas() {
        return repo.findAll().stream().map(PersistenceMapper::toDomain).toList();
    }

    //Obtiene un cliente a partir de un DNI
    @Override
    public Optional<Cliente> findByDniWithCuentas(String dni) {
        return repo.findByDni(dni).map(PersistenceMapper::toDomain);
    }

    //Crea un nuevo usuario
    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = PersistenceMapper.toEntity(cliente);
        ClienteEntity saved = repo.save(entity);
        return PersistenceMapper.toDomain(saved);
    }
}