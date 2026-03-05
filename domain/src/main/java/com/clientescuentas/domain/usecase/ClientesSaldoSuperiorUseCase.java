
package com.clientescuentas.domain.usecase;
import com.clientescuentas.domain.model.Cliente;
import java.util.*;

public interface ClientesSaldoSuperiorUseCase {
    List<Cliente> conSaldoSuperior(Double cantidad);
}
