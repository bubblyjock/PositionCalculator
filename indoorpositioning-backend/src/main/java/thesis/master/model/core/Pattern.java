package thesis.master.model.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Pattern {

    private List<Measurement> measurements;

}
