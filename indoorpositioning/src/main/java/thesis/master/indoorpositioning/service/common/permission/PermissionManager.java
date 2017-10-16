package thesis.master.indoorpositioning.service.common.permission;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java8.util.stream.StreamSupport;

public class PermissionManager {
    public static final List<String> LOCATION_PERMISSIONS = Collections.singletonList(Manifest.permission.ACCESS_COARSE_LOCATION);
    public static final List<String> STORAGE_PERMISSIONS = Collections.singletonList(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private final RxPermissions rxPermissions;
    private final Activity activity;

    public static PermissionManager getInstance(Activity activity) {
        return new PermissionManager(activity);
    }

    private PermissionManager(Activity activity) {
        this.rxPermissions = new RxPermissions(activity);
        this.activity = activity;
    }

    @SafeVarargs
    public final void request(List<String>... permissionGroups) {
        String[] distinctPermissions = StreamSupport.stream(Arrays.asList(permissionGroups))
                .flatMap(StreamSupport::stream)
                .distinct()
                .toArray(String[]::new);

        rxPermissions
                .requestEach(distinctPermissions)
                .takeUntil(permission -> !permission.granted)
                .subscribe(permission -> {
                    if (permission.granted) {
                        Log.i("PERMISSIONS", "Permission " + permission.name + " granted for class " + this.getClass().getSimpleName());
                    } else {
                        Toast.makeText(activity, "Required permission not granted", Toast.LENGTH_SHORT).show();
                        throw new IllegalArgumentException("Required permission not granted");
                    }
                });
    }
}
