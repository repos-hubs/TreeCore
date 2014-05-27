package com.treecore.db2.sqlcrypt;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IContentObserver extends IInterface {
	public abstract void onChange(boolean paramBoolean) throws RemoteException;

	public static abstract class Stub extends Binder implements
			IContentObserver {
		private static final String DESCRIPTOR = "com.treecore.db2.sqlcrypt.IContentObserver";
		static final int TRANSACTION_onChange = 1;

		public Stub() {
			attachInterface(this, "com.treecore.db2.sqlcrypt.IContentObserver");
		}

		public static IContentObserver asInterface(IBinder obj) {
			if (obj == null) {
				return null;
			}
			IInterface iin = obj
					.queryLocalInterface("com.treecore.db2.sqlcrypt.IContentObserver");
			if ((iin != null) && ((iin instanceof IContentObserver))) {
				return (IContentObserver) iin;
			}
			return new Proxy(obj);
		}

		public IBinder asBinder() {
			return this;
		}

		public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
				throws RemoteException {
			switch (code) {
			case 1598968902:
				reply.writeString("com.treecore.db2.sqlcrypt.IContentObserver");
				return true;
			case 1:
				data.enforceInterface("com.treecore.db2.sqlcrypt.IContentObserver");

				boolean _arg0 = data.readInt() != 0;
				onChange(_arg0);
				return true;
			}

			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements IContentObserver {
			private IBinder mRemote;

			Proxy(IBinder remote) {
				this.mRemote = remote;
			}

			public IBinder asBinder() {
				return this.mRemote;
			}

			public String getInterfaceDescriptor() {
				return "com.treecore.db2.sqlcrypt.IContentObserver";
			}

			public void onChange(boolean selfUpdate) throws RemoteException {
				Parcel _data = Parcel.obtain();
				try {
					_data.writeInterfaceToken("com.treecore.db2.sqlcrypt.IContentObserver");
					_data.writeInt(selfUpdate ? 1 : 0);
					this.mRemote.transact(1, _data, null, 1);
				} finally {
					_data.recycle();
				}
			}
		}
	}
}