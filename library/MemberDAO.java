package library;

import java.sql.*;
import java.util.ArrayList;

public class MemberDAO {

    public void addMember(Member m) throws LibraryException {

        String sql = "INSERT INTO member (memberID, name, contactNo, memberType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, m.getMemberID());
            pstm.setString(2, m.getName());
            pstm.setString(3, m.getContactNo());
            pstm.setString(4, m.getMemberType());

            pstm.executeUpdate();

        } catch (SQLException e) {
            throw new LibraryException("Cannot add member. The Member ID may already exist.", e);
        }
    }

    public void updateMember(Member m) throws LibraryException {

        String sql = "UPDATE member SET name = ?, contactNo = ?, memberType = ? WHERE memberID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, m.getName());
            pstm.setString(2, m.getContactNo());
            pstm.setString(3, m.getMemberType());
            pstm.setString(4, m.getMemberID());

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new LibraryException("No member found with ID " + m.getMemberID());
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot update member " + m.getMemberID(), e);
        }
    }

    public void deleteMember(String memberID) throws LibraryException {

    String checkSql = "SELECT recordID FROM borrowrecord WHERE memberID = ? AND returnStatus = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstm = conn.prepareStatement(checkSql)) {

        pstm.setString(1, memberID);
        pstm.setString(2, SharedSpec.STATUS_BORROWED);

        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            throw new LibraryException("Cannot delete member " + memberID + " because they still have book(s) on loan.");
        }

    } catch (SQLException e) {
        throw new LibraryException("Cannot check member's borrow records.", e);
    }

    String deleteSql = "DELETE FROM member WHERE memberID = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstm = conn.prepareStatement(deleteSql)) {

        pstm.setString(1, memberID);

        int rows = pstm.executeUpdate();

        if (rows == 0) {
            throw new LibraryException("No member found with ID " + memberID);
        }

    } catch (SQLException e) {
        throw new LibraryException("Cannot delete member " + memberID, e);
    }
}

    public ArrayList<Member> getAllMembers() throws LibraryException {

        ArrayList<Member> list = new ArrayList<Member>();

        String sql = "SELECT memberID, name, contactNo, memberType FROM member";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {

                String memberID = rs.getString("memberID");
                String name = rs.getString("name");
                String contactNo = rs.getString("contactNo");
                String memberType = rs.getString("memberType");

                Member m = new Member(memberID, name, contactNo, memberType);
                list.add(m);
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot read the member list.", e);
        }

        return list;
    }

    public ArrayList<Member> searchMembers(String criteria, String keyword) throws LibraryException {

        String column;

        if (SharedSpec.SEARCH_BY_NAME.equals(criteria)) {
            column = "name";
        } else if (SharedSpec.SEARCH_BY_MEMBER_ID.equals(criteria)) {
            column = "memberID";
        } else {
            throw new LibraryException("Unknown search criteria: " + criteria);
        }

        String sql = "SELECT memberID, name, contactNo, memberType FROM member WHERE " + column + " LIKE ?";

        ArrayList<Member> list = new ArrayList<Member>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, "%" + keyword + "%");

            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                String id = rs.getString("memberID");
                String name = rs.getString("name");
                String contactNo = rs.getString("contactNo");
                String memberType = rs.getString("memberType");

                list.add(new Member(id, name, contactNo, memberType));
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot search members.", e);
        }

        return list;
    }

    public ArrayList<Member> sortMembers(ArrayList<Member> list, String criteria) {

        ArrayList<Member> copy = new ArrayList<Member>(list);

        for (int i = 0; i < copy.size() - 1; i++) {
            int smallest = i;

            for (int j = i + 1; j < copy.size(); j++) {
                if (comesFirst(copy.get(j), copy.get(smallest), criteria)) {
                    smallest = j;
                }
            }

            Member firstMember = copy.get(i);
            Member smallestMember = copy.get(smallest);

            copy.remove(smallest);
            copy.add(smallest, firstMember);

            copy.remove(i);
            copy.add(i, smallestMember);
        }

    return copy;
}

    private boolean comesFirst(Member a, Member b, String criteria) {

        if (SharedSpec.SORT_BY_NAME.equals(criteria)) {
            return a.getName().compareTo(b.getName()) < 0;
        }

        if (SharedSpec.SORT_BY_MEMBER_ID.equals(criteria)) {
            return a.getMemberID().compareTo(b.getMemberID()) < 0;
        }

        return false;
    }

    public Member findMemberByID(String memberID) throws LibraryException {

        String sql = "SELECT memberID, name, contactNo, memberType FROM member WHERE memberID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, memberID);

            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {

                String id = rs.getString("memberID");
                String name = rs.getString("name");
                String contactNo = rs.getString("contactNo");
                String memberType = rs.getString("memberType");

                return new Member(id, name, contactNo, memberType);
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot search for member " + memberID, e);
        }

        return null;
    }
}