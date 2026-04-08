package models;

public class PremiumMember extends Member {

    private double subscriptionFee;
    private boolean hasResearchAccess;

    public PremiumMember(String name, String email, String phone, double fee) {
        super(name, email, phone); // calling super class constructor
        this.subscriptionFee = fee;
        this.hasResearchAccess = true;
        this.maxBooksAllowed = 10; // override default
        this.memberType = "Premium";
    }

    @Override
    public String getMemberPrivileges() {
        return "Premium Member: Can borrow up to " + maxBooksAllowed +
                " books. Research Access: " + hasResearchAccess +
                ". Fee: $" + subscriptionFee;
    }

    // Additional method unique to PremiumMember
    public boolean hasResearchAccess() {
        return hasResearchAccess;
    }

    public double getSubscriptionFee() {
        return subscriptionFee;
    }
}
