package pe.edu.upeu.syspasteleriadulcesitofx.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.VentaDetalle;

@Repository
public interface VentaDetalleRepository  extends JpaRepository<VentaDetalle, Long> {
}
