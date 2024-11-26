package pe.edu.upeu.syspasteleriadulcesitofx.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ModeloDataAutocomplet;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Cliente;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Proveedor;
import pe.edu.upeu.syspasteleriadulcesitofx.repositorio.ProveedorRepository;

import java.util.ArrayList;
import java.util.List;

import static javax.swing.plaf.synth.Region.LIST;

@Service
public class ProveedorService {
    @Autowired
    ProveedorRepository repo;
    Logger logger= LoggerFactory.getLogger(ClienteService.class);


    public Proveedor save(Proveedor to) {
        return repo.save(to);
    }

    public List<Proveedor> list() {
        return repo.findAll();
    }

    public Proveedor update(Proveedor to, Long id) {
        try {
            Proveedor toe = repo.findById(id).orElse(null);
            if (toe != null) {
                toe.setDniRuc(to.getDniRuc());
                return repo.save(toe);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Proveedor searchById(Long id) {
        return repo.findById(id).orElse(null);
    }
    public List<ModeloDataAutocomplet> listAutoCompletProveedor() {
        List<ModeloDataAutocomplet> listarProveedor = new ArrayList<>();
        try {
            for ( Proveedor proveedor : repo.findAll()) {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(proveedor.getNombresRaso());
                listarProveedor.add(data);
            }
        } catch (Exception e) {
            logger.error("Error durante la operación", e);
        }
        return listarProveedor;
    }
}

