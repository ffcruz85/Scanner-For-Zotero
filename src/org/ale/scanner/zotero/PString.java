/** 
 * Copyright 2011 John M. Schanck
 * 
 * ScannerForZotero is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ScannerForZotero is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ScannerForZotero.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ale.scanner.zotero;

import android.os.Parcel;
import android.os.Parcelable;

public class PString implements Parcelable, CharSequence {

    String mValue;

    public PString(String s){
        mValue = s;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(mValue);
    }

    public String toString(){
        return mValue;
    }

    @Override
    public char charAt(int index) {
        return mValue.charAt(index);
    }

    @Override
    public int length() {
        return mValue.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return mValue.subSequence(start, end);
    }

    public static final Creator<PString> CREATOR = new Creator<PString>() {
        public PString createFromParcel(Parcel in) {
            return new PString(in.readString());
        }

        public PString[] newArray(int size) {
            return new PString[size];
        }
    };
}