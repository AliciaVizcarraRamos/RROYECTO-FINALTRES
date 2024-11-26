package pe.edu.upeu.syspasteleriadulcesitofx.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.CompraDetalle;

@Repository
public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, Long> {
}
