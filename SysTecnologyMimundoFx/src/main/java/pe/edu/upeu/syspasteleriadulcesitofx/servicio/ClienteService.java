package pe.edu.upeu.syspasteleriadulcesitofx.servicio;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ModeloDataAutocomplet;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Cliente;
import pe.edu.upeu.syspasteleriadulcesitofx.repositorio.ClienteRepository;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository repo;

    Logger logger= LoggerFactory.getLogger(ClienteService.class);

    public Cliente save(Cliente cliente) {
        return repo.save(cliente);
    }

    public List<Cliente> list() {
        return repo.findAll();
    }

    public Cliente update(Cliente cliente, String dniruc) {
        Cliente clienteExistente = repo.findById(dniruc).orElse(null);
        if (clienteExistente != null) {
            clienteExistente.setNombres(cliente.getNombres());
            clienteExistente.setRepLegal(cliente.getRepLegal());
            clienteExistente.setTipoDocumento(cliente.getTipoDocumento());
            return repo.save(clienteExistente);
        }
        return null;
    }

    public void delete(String dniruc) {
        repo.deleteById(dniruc);
    }

    public Cliente searchById(String dniruc) {
        return repo.findById(dniruc).orElse(null);
    }

    public List<ModeloDataAutocomplet> listAutoCompletCliente() {
        List<ModeloDataAutocomplet> listarclientes = new ArrayList<>();
        try {
            for (Cliente cliente : repo.findAll()) {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(cliente.getDniruc());
                data.setNameDysplay(cliente.getNombres());
                data.setOtherData(cliente.getTipoDocumento());
                listarclientes.add(data);
            }
        } catch (Exception e) {
            logger.error("Error durante la operación", e);
        }
        return listarclientes;
    }
}