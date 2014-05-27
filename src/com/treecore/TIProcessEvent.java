package com.treecore;

import android.content.Intent;

public abstract interface TIProcessEvent {
	public abstract void processEvent(Intent paramIntent);
}