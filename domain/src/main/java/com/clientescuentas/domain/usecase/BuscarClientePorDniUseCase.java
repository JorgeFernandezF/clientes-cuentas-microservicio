
package com.clientescuentas.domain.usecase;
import com.clientescuentas.domain.model.Cliente;

import javax.management.InstanceNotFoundException;

public interface BuscarClientePorDniUseCase {
    Cliente porDni(String dni) throws InstanceNotFoundException;
}
