package models;

public final class StudentMember extends Member { // 'final' - cannot be subclassed further

    private String studentId;
    private String institution;
    private int semester;

    public StudentMember(String name, String email, String phone,
            String studentId, String institution, int semester) {
        super(name, email, phone);
        this.studentId = studentId;
        this.institution = institution;
        this.semester = semester;
        this.maxBooksAllowed = 5;
        this.memberType = "Student";
    }

    @Override
    public String getMemberPrivileges() {
        return "Student Member (" + institution + "): Can borrow up to " +
                maxBooksAllowed + " books. Semester: " + semester;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getInstitution() {
        return institution;
    }

    public int getSemester() {
        return semester;
    }
}
