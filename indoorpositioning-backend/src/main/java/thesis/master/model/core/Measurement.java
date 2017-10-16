package thesis.master.model.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Measurement {

    private String transmitterId;

    private Integer rssi;

}
