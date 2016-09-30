//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.vending.billing;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IInAppBillingService extends IInterface {
    int isBillingSupported(int var1, String var2, String var3) throws RemoteException;

    Bundle getSkuDetails(int var1, String var2, String var3, Bundle var4) throws RemoteException;

    Bundle getBuyIntent(int var1, String var2, String var3, String var4, String var5) throws RemoteException;

    Bundle getPurchases(int var1, String var2, String var3, String var4) throws RemoteException;

    int consumePurchase(int var1, String var2, String var3) throws RemoteException;

    public abstract static class Stub extends Binder implements IInAppBillingService {
        private static final String DESCRIPTOR = "com.android.vending.billing.IInAppBillingService";
        static final int TRANSACTION_isBillingSupported = 1;
        static final int TRANSACTION_getSkuDetails = 2;
        static final int TRANSACTION_getBuyIntent = 3;
        static final int TRANSACTION_getPurchases = 4;
        static final int TRANSACTION_consumePurchase = 5;

        public Stub() {
            this.attachInterface(this, "com.android.vending.billing.IInAppBillingService");
        }

        public static IInAppBillingService asInterface(IBinder obj) {
            if(obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("com.android.vending.billing.IInAppBillingService");
                return (IInAppBillingService)(iin != null && iin instanceof IInAppBillingService?(IInAppBillingService)iin:new IInAppBillingService.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int _arg0;
            String _arg1;
            String _arg2;
            int _result;
            Bundle _result1;
            String _result3;
            switch(code) {
                case 1:
                    data.enforceInterface("com.android.vending.billing.IInAppBillingService");
                    _arg0 = data.readInt();
                    _arg1 = data.readString();
                    _arg2 = data.readString();
                    _result = this.isBillingSupported(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface("com.android.vending.billing.IInAppBillingService");
                    _arg0 = data.readInt();
                    _arg1 = data.readString();
                    _arg2 = data.readString();
                    Bundle _result5;
                    if(0 != data.readInt()) {
                        _result5 = (Bundle)Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _result5 = null;
                    }

                    _result1 = this.getSkuDetails(_arg0, _arg1, _arg2, _result5);
                    reply.writeNoException();
                    if(_result1 != null) {
                        reply.writeInt(1);
                        _result1.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }

                    return true;
                case 3:
                    data.enforceInterface("com.android.vending.billing.IInAppBillingService");
                    _arg0 = data.readInt();
                    _arg1 = data.readString();
                    _arg2 = data.readString();
                    _result3 = data.readString();
                    String _result4 = data.readString();
                    Bundle _result2 = this.getBuyIntent(_arg0, _arg1, _arg2, _result3, _result4);
                    reply.writeNoException();
                    if(_result2 != null) {
                        reply.writeInt(1);
                        _result2.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }

                    return true;
                case 4:
                    data.enforceInterface("com.android.vending.billing.IInAppBillingService");
                    _arg0 = data.readInt();
                    _arg1 = data.readString();
                    _arg2 = data.readString();
                    _result3 = data.readString();
                    _result1 = this.getPurchases(_arg0, _arg1, _arg2, _result3);
                    reply.writeNoException();
                    if(_result1 != null) {
                        reply.writeInt(1);
                        _result1.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }

                    return true;
                case 5:
                    data.enforceInterface("com.android.vending.billing.IInAppBillingService");
                    _arg0 = data.readInt();
                    _arg1 = data.readString();
                    _arg2 = data.readString();
                    _result = this.consumePurchase(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 1598968902:
                    reply.writeString("com.android.vending.billing.IInAppBillingService");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IInAppBillingService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "com.android.vending.billing.IInAppBillingService";
            }

            public int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                int _result;
                try {
                    _data.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
                    _data.writeInt(apiVersion);
                    _data.writeString(packageName);
                    _data.writeString(type);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                Bundle _result;
                try {
                    _data.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
                    _data.writeInt(apiVersion);
                    _data.writeString(packageName);
                    _data.writeString(type);
                    if(skusBundle != null) {
                        _data.writeInt(1);
                        skusBundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }

                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    if(0 != _reply.readInt()) {
                        _result = (Bundle)Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                Bundle _result;
                try {
                    _data.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
                    _data.writeInt(apiVersion);
                    _data.writeString(packageName);
                    _data.writeString(sku);
                    _data.writeString(type);
                    _data.writeString(developerPayload);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if(0 != _reply.readInt()) {
                        _result = (Bundle)Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                Bundle _result;
                try {
                    _data.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
                    _data.writeInt(apiVersion);
                    _data.writeString(packageName);
                    _data.writeString(type);
                    _data.writeString(continuationToken);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    if(0 != _reply.readInt()) {
                        _result = (Bundle)Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                int _result;
                try {
                    _data.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
                    _data.writeInt(apiVersion);
                    _data.writeString(packageName);
                    _data.writeString(purchaseToken);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }
        }
    }
}
