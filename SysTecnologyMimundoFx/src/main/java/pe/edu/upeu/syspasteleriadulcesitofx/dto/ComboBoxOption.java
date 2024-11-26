package pe.edu.upeu.syspasteleriadulcesitofx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ComboBoxOption {
    private String key;
    private String value;


    @Override
    public String toString() {
        return value;
    }
}

