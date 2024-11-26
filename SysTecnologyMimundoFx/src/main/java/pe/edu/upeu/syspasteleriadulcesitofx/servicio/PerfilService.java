package pe.edu.upeu.syspasteleriadulcesitofx.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Perfil;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ComboBoxOption;
import pe.edu.upeu.syspasteleriadulcesitofx.repositorio.PerfilRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PerfilService {
    @Autowired
    PerfilRepository repo;

    public Perfil save(Perfil to) {
        return repo.save(to);
    }

    public List<Perfil> list() {
        return repo.findAll();
    }

    public Perfil update(Perfil to, Long id) {
        try {
            Perfil toe = repo.findById(id).orElse(null);
            if (toe != null) {
                toe.setNombre(to.getNombre());
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

    public Perfil searchById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<ComboBoxOption> listarCombobox(){
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(Perfil cate : repo.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getIdPerfil()));
            cb.setValue(cate.getNombre());
            listar.add(cb);
        }
        return listar;
    }
}
