package thesis.master.model.arff;

import lombok.*;
import lombok.experimental.Wither;

import java.io.File;
import java.util.List;

@Wither
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ARFFDetails {
    private File arffFile;
    private List<String> classNames;
    private List<String> attributeNames;
}
