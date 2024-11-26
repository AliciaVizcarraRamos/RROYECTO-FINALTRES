package pe.edu.upeu.syspasteleriadulcesitofx.control;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.ColumnInfo;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.ComboBoxAutoComplete;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.TableViewHelper;
import pe.edu.upeu.syspasteleriadulcesitofx.componente.Toast;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ComboBoxOption;
import pe.edu.upeu.syspasteleriadulcesitofx.dto.ModeloDataAutocomplet;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Cliente;
import pe.edu.upeu.syspasteleriadulcesitofx.modelo.Producto;
import pe.edu.upeu.syspasteleriadulcesitofx.repositorio.ClienteRepository;
import pe.edu.upeu.syspasteleriadulcesitofx.servicio.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ClienteController {

    @Autowired
    ClienteRepository repo;

    @FXML
    TextField numDNI, nombreAP, Repe, Filt;
    @FXML
    private TableView<Cliente> tabla;
    @FXML
    Label lbnMsg;
    @FXML
    ComboBox<ComboBoxOption> cbxDOCU;

    @FXML
    private AnchorPane esclavo;

    @Autowired
    ClienteService cS;
    Stage stage;

    ModeloDataAutocomplet lastProducto;
    Cliente formulario;
    ObservableList<Cliente> listarCliente;

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

        cbxDOCU.setTooltip(new Tooltip());
        cbxDOCU.getItems().addAll(
                new ComboBoxOption("DNI", "Documento Nacional de Identidad"),
                new ComboBoxOption("RUC", "Registro Único de Contribuyentes"));
        cbxDOCU.setOnAction(event -> {
            ComboBoxOption selectedClient = cbxDOCU.getSelectionModel().getSelectedItem();
            if (selectedClient != null) {
                String selectedId = selectedClient.getKey(); // Obtener el ID
                System.out.println("ID del cleinte seleccionado: " + selectedId);
            }
        });
        new ComboBoxAutoComplete<>(cbxDOCU);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Crear instancia de la clase genérica TableViewHelper
        TableViewHelper<Cliente> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("Nombre completo", new ColumnInfo("nombres", 200.0)); // Columna visible "Columna 2" mapea al campo
                                                                          // "campo2"
        columns.put("Tipo de Documento", new ColumnInfo("tipoDocumento", 150.0)); // Columna visible "Columna 2" mapea
                                                                                  // al campo "campo2"
        columns.put("Numero", new ColumnInfo("dniruc", 100.0)); // Columna visible "Columna 2" mapea al campo
                                                                // "campo2"
        columns.put("Rep. Legal", new ColumnInfo("repLegal", 200.0)); // Columna visible "Columna 2" mapea al campo

        Consumer<Cliente> updateAction = (Cliente cliente) -> {
            System.out.println("Actualizar: " + cliente);
            editForm(cliente);
        };
        Consumer<Cliente> deleteAction = (Cliente cliente) -> {
            System.out.println("Actualizar: " + cliente);
            cS.delete(cliente.getDniruc()); /* deletePerson(usuario); */
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
            listarCliente = FXCollections.observableArrayList(cS.list());
            tabla.getItems().addAll(listarCliente);
            // Agregar un listener al campo de texto txtFiltroDato para filtrar los
            // productos
            Filt.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarClientes(newValue);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void limpiarError() {
        numDNI.getStyleClass().remove("text-field-error");
        nombreAP.getStyleClass().remove("text-field-error");
        Repe.getStyleClass().remove("text-field-error");
        cbxDOCU.getStyleClass().remove("text-field-error");
    }

    public void clearForm() {
        numDNI.setText("");
        nombreAP.setText("");
        Repe.setText("");
        cbxDOCU.getSelectionModel().select(null);
        limpiarError();
    }

    @FXML
    public void cancelarAccion() {
        clearForm();
        limpiarError();
    }

    void validarCampos(List<ConstraintViolation<Cliente>> violacionesOrdenadasPorPropiedad) {
        // Crear un LinkedHashMap para ordenar las violaciones
        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        // Mostrar el primer mensaje de error
        for (ConstraintViolation<Cliente> violacion : violacionesOrdenadasPorPropiedad) {
            String campo = violacion.getPropertyPath().toString();
            if (campo.equals("nombres")) {
                erroresOrdenados.put("nombres", violacion.getMessage());
                nombreAP.getStyleClass().add("text-field-error");
            } else if (campo.equals("rep_legal")) {
                erroresOrdenados.put("rep_legal", violacion.getMessage());
                Repe.getStyleClass().add("text-field-error");
            } else if (campo.equals("dniruc")) {
                erroresOrdenados.put("dniruc", violacion.getMessage());
                numDNI.getStyleClass().add("text-field-error");
            } else if (campo.equals("tipoDocumento")) {
                erroresOrdenados.put("tipoDocumento", violacion.getMessage());
                cbxDOCU.getStyleClass().add("text-field-error");
            }
        }
        // Mostrar el primer error en el orden deseado
        Map.Entry<String, String> primerError = erroresOrdenados.entrySet().iterator().next();
        lbnMsg.setText(primerError.getValue()); // Mostrar el mensaje del primer error
        lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    }

    @FXML
    public void validarFormulario() {
        formulario = new Cliente();
        formulario.setNombres(nombreAP.getText());
        formulario.setRepLegal(Repe.getText() == "" ? "0" : Repe.getText());
        formulario.setDniruc(numDNI.getText() == "" ? "0" : numDNI.getText());
        String idxM = cbxDOCU.getSelectionModel().getSelectedItem() == null ? "0"
                : cbxDOCU.getSelectionModel().getSelectedItem().getKey();
        formulario.setTipoDocumento(idxM);
        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(formulario);
        // Si prefieres ordenarlo por el nombre de la propiedad que violó la
        // restricción, podrías usar:
        List<ConstraintViolation<Cliente>> violacionesOrdenadasPorPropiedad = violaciones.stream()
                .sorted((v1, v2) -> v1.getPropertyPath().toString().compareTo(v2.getPropertyPath().toString()))
                .collect(Collectors.toList());
        if (violacionesOrdenadasPorPropiedad.isEmpty()) {
            // Los datos son válidos
            lbnMsg.setText("Formulario válido");
            lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
            limpiarError();
            double with = stage.getWidth() / 1.5;
            double h = stage.getHeight() / 2;

            if (cS.searchById(numDNI.getText()) != null) {
                cS.update(formulario, numDNI.getText());
                Toast.showToast(stage, "Se actualizó correctamente!!", 2000, with, h);
                clearForm();
            } else {
                cS.save(formulario);
                Toast.showToast(stage, "Se guardo correctamente!!", 2000, with, h);
                clearForm();
            }
            listar();
        } else {
            validarCampos(violacionesOrdenadasPorPropiedad);
        }
    }

    private void filtrarClientes(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            // Si el filtro está vacío, volver a mostrar la lista completa
            tabla.getItems().clear();
            tabla.getItems().addAll(listarCliente);
        } else {
            // Aplicar el filtro
            String lowerCaseFilter = filtro.toLowerCase();
            List<Cliente> clientesFiltrados = listarCliente.stream()
                    .filter(cliente -> {
                        // Verificar si el filtro coincide con alguno de los campos
                        if (cliente.getNombres().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(cliente.getRepLegal()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(cliente.getDniruc()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (cliente.getTipoDocumento().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false; // Si no coincide con ningún campo
                    })
                    .collect(Collectors.toList());
            // Actualizar los items del TableView con los productos filtrados
            tabla.getItems().clear();
            tabla.getItems().addAll(clientesFiltrados);
        }
    }

    public void editForm(Cliente cliente) {
        nombreAP.setText(cliente.getNombres());
        Repe.setText(cliente.getRepLegal());
        numDNI.setText(cliente.getDniruc());
        // Seleccionar el ítem en cbxDOCU según el ID de Marca
        cbxDOCU.getSelectionModel().select(
                cbxDOCU.getItems().stream()
                        .filter(doc -> doc.getKey().equalsIgnoreCase(cliente.getTipoDocumento().trim()))
                        .findFirst()
                        .orElse(null));
        limpiarError();
    }

}
