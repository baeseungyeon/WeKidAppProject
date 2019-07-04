package com.example.wekid;

import android.os.Parcel;
import android.os.Parcelable;

public class BeaconDTO {
    private String kidsName;
    private String kidsClassName;

    public BeaconDTO() {}

    protected BeaconDTO(Parcel in) {
        kidsName = in.readString();
        kidsClassName = in.readString();
    }

    public static final Parcelable.Creator<BeaconDTO> CREATOR = new Parcelable.Creator<BeaconDTO>() {
        @Override
        public BeaconDTO createFromParcel(Parcel in) {
            return new BeaconDTO(in);
        }

        @Override
        public BeaconDTO[] newArray(int size) {
            return new BeaconDTO[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kidsName);
        dest.writeString(kidsClassName);
    }

    public String getKidsName() {
        return kidsName;
    }

    public void setKidsName(String name) {
        this.kidsName = kidsName;
    }

    public String getKidsClassName() {
        return kidsClassName;
    }

    public void setKidsClassName(String kidsClassName) {
        this.kidsClassName = kidsClassName;
    }
}
