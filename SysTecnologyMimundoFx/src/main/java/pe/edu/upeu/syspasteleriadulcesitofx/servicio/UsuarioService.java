package pe.edu.upeu.syspasteleriadulcesitofx.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Usuario;
import pe.edu.upeu.syspasteleriadulcesitofx.repositorio.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository repo;

    public Usuario save(Usuario to) {
        return repo.save(to);
    }

    public List<Usuario> list() {
        return repo.findAll();
    }

    public Usuario update(Usuario to, Long id) {
        try {
            Usuario usuario = repo.findById(id).orElse(null);
            if (usuario != null) {
                usuario.setClave(to.getClave());
                usuario.setEstado(to.getEstado());
                usuario.setIdPerfil(to.getIdPerfil());
                usuario.setUser(to.getUser());
                return repo.save(usuario);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Usuario searchById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Usuario loginUsuario(String user, String clave) {
        return repo.loginUsuario(user, clave);
    }

}
