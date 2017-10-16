package thesis.master.indoorpositioning.service.position.signalbased.converter;

import android.support.annotation.NonNull;

import java.util.List;

import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;

public interface SignalScanResultConverter<T, R extends SignalScanResult> {

    @NonNull
    List<R> convert(List<T> scanResults);

}
