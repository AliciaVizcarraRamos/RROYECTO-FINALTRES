package pe.edu.upeu.syspasteleriadulcesitofx.servicio;

import pe.edu.upeu.syspasteleriadulcesitofx.dto.MenuMenuItenTO;

import java.util.List;
import java.util.Properties;

public interface MenuMenuItenDaoI {

    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma);

}
