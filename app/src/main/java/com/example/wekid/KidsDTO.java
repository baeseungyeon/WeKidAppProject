package com.example.wekid;

import android.os.Parcel;
import android.os.Parcelable;

// 아이 정보 담는 클래스
// 객체 자체를 전달하기 위해 implements Parcelable
public class KidsDTO implements Parcelable {
    private String identifier;
    private String name;
    private String birth;
    private String address;
    private String kinderName;
    private String className;
    private String teacherId;
    private String parentsId;

    public KidsDTO() {}

    protected KidsDTO(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        birth = in.readString();
        address = in.readString();
        kinderName = in.readString();
        className = in.readString();
        teacherId = in.readString();
        parentsId = in.readString();
    }

    public static final Creator<KidsDTO> CREATOR = new Creator<KidsDTO>() {
        @Override
        public KidsDTO createFromParcel(Parcel in) {
            return new KidsDTO(in);
        }

        @Override
        public KidsDTO[] newArray(int size) {
            return new KidsDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(birth);
        dest.writeString(address);
        dest.writeString(kinderName);
        dest.writeString(className);
        dest.writeString(teacherId);
        dest.writeString(parentsId);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKinderName() {
        return kinderName;
    }

    public void setKinderName(String kinderName) {
        this.kinderName = kinderName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getParentsId() {
        return parentsId;
    }

    public void setParentsId(String parentsId) {
        this.parentsId = parentsId;
    }
}
