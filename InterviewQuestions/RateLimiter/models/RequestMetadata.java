package InterviewQuestions.RateLimiter.models;

public class RequestMetadata {
    private String userId;
    private String userIp;
    private String resourcePath;
    private int tokenRequested;

    public RequestMetadata(String userId, String userIp, String resourcePath, int tokenRequested) {
        this.userId = userId;
        this.userIp = userIp;
        this.resourcePath = resourcePath;
        this.tokenRequested = tokenRequested;
    }

    public int getTokenRequested() {
        return tokenRequested;
    }

    public void setTokenRequested(int tokenRequested) {
        this.tokenRequested = tokenRequested;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
