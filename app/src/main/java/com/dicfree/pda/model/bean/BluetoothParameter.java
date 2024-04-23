/**
 * Copyright 2024 Wuhan Haici Technology Co., Ltd 
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dicfree.pda.model.bean;

public class BluetoothParameter {
	//是否配对
	private boolean  isPair = false;
	private String bluetoothName;
	private String bluetoothMac;
	private String bluetoothStrength;/*蓝牙强度*/
	public String getBluetoothName() {
		return bluetoothName;
	}

	public void setBluetoothName(String bluetoothName) {
		this.bluetoothName = bluetoothName;
	}

	public String getBluetoothMac() {
		return bluetoothMac;
	}

	public void setBluetoothMac(String bluetoothMac) {
		this.bluetoothMac = bluetoothMac;
	}

	public String getBluetoothStrength() {
		return bluetoothStrength;
	}

	public void setBluetoothStrength(String bluetoothStrength) {
		this.bluetoothStrength = bluetoothStrength;
	}

	public boolean isPair() {
		return isPair;
	}

	public void setPair(boolean pair) {
		isPair = pair;
	}
}
