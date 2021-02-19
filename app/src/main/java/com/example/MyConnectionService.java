package com.example;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MyConnectionService extends ConnectionService {
    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {

        return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
    }
}
