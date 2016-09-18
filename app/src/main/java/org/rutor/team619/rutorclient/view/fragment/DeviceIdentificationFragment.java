package org.rutor.team619.rutorclient.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.service.DeviceInformationService;
import org.rutor.team619.rutorclient.view.fragment.core.InjectableFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;

/**
 * A placeholder fragment containing a simple view.
 */
public class DeviceIdentificationFragment extends InjectableFragment {

    private static final String TAG = DeviceIdentificationFragment.class.getName() + ":";

    @Bind(R.id.deviceIdText)
    TextView textView;
    @BindString(R.string.instructions_for_interfacing_devices)
    String message;

    @Inject
    DeviceInformationService deviceInformationService;


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.device_identification_fragment, container, false);
//    }

    @Override
    protected void onViewInjected() {
        textView.setText(String.format(message, deviceInformationService.id()));
    }

    @Override
    public int resolveLayoutId() {
        return R.layout.device_identification_fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }


}
