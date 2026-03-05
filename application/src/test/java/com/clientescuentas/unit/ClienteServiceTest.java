package com.clientescuentas.unit;

import com.clientescuentas.application.service.ClienteService;
import com.clientescuentas.domain.model.Cliente;
import com.clientescuentas.domain.model.PageQuery;
import com.clientescuentas.domain.model.PageResult;
import com.clientescuentas.domain.port.ClienteRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    private final ClienteRepositoryPort repo = mock(ClienteRepositoryPort.class);
    private final ClienteService service = new ClienteService(repo);

    private Cliente cliente(String dni) {
        return Cliente.builder()
                .dni(dni)
                .nombre("Test")
                .apellido1("User")
                .apellido2("Mock")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .cuentas(List.of())
                .build();
    }

    private PageResult<Cliente> pageResult(List<Cliente> content, int page, int size,
                                           long total, int totalPages, String sort) {
        return new PageResult<>(content, page, size, total, totalPages, sort);
    }

    @Test
    @DisplayName("porDni devuelve Cliente si existe")
    void porDni_devuelveCliente_siExiste() throws InstanceNotFoundException {
        when(repo.findByDni("111A")).thenReturn(Optional.of(cliente("111A")));

        var result = service.porDni("111A");

        assertEquals("111A", result.getDni());

        verify(repo).findByDni("111A");
        verifyNoMoreInteractions(repo);
    }

    @Test
    @DisplayName("porDni lanza InstanceNotFoundException si no existe")
    void porDni_lanzaInstanceNotFound_siNoExiste() {
        when(repo.findByDni("XXXXXX")).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> service.porDni("XXXXXX"));

        verify(repo).findByDni("XXXXXX");
        verifyNoMoreInteractions(repo);
    }

    @Test
    @DisplayName("listar (paginación real) delega al repo y devuelve PageResult")
    void listar_paginacionReal_OK() {
        var c1 = cliente("111A");
        var c2 = cliente("222B");

        when(repo.findAllPaged(new PageQuery(0, 2, "dni,asc")))
                .thenReturn(pageResult(List.of(c1, c2), 0, 2, 5, 3, "dni,asc"));

        var result = service.listar(0, 2, "dni,asc");

        assertEquals(0, result.page());
        assertEquals(2, result.size());
        assertEquals(5, result.totalElements());
        assertEquals(3, result.totalPages());

        assertEquals("111A", result.content().get(0).getDni());
        assertEquals("222B", result.content().get(1).getDni());

        verify(repo).findAllPaged(new PageQuery(0, 2, "dni,asc"));
        verifyNoMoreInteractions(repo);
    }

    @Test
    @DisplayName("mayoresPaged delega al repo y devuelve PageResult filtrado")
    void mayoresPaged_OK() {
        var c1 = cliente("111A");
        var c2 = cliente("333C");

        when(repo.findMayoresDeEdadPaged(new PageQuery(0, 10, "dni,asc")))
                .thenReturn(pageResult(List.of(c1, c2), 0, 10, 2, 1, "dni,asc"));

        var result = service.mayoresPaged(0, 10, "dni,asc");

        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals("111A", result.content().get(0).getDni());
        assertEquals("333C", result.content().get(1).getDni());

        verify(repo).findMayoresDeEdadPaged(new PageQuery(0, 10, "dni,asc"));
        verifyNoMoreInteractions(repo);
    }


    @Test
    @DisplayName("conSaldoSuperiorPaged delega al repo y devuelve PageResult filtrado por cantidad")
    void conSaldoSuperiorPaged_OK() {
        var c1 = cliente("111A");

        when(repo.findConSaldoSuperiorAPaged(1000.0, new PageQuery(0, 10, "dni,asc")))
                .thenReturn(pageResult(List.of(c1), 0, 10, 1, 1, "dni,asc"));

        var result = service.conSaldoSuperiorPaged(1000.0, 0, 10, "dni,asc");

        assertEquals(1, result.totalElements());
        assertEquals("111A", result.content().get(0).getDni());

        verify(repo).findConSaldoSuperiorAPaged(1000.0, new PageQuery(0, 10, "dni,asc"));
        verifyNoMoreInteractions(repo);
    }
}