package pe.edu.upeu.syspasteleriadulcesitofx.control;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.*;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ComboBoxOption;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Usuario;
import pe.edu.upeu.syspasteleriadulcesitofx.servicio.PerfilService;
import pe.edu.upeu.syspasteleriadulcesitofx.servicio.UsuarioService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class UsuarioController {

    @FXML
    TextField txtUsuario, txtClave, txtFiltroDato;
    @FXML
    ComboBox<ComboBoxOption> cbxEstado;
    @FXML
    ComboBox<ComboBoxOption> cbxPerfil;
    @FXML
    private TableView<Usuario> tableView;
    @FXML
    Label lbnMsg;
    @FXML
    private AnchorPane miUsuario;
    Stage stage;

    @Autowired
    UsuarioService us;

    @Autowired
    PerfilService perfilService;

    private Validator validator;
    ObservableList<Usuario> listarUsuario;
    Usuario formulario;
    Long idUsuarioCE = 0L;

    public void initialize() {

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), event -> {
            stage = (Stage) miUsuario.getScene().getWindow();
            if (stage != null) {
                System.out.println("El título del stage es: " + stage.getTitle());
            } else {
                System.out.println("Stage aún no disponible.");
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();

        cbxPerfil.setTooltip(new Tooltip("Selecciona un perfil"));
        cbxPerfil.getItems().addAll(perfilService.listarCombobox());

        cbxEstado.setTooltip(new Tooltip("Selecciona un estado"));
        cbxEstado.getItems().addAll(
                new ComboBoxOption("Activo", "Activo"),
                new ComboBoxOption("Inactivo", "Inactivo"));

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Configuración de la tabla
        TableViewHelper<Usuario> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idUsuario", 60.0));
        columns.put("Usuario", new ColumnInfo("user", 150.0));
        columns.put("Estado", new ColumnInfo("estado", 100.0));
        columns.put("Perfil", new ColumnInfo("idPerfil.nombre", 150.0));

        Consumer<Usuario> updateAction = (Usuario usuario) -> {
            System.out.println("Actualizar: " + usuario);
            editForm(usuario);
        };
        Consumer<Usuario> deleteAction = (Usuario usuario) -> {
            us.delete(usuario.getIdUsuario());
            double with = stage.getWidth() / 1.5;
            double h = stage.getHeight() / 2;
            Toast.showToast(stage, "Se eliminó correctamente!!", 2000, with, h);
            listar();
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
        listar();
    }

    public void listar() {
        try {
            tableView.getItems().clear();
            listarUsuario = FXCollections.observableArrayList(us.list());
            tableView.getItems().addAll(listarUsuario);

            // Listener para filtrar usuarios
            txtFiltroDato.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarUsuarios(newValue);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void limpiarError() {
        txtUsuario.getStyleClass().remove("text-field-error");
        txtClave.getStyleClass().remove("text-field-error");
        cbxEstado.getStyleClass().remove("text-field-error");
        cbxPerfil.getStyleClass().remove("text-field-error");
    }

    public void clearForm() {
        txtUsuario.setText("");
        txtClave.setText("");
        cbxEstado.getSelectionModel().select(null);
        cbxPerfil.getSelectionModel().select(null);
        idUsuarioCE = 0L;
        limpiarError();
    }

    @FXML
    public void cancelarAccion() {
        clearForm();
        limpiarError();
    }

    public void validarCampos(List<ConstraintViolation<Usuario>> violaciones) {
        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();

        for (ConstraintViolation<Usuario> violacion : violaciones) {
            String campo = violacion.getPropertyPath().toString();
            if (campo.equals("user")) {
                erroresOrdenados.put("user", violacion.getMessage());
                txtUsuario.getStyleClass().add("text-field-error");
            } else if (campo.equals("clave")) {
                erroresOrdenados.put("clave", violacion.getMessage());
                txtClave.getStyleClass().add("text-field-error");
            } else if (campo.equals("estado")) {
                erroresOrdenados.put("estado", violacion.getMessage());
                cbxEstado.getStyleClass().add("text-field-error");
            } else if (campo.equals("idPerfil")) {
                erroresOrdenados.put("idPerfil", violacion.getMessage());
                cbxPerfil.getStyleClass().add("text-field-error");
            }
        }

        Map.Entry<String, String> primerError = erroresOrdenados.entrySet().iterator().next();
        lbnMsg.setText(primerError.getValue());
        lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    }

    @FXML
    public void validarFormulario() {
        formulario = new Usuario();
        formulario.setUser(txtUsuario.getText());
        formulario.setClave(txtClave.getText());

        String estado = cbxEstado.getSelectionModel().getSelectedItem() == null ? "0"
                : cbxEstado.getSelectionModel().getSelectedItem().getKey();
        formulario.setEstado(estado);
        // Asignar el perfil seleccionado
        String perfilId = cbxPerfil.getSelectionModel().getSelectedItem() == null ? "0"
                : cbxPerfil.getSelectionModel().getSelectedItem().getKey();
        formulario.setIdPerfil(perfilService.searchById(Long.parseLong(perfilId)));

        Set<ConstraintViolation<Usuario>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<Usuario>> violacionesOrdenadas = violaciones.stream()
                .sorted((v1, v2) -> v1.getPropertyPath().toString().compareTo(v2.getPropertyPath().toString()))
                .collect(Collectors.toList());

        if (violacionesOrdenadas.isEmpty()) {
            lbnMsg.setText("Formulario válido");
            lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
            limpiarError();
            double with = stage.getWidth() / 1.5;
            double h = stage.getHeight() / 2;

            if (idUsuarioCE != 0L && idUsuarioCE>0L) {
                formulario.setIdUsuario(idUsuarioCE);
                us.update(formulario, idUsuarioCE);
                Toast.showToast(stage, "Se actualizó correctamente!!", 2000, with, h);
            } else {
                us.save(formulario);
                Toast.showToast(stage, "Se guardó correctamente!!", 2000, with, h);
            }
            clearForm();
            listar();
        } else {
            validarCampos(violacionesOrdenadas);
        }
    }

    private void filtrarUsuarios(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tableView.getItems().clear();
            tableView.getItems().addAll(listarUsuario);
        } else {
            String lowerCaseFilter = filtro.toLowerCase();
            List<Usuario> usuariosFiltrados = listarUsuario.stream()
                    .filter(usuario -> usuario.getUser().toLowerCase().contains(lowerCaseFilter) ||
                            usuario.getEstado().toLowerCase().contains(lowerCaseFilter))
                    .collect(Collectors.toList());

            tableView.getItems().clear();
            tableView.getItems().addAll(usuariosFiltrados);
        }
    }

    public void editForm(Usuario usuario) {
        txtUsuario.setText(usuario.getUser());
        txtClave.setText(usuario.getClave());
        cbxEstado.getSelectionModel().select(
                cbxEstado.getItems().stream()
                        .filter(doc -> doc.getKey().equalsIgnoreCase(usuario.getEstado().trim()))
                        .findFirst()
                        .orElse(null));

        cbxPerfil.getSelectionModel().select(
                cbxPerfil.getItems().stream()
                        .filter(perfil -> perfil.getKey().equals(usuario.getIdPerfil().getIdPerfil().toString()))
                        .findFirst()
                        .orElse(null));

        idUsuarioCE = usuario.getIdUsuario();
        limpiarError();
    }

}
