package library;

public class Member {

private String memberID;
private String name;
private String contactNo;
private String memberType;

public Member(String memberID, String name, String contactNo, String memberType) {
    this.memberID = memberID;
    this.name = name;
    this.contactNo = contactNo;
    this.memberType = memberType;
}

public String getMemberID() {
    return memberID;
}

public String getName() {
    return name;
}

public String getContactNo() {
    return contactNo;
}

public String getMemberType() {
    return memberType;
}

public void setName(String name) {
    this.name = name;
}

public void setContactNo(String contactNo) {
    this.contactNo = contactNo;
}

public void setMemberType(String memberType) {
    this.memberType = memberType;
}

public String toString() {
    return memberID + " - " + name + " (" + memberType + ")";
}
}