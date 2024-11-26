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
import pe.edu.upeu.syspasteleriadulcesitofx.componente.ColumnInfo;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.TableViewHelper;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.Toast;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ModeloDataAutocomplet;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Proveedor;
import pe.edu.upeu.syspasteleriadulcesitofx.repositorio.ProveedorRepository;
import pe.edu.upeu.syspasteleriadulcesitofx.servicio.ProveedorService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ProveedorController {

    @Autowired
    ProveedorRepository repo;

    @FXML
    TextField txtNombreProveedor;
    @FXML
    private TableView<Proveedor> tabla;
    @FXML
    Label lbnMsg;
    @FXML
    private AnchorPane esclavo;

    @Autowired
    ProveedorService cS;
    Stage stage;

    Proveedor formulario;
    ObservableList<Proveedor> listarProveedor;

    private Validator validator;

    public void initialize() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), event -> {
            stage = (Stage) esclavo.getScene().getWindow();
            if (stage != null) {
                System.out.println("El título del stage es: " + stage.getTitle());
            } else {
                System.out.println("Stage aún no disponible.");
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Crear instancia de la clase genérica TableViewHelper
        TableViewHelper<Proveedor> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Nombre del Proveedor:", new ColumnInfo("nombres", 200.0)); // Columna visible "Columna 2" mapea al campo
        columns.put("Tipo de Documento", new ColumnInfo("tipoDocumento", 150.0)); // Columna visible "Columna 2" mapea
        columns.put("Numero", new ColumnInfo("dniruc", 100.0)); // Columna visible "Columna 2" mapea al campo
        columns.put("Rep. Legal", new ColumnInfo("repLegal", 200.0)); // Columna visible "Columna 2" mapea al campo

        Consumer<Proveedor> updateAction = (Proveedor proveedor) -> {
            System.out.println("Actualizar: " + proveedor);
            editForm(proveedor);
        };
        Consumer<Proveedor> deleteAction = (Proveedor proveedor) -> {
            System.out.println("Eliminar: " + proveedor);
            cS.delete(Long.valueOf(proveedor.getIdProveedor())); // eliminar el proveedor
            double with = stage.getWidth() / 1.5;
            double h = stage.getHeight() / 2;
            Toast.showToast(stage, "Se eliminó correctamente!!", 2000, with, h);
            listar();
        };

        tableViewHelper.addColumnsInOrderWithSize(tabla, columns, updateAction, deleteAction);
        tabla.setTableMenuButtonVisible(true);
        listar();
    }

    public void listar() {
        try {
            tabla.getItems().clear();
            listarProveedor = FXCollections.observableArrayList(cS.list());
            tabla.getItems().addAll(listarProveedor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void limpiarError() {
        tabla.getStyleClass().remove("text-field-error");
    }

    public void clearForm() {
        txtNombreProveedor.setText("");
        limpiarError();
    }

    @FXML
    public void cancelarAccion() {
        clearForm();
        limpiarError();
    }

    void validarCampos(List<ConstraintViolation<Proveedor>> violacionesOrdenadasPorPropiedad) {
        // Crear un LinkedHashMap para ordenar las violaciones
        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        // Mostrar el primer mensaje de error
        for (ConstraintViolation<Proveedor> violacion : violacionesOrdenadasPorPropiedad) {
            String campo = violacion.getPropertyPath().toString();
            if (campo.equals("nombres")) {
                // Puedes agregar la lógica para los campos si es necesario
            }
        }
        // Mostrar el primer error en el orden deseado
        Map.Entry<String, String> primerError = erroresOrdenados.entrySet().iterator().next();
        lbnMsg.setText(primerError.getValue()); // Mostrar el mensaje del primer error
        lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    }

    @FXML
    public void validarFormulario() {
        formulario = new Proveedor();
        Long.parseLong(txtNombreProveedor.getText());
        //formulario.setIdProveedor(Long.valueOf(txtNombreProveedor.getText().isEmpty() ? "0" : txtNombreProveedor.getText()));

        // Verificar si el texto ingresado en el campo es un número (para el ID o algún otro campo numérico)
        String inputText = txtNombreProveedor.getText();
        if (isNumeric(inputText)) {
            Long id = Long.valueOf(inputText);

            // Si se encuentra un proveedor con el ID, actualizarlo
            if (cS.searchById(id) != null) {
                cS.update(formulario, id);
                showToast("Se actualizó correctamente!!");
            } else {
                // Si no existe, guardar uno nuevo
                cS.save(formulario);
                showToast("Se guardó correctamente!!");
            }
        } else {
            // Si el texto no es un número, mostrar un mensaje de error
            lbnMsg.setText("Por favor ingrese un ID numérico válido.");
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        }

        // Refrescar la lista de proveedores
        listar();
    }

    // Método para verificar si el texto es numérico
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);  // Intentar convertir el texto a Long
            return true;
        } catch (NumberFormatException e) {
            return false;  // Si ocurre una excepción, no es numérico
        }
    }

    // Método para mostrar el Toast de manera más eficiente
    private void showToast(String message) {
        double width = stage.getWidth() / 1.5;
        double height = stage.getHeight() / 2;
        Toast.showToast(stage, message, 2000, width, height);
    }

    private void filtrarProveedor(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            // Si el filtro está vacío, volver a mostrar la lista completa
            tabla.getItems().clear();
            tabla.getItems().addAll(listarProveedor);
        } else {
            // Aplicar el filtro
            String lowerCaseFilter = filtro.toLowerCase();
            List<Proveedor> clientesFiltrados = listarProveedor.stream()
                    .filter(proveedor -> proveedor.getNombresRaso().toLowerCase().contains(lowerCaseFilter))
                    .collect(Collectors.toList());

            // Actualizar los items del TableView con los productos filtrados
            tabla.getItems().clear();
            tabla.getItems().addAll(clientesFiltrados);
        }
    }

    public void editForm(Proveedor proveedor) {
        txtNombreProveedor.setText(String.valueOf(proveedor.getIdProveedor()));
        limpiarError();
    }

}
