package thesis.master.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NavigationNode {

    private String id;

    private Position position;

    private List<Pattern> patterns;

}
